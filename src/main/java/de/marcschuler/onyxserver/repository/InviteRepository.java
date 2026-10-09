package de.marcschuler.onyxserver.repository;

import de.marcschuler.onyxserver.data.Invite;
import de.marcschuler.onyxserver.data.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InviteRepository extends JpaRepository<Invite, String> {

    List<Invite> findAllByServer(Server server);
}
