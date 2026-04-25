package de.marcschuler.webrtcserver.data.message;

import de.marcschuler.webrtcserver.data.File;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("FILE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMessageContent extends MessageContent{

    @ManyToOne
    private File file;
}
