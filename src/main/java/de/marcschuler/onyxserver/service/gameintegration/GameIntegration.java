package de.marcschuler.onyxserver.service.gameintegration;

import de.marcschuler.onyxserver.webclient.WebClient;
import org.springframework.core.Ordered;

import java.util.List;

public interface GameIntegration<PLAYERID> extends Ordered {

    void updateClients(List<WebClient> clients);
}
