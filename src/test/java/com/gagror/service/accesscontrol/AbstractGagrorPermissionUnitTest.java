package com.gagror.service.accesscontrol;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import lombok.Getter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.gagror.CodingErrorException;
import com.gagror.data.Identifiable;
import com.gagror.data.account.AccountEntity;

@RunWith(MockitoJUnitRunner.class)
public class AbstractGagrorPermissionUnitTest {

	private static final String PERMISSION_NAME = "testPermission";
	private static final String ANOTHER_NAME = "testAnother";

	Impl instance;

	@Mock
	AccountEntity accountEntity;

	@Test
	public void hasPermission_domainObject_ok() {
		assertTrue("Permission should have been granted", instance.hasPermission(accountEntity, PERMISSION_NAME));
	}

	@Test
	public void hasPermission_domainObject_notOk() {
		assertFalse("Permission should not have been granted", instance.hasPermission(accountEntity, ANOTHER_NAME));
	}

	@Test
	public void hasPermission_idType_ok() {
		assertTrue("Permission should have been granted", instance.hasPermission(accountEntity, PERMISSION_NAME, Target.class.getCanonicalName()));
	}

	@Test
	public void hasPermission_idType_notOk() {
		assertFalse("Permission should not have been granted", instance.hasPermission(accountEntity, ANOTHER_NAME, Target.class.getCanonicalName()));
	}

	@Test(expected=CodingErrorException.class)
	public void hasPermission_idType_wrongTargetType() {
		instance.hasPermission(accountEntity, PERMISSION_NAME, "Wrong target type");
	}

	@Before
	public void setupInstance() {
		instance = new Impl();
	}

	@Before
	public void setupAccount() {
		// Set account name to permission name because that's how the test Impl checks permissions
		when(accountEntity.getName()).thenReturn(PERMISSION_NAME);
	}

	private class Impl extends AbstractGagrorPermission<String, Target> {

		public Impl() {
			super(PERMISSION_NAME, Target.class);
		}

		@Override
		protected String parseId(final String rawId) {
			return rawId;
		}

		@Override
		protected boolean hasPermission(final String id, final AccountEntity account) {
			return id.equals(account.getName());
		}
	}

	private class Target implements Identifiable<String> {
		@Getter
		private String id;
	}
}
