package de.marcschuler.webrtcserver.webclient.messages.server;

import de.marcschuler.webrtcserver.dto.data.SectionDTO;
import de.marcschuler.webrtcserver.dto.data.SectionExtendedDTO;
import de.marcschuler.webrtcserver.dto.data.ServerDTO;
import de.marcschuler.webrtcserver.dto.data.UserSimpleDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private ServerDTO server;

    @NotNull
    private List<SectionExtendedDTO> sections;
    @NotNull
    private  List<UserSimpleDTO> users;
    @NotNull
    private  List<UserSimpleDTO> usersNotInChannel;

}
