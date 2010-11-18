package se.spacejens.gagror.model.user;

/**
 * Specification of functionality for the user DAO.
 * 
 * @author spacejens
 */
public interface UserDAO {

	/**
	 * Create and persist a new user.
	 * 
	 * @param username
	 *            Username to use.
	 * @param password
	 *            Encrypted password to use.
	 * @return The user after persisting.
	 */
	public User createUser(final String username, final String password);
}
