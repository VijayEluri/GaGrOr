package com.gagror.service.wh40kskirmish.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

import com.gagror.data.DataNotFoundException;
import com.gagror.data.group.GroupEntity;
import com.gagror.data.group.GroupRepository;
import com.gagror.data.group.WrongGroupTypeException;
import com.gagror.data.wh40kskirmish.rules.Wh40kSkirmishRulesEntity;
import com.gagror.data.wh40kskirmish.rules.experience.Wh40kSkirmishExperienceLevelEntity;
import com.gagror.data.wh40kskirmish.rules.experience.Wh40kSkirmishExperienceLevelInput;
import com.gagror.data.wh40kskirmish.rules.gangs.Wh40kSkirmishGangTypeEntity;
import com.gagror.data.wh40kskirmish.rules.gangs.Wh40kSkirmishGangTypeInput;
import com.gagror.data.wh40kskirmish.rules.gangs.Wh40kSkirmishGangTypeRepository;

@RunWith(MockitoJUnitRunner.class)
public class Wh40kSkirmishGangTypePersisterUnitTest {

	private static final Long GROUP_ID = 2135L;

	private static final String FORM_GANG_TYPE_NAME = "Gang type form";
	private static final String FORM_XP_LEVEL_FIRST_NAME = "First level form";
	private static final int FORM_XP_LEVEL_FIRST_XP = 0;
	private static final String FORM_XP_LEVEL_SECOND_NAME = "Second level form";
	private static final int FORM_XP_LEVEL_SECOND_XP = 6;

	private static final Long DB_GANG_TYPE_ID = 5678L;
	private static final String DB_GANG_TYPE_NAME = "Gang type DB";
	private static final Long DB_GANG_TYPE_VERSION = 5L;
	private static final Long DB_XP_LEVEL_FIRST_ID = 3457L;
	private static final String DB_XP_LEVEL_FIRST_NAME = "First level DB";
	private static final int DB_XP_LEVEL_FIRST_XP = 0;
	private static final Long DB_XP_LEVEL_SECOND_ID = 3525L;
	private static final String DB_XP_LEVEL_SECOND_NAME = "Second level DB";
	private static final int DB_XP_LEVEL_SECOND_XP = 11;

	Wh40kSkirmishGangTypePersister instance;

	@Mock
	Wh40kSkirmishGangTypeInput form;

	@Mock
	Wh40kSkirmishExperienceLevelInput formExperienceLevelFirst;

	@Mock
	Wh40kSkirmishExperienceLevelInput formExperienceLevelSecond;

	@Mock
	BindingResult bindingResult;

	@Mock
	GroupRepository groupRepository;

	@Mock
	Wh40kSkirmishGangTypeRepository gangTypeRepository;

	@Mock
	GroupEntity group;

	@Mock
	Wh40kSkirmishGangTypeEntity gangType;

	@Mock
	Wh40kSkirmishExperienceLevelEntity experienceLevelFirst;

	@Mock
	Wh40kSkirmishExperienceLevelEntity experienceLevelSecond;

	@Mock
	Wh40kSkirmishRulesEntity rules;

	@Test
	public void save_new_ok() {
		final boolean result = instance.save(form, bindingResult);
		assertTrue("Should have saved successfully", result);
		verify(bindingResult).hasErrors(); // Should check for form validation errors
		verifyNoMoreInteractions(bindingResult);
		final ArgumentCaptor<Wh40kSkirmishGangTypeEntity> savedGangType = ArgumentCaptor.forClass(Wh40kSkirmishGangTypeEntity.class);
		verify(gangTypeRepository).save(savedGangType.capture());
		assertEquals("Wrong name", FORM_GANG_TYPE_NAME, savedGangType.getValue().getName());
		// TODO Verify creation of experience levels
		assertTrue("Not added to rules", rules.getGangTypes().contains(savedGangType.getValue()));
	}

	// TODO Test for failing to save new gang type because no experience level starts at zero

	// TODO Test for failing to save new gang type because there are duplicates in the experience levels

	@Test
	public void save_new_bindingError() {
		when(bindingResult.hasErrors()).thenReturn(true);
		final boolean result = instance.save(form, bindingResult);
		assertFalse("Should have failed to save", result);
		verify(gangTypeRepository, never()).save(any(Wh40kSkirmishGangTypeEntity.class));
	}

	@Test(expected=WrongGroupTypeException.class)
	public void save_new_wrongGroupType() {
		when(group.getWh40kSkirmishRules()).thenReturn(null);
		instance.save(form, bindingResult);
	}

	@Test
	public void save_existing_ok() {
		whenGangTypeExists();
		final boolean result = instance.save(form, bindingResult);
		assertTrue("Should have saved successfully", result);
		verify(bindingResult).hasErrors(); // Should check for form validation errors
		verifyNoMoreInteractions(bindingResult);
		verify(gangTypeRepository, never()).save(any(Wh40kSkirmishGangTypeEntity.class));
		verify(gangType).setName(FORM_GANG_TYPE_NAME);
		verify(experienceLevelFirst).setName(FORM_XP_LEVEL_FIRST_NAME);
		verify(experienceLevelFirst).setExperiencePoints(FORM_XP_LEVEL_FIRST_XP);
		verify(experienceLevelSecond).setName(FORM_XP_LEVEL_SECOND_NAME);
		verify(experienceLevelSecond).setExperiencePoints(FORM_XP_LEVEL_SECOND_XP);
	}

