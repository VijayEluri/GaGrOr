package com.gagror.service.accesscontrol;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import lombok.SneakyThrows;

import org.springframework.stereotype.Service;

import com.gagror.data.account.LoginCredentialsInput;

@Service
public class PasswordEncryptionService {

	public static final int SALT_BYTES = 12; // 16 characters in Base64
	public static final int SALT_STRING_LENGTH = 16;

	private final SecureRandom secureRandom = new SecureRandom();
	private final Base64.Encoder base64Encoder = Base64.getEncoder();
	private final MessageDigest sha256;

	@SneakyThrows(NoSuchAlgorithmException.class)
	public PasswordEncryptionService() {
		sha256 = MessageDigest.getInstance("SHA-256");
	}

	/**
	 * Encrypt the password in the provided login credentials.
	 *
	 * @param loginCredentials Will be updated with the encrypted password.
	 * @return The encrypted password.
	 */
	public String encrypt(final LoginCredentialsInput loginCredentials) {
		// If the password has already been encrypted, use it
		if(null != loginCredentials.getEncryptedPassword()) {
			return loginCredentials.getEncryptedPassword();
		}
		// Generate a salt for the encryption
		final byte[] salt = generateSalt();
		// Encrypt the password
		final String encryptedPassword =
				this.encodePassword(loginCredentials.getPassword(), salt);
		// Store and return the encrypted password
		loginCredentials.setSalt(base64Encoder.encodeToString(salt));
		loginCredentials.setEncryptedPassword(encryptedPassword);
		return encryptedPassword;
	}

	@SneakyThrows(UnsupportedEncodingException.class)
	private String encodePassword(final String password, final byte[] salt) {
		final byte[] passwordBytes = password.getBytes("UTF-8");
		final byte[] inputBytes = new byte[salt.length + passwordBytes.length];
		System.arraycopy(salt, 0, inputBytes, 0, salt.length);
		System.arraycopy(passwordBytes, 0, inputBytes, salt.length, passwordBytes.length);
		return base64Encoder.encodeToString(sha256.digest(inputBytes));
	}

	private byte[] generateSalt() {
		byte[] salt = new byte[SALT_BYTES];
		secureRandom.nextBytes(salt);
		return salt;
	}
}
