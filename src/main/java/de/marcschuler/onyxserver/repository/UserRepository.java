package de.marcschuler.onyxserver.repository;

import de.marcschuler.onyxserver.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
