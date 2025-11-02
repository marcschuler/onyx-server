package de.marcschuler.webrtcserver.dto;

import lombok.Data;

@Data
public class IceServer {
    private String urls;
    private String username;
    private String credential;
}
