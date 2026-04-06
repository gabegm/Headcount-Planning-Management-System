package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Page;
import com.gaucimaistre.gatekeeping.repository.PageRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PageService {

    private final PageRepository pageRepository;
    private Policy policy;

    @PostConstruct
    void initPolicy() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("antisamy-slashdot.xml")) {
            if (is != null) {
                policy = Policy.getInstance(is);
            } else {
                log.warn("PageService: antisamy-slashdot.xml not found on classpath; HTML sanitisation disabled");
            }
        } catch (Exception e) {
            log.error("PageService: failed to load AntiSamy policy", e);
        }
    }

    public List<Page> findByName(String name) {
        return pageRepository.findByName(name);
    }

    public Optional<Page> findByNameAndTitle(String name, String title) {
        return pageRepository.findByNameAndTitle(name, title);
    }

    public int create(Page page) {
        String sanitisedBody = sanitise(page.body());
        Page sanitised = new Page(page.id(), page.submitted(), page.edited(), page.name(), page.title(), sanitisedBody);
        return pageRepository.save(sanitised);
    }

    public void update(Page page) {
        String sanitisedBody = sanitise(page.body());
        Page sanitised = new Page(page.id(), page.submitted(), OffsetDateTime.now(), page.name(), page.title(), sanitisedBody);
        pageRepository.update(sanitised);
    }

    public void delete(int id) {
        pageRepository.delete(id);
    }

    private String sanitise(String body) {
        if (body == null || policy == null) {
            return body;
        }
        try {
            AntiSamy antiSamy = new AntiSamy();
            CleanResults cr = antiSamy.scan(body, policy);
            return cr.getCleanHTML();
        } catch (Exception e) {
            log.error("PageService: AntiSamy scan failed, rejecting input to prevent XSS", e);
            return "";
        }
    }
}
