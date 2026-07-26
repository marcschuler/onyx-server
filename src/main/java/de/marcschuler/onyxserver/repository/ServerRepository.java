package de.marcschuler.onyxserver.repository;

import de.marcschuler.onyxserver.data.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServerRepository extends JpaRepository<Server, UUID> {
}
