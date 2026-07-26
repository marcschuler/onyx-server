package de.marcschuler.onyxserver.repository;

import de.marcschuler.onyxserver.data.file.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
}
