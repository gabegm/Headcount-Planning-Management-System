package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.model.enums.UserType;
import com.gaucimaistre.headcount.security.AppUserDetails;
import com.gaucimaistre.headcount.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public String index(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        log.debug("Home page requested by user {}", principal.getUserId());
        model.addAttribute("functions",
                homeService.getFunctionsForUser(principal.getUserId(), principal.getUserType()));
        return "index";
    }
}
