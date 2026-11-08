package de.marcschuler.onyxserver.webclient;

import de.marcschuler.onyxserver.service.gameintegration.Game;
import de.marcschuler.onyxserver.service.gameintegration.SteamID64;
import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class WebClientGameIntegration {
    @Nullable
    private SteamID64 steamId64;

    @Nullable
    private Game game;
}
