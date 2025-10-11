package de.marcschuler.webrtcserver.webclient.events.server;

import de.marcschuler.webrtcserver.dto.SectionDTO;
import de.marcschuler.webrtcserver.webclient.events.MessageBody;
import lombok.Data;

import java.util.List;

/**
 * Sends the whole section tree (sections, channels, clients)
 * when a change is made, e.g. new channels or a client switched channels.
 * Clients should figure out what changed if there are interested at all.
 * This represents a change that is visible from the client.
 * So this could fire on join or when the client permissions are changed
 */
@Data
public class ServerTreeChangeMessage extends MessageBody {

    private String name;

    private List<SectionDTO> sections;

}
