package de.marcschuler.onyxserver.data.message;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(
        name = "type",
        discriminatorType = DiscriminatorType.STRING
)
@Data
public abstract class MessageContent {

    @Id
    @GeneratedValue
    protected UUID id;
}