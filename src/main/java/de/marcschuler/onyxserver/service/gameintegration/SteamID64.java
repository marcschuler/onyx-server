package de.marcschuler.onyxserver.service.gameintegration;

import jakarta.annotation.Nullable;

public class SteamID64 {

    private final long steamId64;

    private SteamID64(long steamId64) {
        this.steamId64 = steamId64;
    }

    public static SteamID64 fromLong(long steamId64) {
        if (!isSteamID64(steamId64)) {
            throw new IllegalArgumentException("Invalid SteamID64: " + steamId64);
        }
        return new SteamID64(steamId64);
    }

    public static boolean isSteamID64(@Nullable String id) {
        if (id == null || id.isBlank())
            return false;
        if (id.length() != 17)
            return false;
        if (!id.startsWith("7656119"))
            return false;
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isSteamID64(long id) {
        return isSteamID64(Long.toString(id));
    }

    public long value() {
        return steamId64;
    }

    @Override
    public String toString() {
        return Long.toString(steamId64);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SteamID64 other))
            return false;
        return steamId64 == other.steamId64;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(steamId64);
    }
}
