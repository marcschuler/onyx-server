package de.marcschuler.webrtcserver.webclient.messages.peers;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * A peer-to-peer message that explains what kind of stream a newly created stream is
 * @param mid the mid of the tracks sender (is defined as string but may be a number)
 * @param label the label. either cameramic or screen
 */
public record TrackMetadataMessage(@NotNull String mid, @NotNull TrackMetaLabel label) implements MessageBody {

    public enum TrackMetaLabel{
        CAMERAMIC,
        SCREEN
    }
}
