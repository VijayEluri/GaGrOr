package com.gagror.controller.wh40kskirmish;

import javax.validation.Valid;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gagror.controller.AbstractController;
import com.gagror.data.wh40kskirmish.Wh40kSkirmishFactionInput;
import com.gagror.data.wh40kskirmish.Wh40kSkirmishFactionOutput;
import com.gagror.data.wh40kskirmish.Wh40kSkirmishGangTypeInput;
import com.gagror.data.wh40kskirmish.Wh40kSkirmishGangTypeOutput;
import com.gagror.service.social.GroupService;
import com.gagror.service.wh40kskirmish.Wh40kSkirmishFactionPersister;
import com.gagror.service.wh40kskirmish.Wh40kSkirmishGangTypePersister;
import com.gagror.service.wh40kskirmish.Wh40kSkirmishRulesService;

@Controller
@RequestMapping("/wh40kskirmish/rules")
@CommonsLog
public class Wh40kSkirmishRulesController extends AbstractController {

	protected static final String ATTR_GANGTYPE_ID = "gangTypeId";
	protected static final String ATTR_FACTION_ID = "factionId";
	protected static final String ATTR_RACE_ID = "raceId";

	@Autowired
	GroupService groupService;

	@Autowired
	Wh40kSkirmishRulesService rulesService;

	@Autowired
	Wh40kSkirmishGangTypePersister gangTypePersister;

	@Autowired
	Wh40kSkirmishFactionPersister factionPersister;

	@PreAuthorize(MAY_VIEW_GROUP)
	@RequestMapping("/{" + ATTR_GROUP_ID + "}")
	public String viewRules(@PathVariable(ATTR_GROUP_ID) final Long groupId, final Model model) {
		log.info(String.format("Viewing rules for group %d", groupId));
		model.addAttribute("rules", rulesService.viewRules(groupId));
		// TODO Make initial territory allocation for gangs configurable
		// TODO Make initial money per gang configurable
		// TODO Make name of currency in game configurable
		return "wh40kskirmish/rules_view";
	}

	// TODO Add page to edit basic rules

	@PreAuthorize(MAY_ADMIN_GROUP)
	@RequestMapping(value="/{" + ATTR_GROUP_ID + "}/gangtype/create", method=RequestMethod.GET)
	public String createGangTypeForm(@PathVariable(ATTR_GROUP_ID) final Long groupId, final Model model) {
		log.info(String.format("Viewing create gang type form for group %d", groupId));
		model.addAttribute("group", groupService.viewGroup(groupId));
		model.addAttribute("gangTypeForm", new Wh40kSkirmishGangTypeInput(groupId));
		return "wh40kskirmish/gangtypes_edit";
	}

	@PreAuthorize(MAY_ADMIN_GROUP)
	@RequestMapping(value="/{" + ATTR_GROUP_ID + "}/gangtype/save", method=RequestMethod.POST)
	public Object saveGangTypeForm(
			@PathVariable(ATTR_GROUP_ID) final Long groupId,
			final Model model,
			@Valid @ModelAttribute("gangTypeForm") final Wh40kSkirmishGangTypeInput gangTypeForm,
			final BindingResult bindingResult) {
		if(! groupId.equals(gangTypeForm.getGroupId())) {
			log.error(String.format("Group ID URL (%d) and form (%d) mismatch when attempting to save gang type form", groupId, gangTypeForm.getGroupId()));
			throw new IllegalArgumentException(String.format("Unexpected group ID in gang type form"));
		}
		if(gangTypePersister.save(gangTypeForm, bindingResult)) {
			log.info(String.format("Saved gang type: %s", gangTypeForm));
			return redirect(String.format("/wh40kskirmish/rules/%d", groupId));
		} else {
			log.warn(String.format("Failed to save: %s", gangTypeForm));
			model.addAttribute("group", groupService.viewGroup(groupId));
			return "wh40kskirmish/gangtypes_edit";
		}
	}

	@PreAuthorize(MAY_VIEW_GROUP)
	@RequestMapping("/{" + ATTR_GROUP_ID + "}/gangtype/{" + ATTR_GANGTYPE_ID + "}")
	public String viewGangType(
			@PathVariable(ATTR_GROUP_ID) final Long groupId,
			@PathVariable(ATTR_GANGTYPE_ID) final Long gangTypeId,
			final Model model) {
		model.addAttribute("gangType", rulesService.viewGangType(groupId, gangTypeId));
		return "wh40kskirmish/gangtypes_view";
	}

	@PreAuthorize(MAY_ADMIN_GROUP)
	@RequestMapping(value="/{" + ATTR_GROUP_ID + "}/gangtype/{" + ATTR_GANGTYPE_ID + "}/edit", method=RequestMethod.GET)
	public String editGangTypeForm(
			@PathVariable(ATTR_GROUP_ID) final Long groupId,
			@PathVariable(ATTR_GANGTYPE_ID) final Long gangTypeId,
			final Model model) {
		log.info(String.format("Viewing edit gang type form for gang type %d in group %d", gangTypeId, groupId));
		final Wh40kSkirmishGangTypeOutput gangType = rulesService.viewGangType(groupId, gangTypeId);
		model.addAttribute("group", gangType.getGroup());
		model.addAttribute("gangTypeForm", new Wh40kSkirmishGangTypeInput(gangType));
		return "wh40kskirmish/gangtypes_edit";
	}

