package de.marcschuler.onyxserver.dto.data;

import de.marcschuler.onyxserver.dto.data.message.MessageContentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ServerDTO  {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private UUID id;

    @NotNull
    private String name;

    private FileDTO icon;

    @NotNull
    private Map<String,Object> publicKey;
    @NotNull
    private List<MessageContentDTO> description;
}
