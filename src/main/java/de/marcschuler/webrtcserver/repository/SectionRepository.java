package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {
}