	@PreAuthorize(MAY_ADMIN_GROUP)
	@RequestMapping(value="/{" + ATTR_GROUP_ID + "}/faction/{" + ATTR_GANGTYPE_ID + "}/create", method=RequestMethod.GET)
	public String createFactionForm(
			@PathVariable(ATTR_GROUP_ID) final Long groupId,
			@PathVariable(ATTR_GANGTYPE_ID) final Long gangTypeId,
			final Model model) {
		log.info(String.format("Viewing create faction form for gang type %d of group %d", gangTypeId, groupId));
		model.addAttribute("gangType", rulesService.viewGangType(groupId, gangTypeId));
		model.addAttribute("factionForm", new Wh40kSkirmishFactionInput(groupId, gangTypeId));
		return "wh40kskirmish/factions_edit";
	}

	@PreAuthorize(MAY_ADMIN_GROUP)
	@RequestMapping(value="/{" + ATTR_GROUP_ID + "}/faction/save", method=RequestMethod.POST)
	public Object saveFactionForm(
			@PathVariable(ATTR_GROUP_ID) final Long groupId,
			final Model model,
			@Valid @ModelAttribute("factionForm") final Wh40kSkirmishFactionInput factionForm,
			final BindingResult bindingResult) {
		if(! groupId.equals(factionForm.getGroupId())) {
			log.error(String.format("Group ID URL (%d) and form (%d) mismatch when attempting to save faction form", groupId, factionForm.getGroupId()));
			throw new IllegalArgumentException(String.format("Unexpected group ID in faction form"));
		}
		if(factionPersister.save(factionForm, bindingResult)) {
			log.info(String.format("Saved faction: %s", factionForm));
			return redirect(String.format("/wh40kskirmish/rules/%d", groupId));
		} else {
			log.warn(String.format("Failed to save: %s", factionForm));
			model.addAttribute("gangType", rulesService.viewGangType(groupId, factionForm.getGangTypeId()));
			return "wh40kskirmish/factions_edit";
		}
	}

	@PreAuthorize(MAY_VIEW_GROUP)
	@RequestMapping("/{" + ATTR_GROUP_ID + "}/faction/{" + ATTR_GANGTYPE_ID + "}/{" + ATTR_FACTION_ID + "}")
	public String viewFaction(
			@PathVariable(ATTR_GROUP_ID) final Long groupId,
			@PathVariable(ATTR_GANGTYPE_ID) final Long gangTypeId,
			@PathVariable(ATTR_FACTION_ID) final Long factionId,
			final Model model) {
		model.addAttribute("faction", rulesService.viewFaction(groupId, gangTypeId, factionId));
		return "wh40kskirmish/factions_view";
	}

	@PreAuthorize(MAY_ADMIN_GROUP)
	@RequestMapping(value="/{" + ATTR_GROUP_ID + "}/faction/{" + ATTR_GANGTYPE_ID + "}/{" + ATTR_FACTION_ID + "}/edit", method=RequestMethod.GET)
	public String editFactionForm(
			@PathVariable(ATTR_GROUP_ID) final Long groupId,
			@PathVariable(ATTR_GANGTYPE_ID) final Long gangTypeId,
			@PathVariable(ATTR_FACTION_ID) final Long factionId,
			final Model model) {
		log.info(String.format("Viewing edit faction form for faction %d of gang type %d in group %d", factionId, gangTypeId, groupId));
		final Wh40kSkirmishFactionOutput faction = rulesService.viewFaction(groupId, gangTypeId, factionId);
		model.addAttribute("gangType", faction.getGangType());
		model.addAttribute("factionForm", new Wh40kSkirmishFactionInput(faction));
		return "wh40kskirmish/factions_edit";
	}

	// TODO Add page to create race of gang type

	@PreAuthorize(MAY_VIEW_GROUP)
	@RequestMapping("/{" + ATTR_GROUP_ID + "}/race/{" + ATTR_GANGTYPE_ID + "}/{" + ATTR_RACE_ID + "}")
	public String viewRace(
			@PathVariable(ATTR_GROUP_ID) final Long groupId,
			@PathVariable(ATTR_GANGTYPE_ID) final Long gangTypeId,
			@PathVariable(ATTR_RACE_ID) final Long raceId,
			final Model model) {
		model.addAttribute("race", rulesService.viewRace(groupId, gangTypeId, raceId));
		return "wh40kskirmish/races_view";
	}

	// TODO Add page to edit race of gang type

	// TODO Add pages for creating, viewing, and editing fighter types of races

	// TODO Add page to view skills

	// TODO Add page to edit skills

	// TODO Add page to view equipment

	// TODO Add page to edit equipment

	// TODO Add page to view territories

	// TODO Add page to edit territories
}
