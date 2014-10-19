package com.gagror.data.account;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.gagror.data.Identifiable;

@Data
@NoArgsConstructor
@Entity
@ToString(exclude="password")
@Table(name="account")
public class AccountEntity implements Identifiable<Long> {

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Long version;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private AccountType accountType;

	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false)
	private boolean locked;

	@Column(nullable = false, insertable = true, updatable = false)
	private Date created;

	public AccountEntity(final String username, final String encryptedPassword) {
		setUsername(username);
		setPassword(encryptedPassword);
		setAccountType(AccountType.STANDARD);
		setActive(true);
		setLocked(false);
		setCreated(new Date());
	}
}
