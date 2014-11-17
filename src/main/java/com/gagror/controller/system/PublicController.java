package com.gagror.controller.system;

import static com.gagror.data.account.SecurityRoles.IS_PUBLIC;
import lombok.extern.apachecommons.CommonsLog;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gagror.controller.AbstractController;

@Controller
@CommonsLog
@PreAuthorize(IS_PUBLIC)
public class PublicController extends AbstractController {

	@RequestMapping("/")
	public String about(final Model model) {
		log.info("Viewing about page");
		return "about";
	}
}
