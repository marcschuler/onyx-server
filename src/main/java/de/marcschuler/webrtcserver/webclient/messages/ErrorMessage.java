package de.marcschuler.webrtcserver.webclient.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage extends MessageBody{
    private String message;
}
