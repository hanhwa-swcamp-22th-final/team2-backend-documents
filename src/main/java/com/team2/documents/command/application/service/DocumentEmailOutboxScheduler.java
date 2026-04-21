package com.team2.documents.command.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.DocumentEmailOutbox;
import com.team2.documents.command.domain.entity.enums.DocumentEmailOutboxStatus;
import com.team2.documents.command.domain.repository.DocumentEmailOutboxRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentEmailOutboxScheduler {

    private final DocumentEmailOutboxRepository outboxRepository;
    private final DocumentEmailOutboxWorker outboxWorker;

    @Value("${documents.mail.outbox.poll-size:10}")
    private int pollSize;

    @Scheduled(
            fixedDelayString = "${documents.mail.outbox.poll-delay-ms:30000}",
            initialDelayString = "${documents.mail.outbox.initial-delay-ms:10000}"
    )
    public void dispatchReadyOutboxRows() {
        List<Long> outboxIds = outboxRepository.findReadyIds(
                DocumentEmailOutboxStatus.PENDING,
                DocumentEmailOutbox.now(),
                PageRequest.of(0, pollSize)
        );
        for (Long outboxId : outboxIds) {
            outboxWorker.processAsync(outboxId);
        }
    }
}
