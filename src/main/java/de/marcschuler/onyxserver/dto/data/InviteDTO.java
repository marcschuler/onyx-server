package de.marcschuler.onyxserver.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class InviteDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private String code;

@NotNull
    private String title;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Integer maxUsages;
    @NotNull
    private int usages;

    @ManyToMany
    private List<UUID> groups;
}
