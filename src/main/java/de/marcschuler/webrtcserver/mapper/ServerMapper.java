package de.marcschuler.webrtcserver.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.jwk.JWK;
import de.marcschuler.webrtcserver.config.WebRTConfig;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.dto.ChannelReference;
import de.marcschuler.webrtcserver.dto.SectionReference;
import de.marcschuler.webrtcserver.dto.UserReference;
import de.marcschuler.webrtcserver.service.CryptoService;
import de.marcschuler.webrtcserver.webclient.events.peer.IceServerData;
import de.marcschuler.webrtcserver.webclient.events.server.ServerTreeChangeEvent;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.PublicKey;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ServerMapper {

    @Autowired
    private CryptoService cryptoService;

    public abstract ServerTreeChangeEvent mapToDTO(Server server);

    public abstract SectionReference mapToDTO(Section section);

    public abstract ChannelReference mapToDTO(Channel channel);

    public abstract UserReference mapToDTO(User user);

    public abstract IceServerData.IceServer mapToDTO(WebRTConfig.IceConfig iceConfig);

    public abstract List<IceServerData.IceServer> mapToDTO(List<WebRTConfig.IceConfig> iceConfig);

    JWK mapKeyToJWK(PublicKey key) {
        return this.cryptoService.exportPublicKey(key);
    }

    JsonNode mapKeyToJSON(PublicKey key) throws JsonProcessingException {
        return this.cryptoService.exportPublicKeyToJSON(key);
    }
}
