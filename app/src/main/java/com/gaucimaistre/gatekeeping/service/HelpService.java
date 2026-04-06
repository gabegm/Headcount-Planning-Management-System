package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Page;
import com.gaucimaistre.gatekeeping.repository.PageRepository;
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

    public void upsert(String body) {
        Optional<Page> existing = getHelpPage();
        if (existing.isPresent()) {
            Page updated = new Page(existing.get().id(), existing.get().submitted(),
                    null, "help", "help", body);
            pageService.update(updated);
        } else {
            pageService.create(new Page(0, null, null, "help", "help", body));
        }
    }

    public void update(Page page) {
        pageService.update(page);
    }
}
