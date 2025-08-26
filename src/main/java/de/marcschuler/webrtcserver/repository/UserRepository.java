package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
