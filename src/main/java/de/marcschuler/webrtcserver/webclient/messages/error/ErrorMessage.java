package de.marcschuler.webrtcserver.webclient.messages.error;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;

/**
 * A error message. May be related to a request the client did send
 * @param message the message of the error (optional)
 */
public record ErrorMessage(String message) implements MessageBody {
}
