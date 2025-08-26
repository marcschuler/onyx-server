package de.marcschuler.webrtcserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {

    private String version = "0.0.0";

    private List<ServerInfoDetail> details;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServerInfoDetail {
        private UUID id;
        private String name;
        private byte[] publicKey;
    }
}
