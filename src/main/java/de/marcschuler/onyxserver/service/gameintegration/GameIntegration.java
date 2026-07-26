package de.marcschuler.onyxserver.service.gameintegration;

import org.springframework.core.Ordered;

import java.util.List;
import java.util.Optional;

public interface GameIntegration<PLAYERID> extends Ordered {

    Optional<ClientCurrentGame> getCurrentlyPlaying(PLAYERID id);

    void updateGameList(List<PLAYERID> ids);
}
