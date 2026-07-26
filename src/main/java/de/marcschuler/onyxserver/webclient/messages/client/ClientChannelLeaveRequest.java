package de.marcschuler.onyxserver.webclient.messages.client;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;

/**
 * A request from a user leave the current channel.
 * You may assume that this should always be successfull.
 * In this case a @{@link ClientChannelLeaveEvent} with your user id is sent
 */
public record ClientChannelLeaveRequest() implements MessageBody {
}
