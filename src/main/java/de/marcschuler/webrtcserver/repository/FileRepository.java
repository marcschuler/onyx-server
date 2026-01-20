package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
}
