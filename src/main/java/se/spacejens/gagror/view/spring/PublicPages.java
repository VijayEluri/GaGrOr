package se.spacejens.gagror.view.spring;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import se.spacejens.gagror.controller.LoginFailedException;
import se.spacejens.gagror.controller.MayNotBeLoggedInException;
import se.spacejens.gagror.controller.RequestContext;
import se.spacejens.gagror.controller.ServiceCommunicationException;
import se.spacejens.gagror.controller.helper.RepeatedPasswordNotMatchingException;
import se.spacejens.gagror.model.user.User;
import se.spacejens.gagror.model.user.UserCreationException;

/**
 * Spring controller for all public web pages (i.e. pages not requiring the user
 * to be logged in).
 * 
 * @author spacejens
 */
@Controller
@RequestMapping(SpringRequestMappings.PUBLIC)
public class PublicPages extends SpringViewSupport {

	/**
	 * The index page is the first page seen by visitors. It provides
	 * functionality to log in and a link to register new users. Finally, it
	 * also displays some general information about the system.
	 * 
	 * @param request
	 *            HTTP request.
	 * @return View to the index page.
	 */
	@RequestMapping(value = SpringRequestMappings.PUBLIC_INDEX)
	public ModelAndView index(final HttpServletRequest request) {
		return new Work() {
			@Override
			protected ModelAndView doWork(final RequestContext rc) throws LoginFailedException, ServiceCommunicationException {
				this.getLog().debug("Serving index page");
				final ModelAndView mav = new ModelAndView("index");
				mav.getModel().put("loginForm", new LoginForm());
				return mav;
			}
		}.process(request);

	}

	/**
	 * This page is displayed when the user is logged out.
	 * 
	 * @param request
	 *            HTTP request.
	 * @return View to the logged out page.
	 */
	@RequestMapping(value = SpringRequestMappings.PUBLIC_LOGGEDOUT)
	public ModelAndView logout(final HttpServletRequest request) {
		return new Work() {
			@Override
			protected ModelAndView doWork(final RequestContext rc) throws LoginFailedException, ServiceCommunicationException {
				this.getLog().debug("Serving logged out page");
				// TODO Use service to log out?
				PublicPages.this.setLoggedInUser(null, request.getSession());
				final ModelAndView mav = new ModelAndView("login");
				mav.getModel().put("headline", "Logged Out");
				mav.getModel().put("message", "Log in again");
				mav.getModel().put("loginForm", new LoginForm());
				return mav;
			}
		}.process(request);
	}

	/**
	 * This page is viewed when the user was not logged in as expected.
	 * 
	 * @param request
	 *            HTTP request.
	 * @return View to the login page.
	 */
	@RequestMapping(value = SpringRequestMappings.PUBLIC_NOTLOGGEDIN, method = RequestMethod.GET)
	public ModelAndView notLoggedIn(final HttpServletRequest request) {
		return new Work() {
			@Override
			protected ModelAndView doWork(final RequestContext rc) throws LoginFailedException, ServiceCommunicationException {
				final ModelAndView mav = new ModelAndView("login");
				mav.getModel().put("headline", "You Were Not Logged In");
				mav.getModel().put("message", "Please log in and try again");
				mav.getModel().put("loginForm", new LoginForm());
				return mav;
			}
		}.process(request);
	}

	/**
	 * View dedicated login page.
	 * 
	 * @param request
	 *            HTTP request.
	 * @return View to the login page.
	 */
	@RequestMapping(value = SpringRequestMappings.PUBLIC_LOGIN, method = RequestMethod.GET)
	public ModelAndView login(final HttpServletRequest request) {
		return new Work() {
			@Override
			protected ModelAndView doWork(final RequestContext rc) throws LoginFailedException, ServiceCommunicationException {
				final ModelAndView mav = new ModelAndView("login");
				mav.getModel().put("headline", "Log In");
				mav.getModel().put("message", "Enter your login credentials");
				mav.getModel().put("loginForm", new LoginForm());
				return mav;
			}
		}.process(request);
	}

