package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Page;
import com.gaucimaistre.headcount.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HelpService {

    private final PageRepository pageRepository;
    private final PageService pageService;

    public Optional<Page> getHelpPage() {
        return pageService.findByNameAndTitle("help", "help");
    }

    public void update(Page page) {
        pageService.update(page);
    }
}
