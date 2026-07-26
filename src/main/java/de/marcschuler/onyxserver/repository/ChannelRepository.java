package de.marcschuler.onyxserver.repository;

import de.marcschuler.onyxserver.data.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    Optional<Channel> findChannelByChatIs(UUID chatId);
}
