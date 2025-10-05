package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
}
