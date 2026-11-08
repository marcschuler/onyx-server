package de.marcschuler.onyxserver.dto;

import jakarta.validation.constraints.NotNull;

/**
 * The payload a client signs to answer an {@link AuthChallenge}.
 * <p>
 * The origin is the url the client itself connected to (e.g. {@code https://example.com}) and must
 * never be taken from data the server sent. As it is covered by the client's signature, a malicious
 * server relaying the challenge of another server can not change it, and the real server rejects
 * the proof because the origin is not one of its own.
 *
 * @param type      domain separation, must be {@link #TYPE} so signatures for other purposes can not be reused
 * @param challenge the challenge exactly as sent by the server
 * @param origin    scheme, host and port the client connected to
 */
public record
AuthChallengeProof(@NotNull String type,
                                 @NotNull Challenge challenge,
                                 @NotNull String origin) {

    public static final String TYPE = "onyx-auth";
}
