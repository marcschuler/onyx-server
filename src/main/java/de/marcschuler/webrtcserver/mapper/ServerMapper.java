package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.policy.Policy;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyDTO;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import de.marcschuler.webrtcserver.webclient.WebClient;
import org.mapstruct.*;
import tools.jackson.databind.JsonNode;
import com.nimbusds.jose.jwk.JWK;
import de.marcschuler.webrtcserver.config.WebRTConfig;
import de.marcschuler.webrtcserver.data.*;
import de.marcschuler.webrtcserver.dto.*;
import de.marcschuler.webrtcserver.dto.data.*;
import de.marcschuler.webrtcserver.service.CryptoService;
import de.marcschuler.webrtcserver.webclient.messages.server.ServerTreeChangeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.core.JacksonException;

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
    /**
     *
     * @param server the server
     * @return the tree WITHOUT populated user lists. See {@link WebSocketService#createServerTreeChangeEvent(WebClient)} ()} for a fully populated list
     */
    @Mapping(target = "server", source = "server")
    @Named("mapToChangeEvent")
    public abstract ServerTreeChangeMessage mapToChangeEvent(Server server);

    @Mapping(target = "chatId", source = "chat.id")
    public abstract ChannelExtendedDTO mapToDTOExtended(Channel channel);

    public abstract SectionExtendedDTO mapToDTOExtended(Section section);


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

    public abstract Channel mapFromDTO(ChannelWriteDTO channel);

    public abstract Channel update(@MappingTarget Channel channel, ChannelWriteDTO channelDTO);

    public abstract UserSimpleDTO mapToDTO(User user);

    public abstract List<UserSimpleDTO> mapToDTOList(List<User> user);

    public abstract UserExtendedDTO mapToDTOExtended(User user);


    public abstract Group mapFromDTO(GroupWriteDTO policyWriteDTO);

    public abstract GroupDTO mapToDTO(Group group);

    public abstract Group update(@MappingTarget Group group, GroupWriteDTO groupWriteDTO);

    public abstract IceServer mapToDTO(WebRTConfig.IceConfig iceConfig);

    public abstract List<IceServer> mapToDTO(List<WebRTConfig.IceConfig> iceConfig);

    /**
     * OTHER
     */
    public abstract FileDTO mapToDTO(File file);

    JWK mapPublicKeyToJWK(KeyPair keyPair) {
        return this.cryptoService.exportPublicKey(keyPair.getPublic());
    }

    JsonNode mapPublicKeyToJWKJson(KeyPair keyPair) throws JacksonException {
        return this.cryptoService.exportPublicKeyToJSON(keyPair.getPublic());
    }

    JWK mapKeyToJWK(PublicKey key) {
        return this.cryptoService.exportPublicKey(key);
    }

    JsonNode mapKeyToJSON(PublicKey key) throws JacksonException {
        return this.cryptoService.exportPublicKeyToJSON(key);
    }

}