	// TODO Test for successfully saving existing gang type and adding experience level

	// TODO Test for successfully saving existing gang type and removing experience level

	// TODO Test for failing to save existing gang type because no experience level starts at zero

	// TODO Test for failing to save existing gang type because there are duplicates in the experience levels

	@Test
	public void save_existing_bindingError() {
		whenGangTypeExists();
		when(bindingResult.hasErrors()).thenReturn(true);
		final boolean result = instance.save(form, bindingResult);
		assertFalse("Should have failed to save", result);
		verify(gangType, never()).setName(anyString());
		verify(gangTypeRepository, never()).save(any(Wh40kSkirmishGangTypeEntity.class));
	}

	@Test(expected=WrongGroupTypeException.class)
	public void save_existing_wrongGroupType() {
		whenGangTypeExists();
		when(group.getWh40kSkirmishRules()).thenReturn(null);
		instance.save(form, bindingResult);
	}

	@Test(expected=DataNotFoundException.class)
	public void save_existing_notFoundInGroup() {
		whenGangTypeExists();
		rules.getGangTypes().remove(gangType);
		instance.save(form, bindingResult);
	}

	@Test
	public void save_existing_simultaneousEdit() {
		whenGangTypeExists();
		when(form.getVersion()).thenReturn(DB_GANG_TYPE_VERSION - 1);
		when(bindingResult.hasErrors()).thenReturn(true); // Will be the case when checked
		final boolean result = instance.save(form, bindingResult);
		assertFalse("Should have failed to save", result);
		verify(form).addErrorSimultaneuosEdit(bindingResult);
		verify(gangType, never()).setName(anyString());
	}

	protected void whenGangTypeExists() {
		when(form.getId()).thenReturn(DB_GANG_TYPE_ID);
		when(form.getVersion()).thenReturn(DB_GANG_TYPE_VERSION);
		when(gangType.getId()).thenReturn(DB_GANG_TYPE_ID);
		when(gangType.getVersion()).thenReturn(DB_GANG_TYPE_VERSION);
		when(gangType.getName()).thenReturn(DB_GANG_TYPE_NAME);
		when(gangType.getRules()).thenReturn(rules);
		rules.getGangTypes().add(gangType);
		when(gangType.getExperienceLevels()).thenReturn(new HashSet<Wh40kSkirmishExperienceLevelEntity>());
		when(experienceLevelFirst.getId()).thenReturn(DB_XP_LEVEL_FIRST_ID);
		when(experienceLevelFirst.getName()).thenReturn(DB_XP_LEVEL_FIRST_NAME);
		when(experienceLevelFirst.getExperiencePoints()).thenReturn(DB_XP_LEVEL_FIRST_XP);
		when(experienceLevelFirst.getGangType()).thenReturn(gangType);
		gangType.getExperienceLevels().add(experienceLevelFirst);
		when(experienceLevelSecond.getId()).thenReturn(DB_XP_LEVEL_SECOND_ID);
		when(experienceLevelSecond.getName()).thenReturn(DB_XP_LEVEL_SECOND_NAME);
		when(experienceLevelSecond.getExperiencePoints()).thenReturn(DB_XP_LEVEL_SECOND_XP);
		when(experienceLevelSecond.getGangType()).thenReturn(gangType);
		gangType.getExperienceLevels().add(experienceLevelSecond);
	}

	@Before
	public void setupForm() {
		when(form.getGroupId()).thenReturn(GROUP_ID);
		when(form.getName()).thenReturn(FORM_GANG_TYPE_NAME);
		when(form.getExperienceLevels()).thenReturn(new ArrayList<Wh40kSkirmishExperienceLevelInput>());
		when(formExperienceLevelFirst.getName()).thenReturn(FORM_XP_LEVEL_FIRST_NAME);
		when(formExperienceLevelFirst.getExperiencePoints()).thenReturn(FORM_XP_LEVEL_FIRST_XP);
		form.getExperienceLevels().add(formExperienceLevelFirst);
		when(formExperienceLevelSecond.getName()).thenReturn(FORM_XP_LEVEL_SECOND_NAME);
		when(formExperienceLevelSecond.getExperiencePoints()).thenReturn(FORM_XP_LEVEL_SECOND_XP);
		form.getExperienceLevels().add(formExperienceLevelSecond);
	}

	@Before
	public void setupGroup() {
		when(group.getId()).thenReturn(GROUP_ID);
		when(groupRepository.findOne(GROUP_ID)).thenReturn(group);
		when(group.getWh40kSkirmishRules()).thenReturn(rules);
		when(rules.getGangTypes()).thenReturn(new HashSet<Wh40kSkirmishGangTypeEntity>());
	}

	@Before
	public void setupInstance() {
		instance = new Wh40kSkirmishGangTypePersister();
		instance.groupRepository = groupRepository;
		instance.gangTypeRepository = gangTypeRepository;
	}
}
