package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.policy.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {
}
