package se.spacejens.gagror.view.spring;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import se.spacejens.gagror.model.user.User;

/**
 * Form backing bean used when registering a new user.
 * 
 * @author spacejens
 */
public class UserRegistrationForm extends SpringFormSupport {

	/** Username. */
	private String username;

	/** Plaintext password. */
	private String password;

	/** Plaintext password repeated. */
	private String repeatPassword;

	/**
	 * Get the username.
	 * 
	 * @return Username.
	 */
	@NotNull
	@Size(min = User.USERNAME_MIN_LENGTH, max = User.USERNAME_MAX_LENGTH)
	public String getUsername() {
		return this.username;
	}

	/**
	 * Set the username.
	 * 
	 * @param username
	 *            Username.
	 */
	public void setUsername(final String username) {
		this.username = username;
	}

	/**
	 * Get the password.
	 * 
	 * @return Password.
	 */
	@NotNull
	@Size(min = User.PASSWORD_MIN_LENGTH, max = User.PASSWORD_MAX_LENGTH)
	public String getPassword() {
		return this.password;
	}

	/**
	 * Set the password.
	 * 
	 * @param password
	 *            Password.
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * Get repeated plaintext password.
	 * 
	 * @return Plaintext password.
	 */
	@NotNull
	@Size(min = User.PASSWORD_MIN_LENGTH, max = User.PASSWORD_MAX_LENGTH)
	public String getRepeatPassword() {
		return this.repeatPassword;
	}

	/**
	 * Set repeated plaintext password.
	 * 
	 * @param repeatPassword
	 *            Plaintext password.
	 */
	public void setRepeatPassword(final String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}
}
