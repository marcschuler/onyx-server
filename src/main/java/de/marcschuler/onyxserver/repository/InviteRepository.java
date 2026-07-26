package de.marcschuler.onyxserver.repository;

import de.marcschuler.onyxserver.data.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, String> {
}
