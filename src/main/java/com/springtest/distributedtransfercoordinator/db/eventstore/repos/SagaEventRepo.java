package com.springtest.distributedtransfercoordinator.db.eventstore.repos;

import com.springtest.distributedtransfercoordinator.db.eventstore.models.SagaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SagaEventRepo extends JpaRepository<SagaEvent, UUID> {
}
