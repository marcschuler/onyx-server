package de.marcschuler.onyxserver.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Invite {

    // a unique code
    @Id
    @Size(min = 3, max = 32)
    private String code;

    // a public facing title and description
    @Column(nullable = false)
    private String title;
    private String description;

    // optional: start and end times for this invite
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // optional: limit the amount of uses from this invite
    private Integer maxUsages;
    private int usages;

    //optional: a list of groups the user has access to
    @ManyToMany
    private List<Group> groups;
}
