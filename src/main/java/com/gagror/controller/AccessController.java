package com.gagror.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.gagror.data.account.LoginCredentialsInput;
import com.gagror.data.account.RegisterInput;
import com.gagror.service.accesscontrol.AccessControlService;

@Controller
@RequestMapping("/access")
public class AccessController extends AbstractController {

	@Autowired
	AccessControlService accessControl;

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String loginForm(final Model model) {
		model.addAttribute("loginForm", new LoginCredentialsInput());
		return "login";
	}

	@RequestMapping(value="/login", method = RequestMethod.POST)
	public RedirectView loginProcess(final Model model, final LoginCredentialsInput loginForm) {
		switch(accessControl.logIn(loginForm)) {
		case LOGGED_IN:
			return redirect("/");
		default:
			// TODO Show appropriate form error when login fails
			return redirect("/access/login");
		}
	}

	@RequestMapping(value="/logout")
	public RedirectView logOut(final Model model) {
		accessControl.logOut();
		return redirect("/access/login");
	}

	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String registerForm(@ModelAttribute("registerForm") final RegisterInput registerForm) {
		return "register";
	}

	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String registerProcess(
			@Valid @ModelAttribute("registerForm") final RegisterInput registerForm,
			final BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "register";
		}
		switch(accessControl.register(registerForm)) {
		case LOGGED_IN:
			return "redirect:/";
		case LOGIN_FAILED:
			// TODO Unexpected immediate login failure, do something else here?
			return "redirect:/access/login";
		case REGISTER_FAILED_USERNAME_BUSY:
			bindingResult.addError(new FieldError(bindingResult.getObjectName(), "username", "must be unique"));
			return "register";
		case REGISTER_FAILED_PASSWORDS_DONT_MATCH:
		default:
			bindingResult.addError(new FieldError(bindingResult.getObjectName(), "passwordRepeat", "must match"));
			return "register";
		}
	}
}
