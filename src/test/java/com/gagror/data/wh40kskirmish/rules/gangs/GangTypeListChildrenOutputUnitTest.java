package com.gagror.data.wh40kskirmish.rules.gangs;

import static com.gagror.GagrorAssert.assertNames;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.gagror.data.group.GroupEntity;
import com.gagror.data.wh40kskirmish.rules.Wh40kSkirmishRulesEntity;

@RunWith(MockitoJUnitRunner.class)
public class GangTypeListChildrenOutputUnitTest {

	private static final Long FACTION_ID_1 = 1L;
	private static final Long FACTION_ID_2 = 2L;
	private static final Long FACTION_ID_3 = 3L;
	private static final Long FACTION_ID_4 = 4L;
	private static final String FACTION_NAME_1 = "Name 1";
	private static final String FACTION_NAME_2 = "Name 2";
	private static final String FACTION_NAME_3 = "Name 3";
	private static final String FACTION_NAME_4 = "Name 4";
	private static final Long GROUP_ID = 64578L;

	@Mock
	FactionEntity faction1;

	@Mock
	FactionEntity faction2;

	@Mock
	FactionEntity faction3;

	@Mock
	FactionEntity faction4;

	@Mock
	GangTypeEntity gangType;

	@Mock
	Wh40kSkirmishRulesEntity rules;

	@Mock
	GroupEntity group;

	@Test
	public void getFactions() {
		final GangTypeListChildrenOutput instance = new GangTypeListChildrenOutput(gangType);
		String[] expectedFactions = { FACTION_NAME_1, FACTION_NAME_2, FACTION_NAME_3, FACTION_NAME_4 };
		assertNames(instance.getFactions(), expectedFactions);
	}

	@Before
	public void setup() {
		when(gangType.getRules()).thenReturn(rules);
		when(rules.getGroup()).thenReturn(group);
		when(group.getId()).thenReturn(GROUP_ID);
		when(gangType.getFactions()).thenReturn(new HashSet<FactionEntity>());
		when(gangType.getGroup()).thenReturn(group);
		mockFaction(faction1, FACTION_ID_1, FACTION_NAME_1);
		mockFaction(faction2, FACTION_ID_2, FACTION_NAME_2);
		mockFaction(faction3, FACTION_ID_3, FACTION_NAME_3);
		mockFaction(faction4, FACTION_ID_4, FACTION_NAME_4);
	}

	private void mockFaction(final FactionEntity faction, final Long id, final String name) {
		when(faction.getId()).thenReturn(id);
		when(faction.getGroup()).thenReturn(group);
		when(faction.getName()).thenReturn(name);
		gangType.getFactions().add(faction);
	}
}
