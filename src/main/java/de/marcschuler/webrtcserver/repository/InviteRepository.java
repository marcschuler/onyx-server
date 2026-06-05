package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, String> {
}
