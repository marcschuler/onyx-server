package de.marcschuler.onyxserver.repository;

import de.marcschuler.onyxserver.data.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {
}
