package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServerRepository extends JpaRepository<Server, UUID> {
}
