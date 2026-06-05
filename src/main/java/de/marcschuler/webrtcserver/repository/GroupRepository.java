package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    List<Group> findGroupByDefaultForNewUsersIsTrue();
}
