package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.service.HelpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/help")
@RequiredArgsConstructor
public class HelpController {

    private final HelpService helpService;

    @GetMapping
    public String index(Model model) {
        helpService.getHelpPage().ifPresent(p -> model.addAttribute("page", p));
        return "help/index";
    }
}
