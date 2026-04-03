package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Page;
import com.gaucimaistre.headcount.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaqService {

    private final PageRepository pageRepository;
    private final PageService pageService;

    public List<Page> findAll() {
        return pageService.findByName("faq");
    }

    public Optional<Page> findById(int id) {
        return Optional.empty();
    }

    public void save(Page page) {
        pageService.create(page);
    }

    public void update(int id, Page page) {
        pageService.update(page);
    }

    public void delete(int id) {
        pageService.delete(id);
    }
}
