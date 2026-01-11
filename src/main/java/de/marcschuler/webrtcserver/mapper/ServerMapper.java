package de.marcschuler.webrtcserver.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.jwk.JWK;
import de.marcschuler.webrtcserver.config.WebRTConfig;
import de.marcschuler.webrtcserver.data.*;
import de.marcschuler.webrtcserver.dto.*;
import de.marcschuler.webrtcserver.dto.data.*;
import de.marcschuler.webrtcserver.service.CryptoService;
import de.marcschuler.webrtcserver.webclient.messages.server.ServerTreeChangeMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.List;

@Mapper(componentModel = "spring", uses = MessageMapper.class)
public abstract class ServerMapper {

    @Autowired
    private CryptoService cryptoService;

    /**
     * SERVER
     */
    @Mapping(target = "server", source = "server")
    public abstract ServerTreeChangeMessage mapToChangeEvent(Server server);

    @Mapping(target = "publicKey", source = "keys")
    public abstract ServerDTO mapToDTO(Server server);

    public abstract Server update(@MappingTarget Server server, ServerWriteDTO dto);

    /**
     * SECTIONS
     */
    public abstract Section mapFromDTO(SectionDTO channel);

    public abstract Section mapFromDTO(SectionWriteDTO channel);

    public abstract Section update(@MappingTarget Section section, SectionWriteDTO dto);

    public abstract SectionDTO mapToDTO(Section section);

    @Mapping(target = "chatId", source = "chat.id")
    public abstract ChannelDTO mapToDTO(Channel channel);

    public abstract Channel mapFromDTO(ChannelDTO channel);

    public abstract UserSimpleDTO mapToDTO(User user);

    public abstract IceServer mapToDTO(WebRTConfig.IceConfig iceConfig);

    public abstract List<IceServer> mapToDTO(List<WebRTConfig.IceConfig> iceConfig);

    /**
     * OTHER
     */

    JWK mapPublicKeyToJWK(KeyPair keyPair) {
        return this.cryptoService.exportPublicKey(keyPair.getPublic());
    }

    JsonNode mapPublicKeyToJWKJson(KeyPair keyPair) throws JsonProcessingException {
        return this.cryptoService.exportPublicKeyToJSON(keyPair.getPublic());
    }

    JWK mapKeyToJWK(PublicKey key) {
        return this.cryptoService.exportPublicKey(key);
    }

    JsonNode mapKeyToJSON(PublicKey key) throws JsonProcessingException {
        return this.cryptoService.exportPublicKeyToJSON(key);
    }
}
