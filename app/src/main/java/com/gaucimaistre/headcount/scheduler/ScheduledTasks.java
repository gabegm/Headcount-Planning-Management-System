package com.gaucimaistre.headcount.scheduler;

import com.gaucimaistre.headcount.repository.UserRepository;
import com.gaucimaistre.headcount.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final SubmissionService submissionService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *")
    void applyEffectiveDateSubmissions() {
        log.info("Scheduler: applying effective-date submissions");
        try {
            submissionService.applyEffectiveDateSubmissions();
        } catch (Exception e) {
            log.error("Scheduler: failed to apply effective-date submissions", e);
        }
    }

    @Scheduled(cron = "0 10 0 * * *")
    void expirePasswordResetTokens() {
        log.info("Scheduler: clearing all password reset tokens");
        try {
            userRepository.clearAllPasswordResetTokens();
        } catch (Exception e) {
            log.error("Scheduler: failed to clear password reset tokens", e);
        }
    }
}