	/**
	 * Handle posting of login form.
	 * 
	 * @param request
	 *            HTTP request.
	 * @param loginForm
	 *            The posted form contents.
	 * @param result
	 *            Result of data binding.
	 * @return View to login failed page (with error messages) if failed,
	 *         redirect to system dashboard if successful.
	 * @throws LoginFailedException
	 * @throws ServiceCommunicationException
	 */
	@RequestMapping(value = SpringRequestMappings.PUBLIC_LOGIN, method = RequestMethod.POST)
	public ModelAndView postLoginForm(final HttpServletRequest request, @Valid final LoginForm loginForm, final BindingResult result) {
		return new Work() {
			@Override
			protected ModelAndView doWork(final RequestContext rc) throws LoginFailedException, ServiceCommunicationException {
				this.getLog().debug("Login form posted");
				if (result.hasErrors()) {
					throw new LoginFailedException();
				}
				final User user = PublicPages.this.getLoginService().loginUser(rc, loginForm.getUsername(), loginForm.getPassword());
				PublicPages.this.setLoggedInUser(user, request.getSession());
				// Framework adds login form and binding result automatically
				return new ModelAndView(new RedirectView(rc.getContextPath() + SpringRequestMappings.DASHBOARD
						+ SpringRequestMappings.DASHBOARD_INDEX));
			}
		}.process(request);
	}

	/**
	 * This page provides a form used for registering a new user.
	 * 
	 * @return View to the registration form.
	 */
	@RequestMapping(value = SpringRequestMappings.PUBLIC_REGISTER, method = RequestMethod.GET)
	public ModelAndView registerFormGet(final HttpServletRequest request) {
		return new WorkNotLoggedIn() {
			@Override
			protected ModelAndView doWorkNotLoggedIn(final RequestContext rc) throws LoginFailedException, MayNotBeLoggedInException,
					ServiceCommunicationException {
				this.getLog().debug("Serving registration page");
				final ModelAndView mav = new ModelAndView("register");
				mav.getModel().put("userRegistrationForm", new UserRegistrationForm());
				return mav;
			}
		}.process(request);
	}

	/**
	 * Posting user registration form. If registration is successful, the user
	 * will be automatically logged in.
	 * 
	 * @param request
	 *            HTTP request.
	 * @param userRegistrationForm
	 *            The posted form contents.
	 * @param result
	 *            Result of data binding.
	 * @return View to registration form (with error messages) if failed,
	 *         redirect to system dashboard if successful.
	 */
	@RequestMapping(value = SpringRequestMappings.PUBLIC_REGISTER, method = RequestMethod.POST)
	public ModelAndView registerFormPost(final HttpServletRequest request, @Valid final UserRegistrationForm userRegistrationForm,
			final BindingResult result) {
		// TODO Rewrite this method using work wrapper
		this.getLog().debug("Registration form posted");
		final ModelAndView mav = new ModelAndView();
		if (result.hasErrors()) {
			this.getLog().debug("Registration form has binding errors");
			mav.setViewName("register");
			// Framework adds registration form and binding result automatically
			return mav;
		}
		final RequestContext rc = this.getContext(request);
		try {
			final User user = this.getLoginService().registerUser(rc, userRegistrationForm.getUsername(), userRegistrationForm.getPassword(),
					userRegistrationForm.getRepeatPassword());
			this.setLoggedInUser(user, request.getSession());
			mav.setView(new RedirectView(rc.getContextPath() + "/index.html"));
			return mav;
		} catch (RepeatedPasswordNotMatchingException e) {
			this.getLog().debug("Repeated password did not match when registering user");
			final FieldError error = new FieldError("userRegistrationForm", "repeatPassword", "passwords did not match");
			result.addError(error);
			mav.setViewName("register");
			// Framework adds registration form and binding result automatically
			return mav;
		} catch (final UserCreationException e) {
			this.getLog().debug("User could not be registered, assume that the username is not unique");
			final FieldError error = new FieldError("userRegistrationForm", "username", "username is not unique");
			result.addError(error);
			mav.setViewName("register");
			// Framework adds registration form and binding result automatically
			return mav;
		} catch (final ServiceCommunicationException e) {
			this.getLog().debug("Failure to communicate with service when registering user");
			final ObjectError error = new ObjectError("userRegistrationForm", "communication problem");
			result.addError(error);
			mav.setViewName("register");
			// Framework adds registration form and binding result automatically
			return mav;
		} catch (MayNotBeLoggedInException e) {
			this.getLog().debug("Attempting to register a new user while logged in");
			mav.setView(new RedirectView(rc.getContextPath() + "/index.html"));
			return mav;
		}
	}
}