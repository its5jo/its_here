package com.spring.its_here.domain.aihistory.repository;

import com.spring.its_here.domain.aihistory.entity.AiHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiHistoryRepository extends JpaRepository<AiHistory, UUID> {
}
