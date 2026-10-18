package de.marcschuler.onyxserver.integration;

import com.nimbusds.jose.JOSEException;
import de.marcschuler.onyxserver.IntegrationHelper;
import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.WebSocketMock;
import de.marcschuler.onyxserver.data.Channel;
import de.marcschuler.onyxserver.repository.ChannelRepository;
import de.marcschuler.onyxserver.service.ChannelService;
import de.marcschuler.onyxserver.service.CryptoService;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChannelJoinEvent;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChannelJoinRequest;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChannelLeaveEvent;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChannelLeaveRequest;
import de.marcschuler.onyxserver.webclient.messages.error.NoPermissionMessage;
import de.marcschuler.onyxserver.webclient.messages.peer.IceServerMessage;
import de.marcschuler.onyxserver.webclient.messages.server.ServerTreeChangeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
public class SimpleConnectionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IntegrationHelper integrationHelper;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelRepository channelRepository;

    private WebSocketMock client;

    @BeforeEach
    void setup() throws IOException, SignatureException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, TimeoutException, JOSEException {
        client = integrationHelper.quickConnect("user", port);
    }

    @AfterEach
    void cleanup() throws IOException {
        client.close();
    }

    @Test
    void testLoginAndReceiveServerTree() throws InterruptedException {
        client.recv(IceServerMessage.class);
        var tree = client.recv(ServerTreeChangeMessage.class);
        assertNotNull(tree, "tree exists");
        assertEquals(3, tree.sections().size(), "three sections");
    }

    @Test
    void testJoinAndLeaveChannel() throws InterruptedException, JacksonException {
        var keyId = cryptoService.generateKeyId(client.getKeyPair());

        //login
        client.recv(IceServerMessage.class);
        var tree = client.recv(ServerTreeChangeMessage.class);


        var firstChannel = tree.sections().getFirst().getChannels().getFirst();
        client.sendMessage(new ClientChannelJoinRequest(firstChannel.getId()));

        //join channel
        var joinEvent = client.recv(ClientChannelJoinEvent.class);
        assertNotNull(joinEvent.userId());
        assertEquals(keyId, joinEvent.userId());
        assertEquals(firstChannel.getId(), joinEvent.channelId());

        //joining the same channel should do nothing
        client.sendMessage(new ClientChannelJoinRequest(firstChannel.getId()));
        client.recvNothing();

        //leave the channel
        client.sendMessage(new ClientChannelLeaveRequest());
        var leaveEvent = client.recv(ClientChannelLeaveEvent.class);
        assertNotNull(leaveEvent.userId());
        assertEquals(keyId, leaveEvent.userId());

        //try to leave channel again
        client.sendMessage(new ClientChannelLeaveRequest());
        client.recvNothing();
    }

    @Test
    void testJoinChannelVoiceModeNone() throws InterruptedException, JacksonException {
        var channel = channelService.get(TestService.CHANNEL_LOBBY_ID).orElseThrow();
        channel.setVoiceMode(Channel.VoiceMode.NONE);
        channelRepository.save(channel);

        client.recv(IceServerMessage.class);
        var tree = client.recv(ServerTreeChangeMessage.class);

        var firstChannel = tree.sections().getFirst().getChannels().getFirst();
        client.sendMessage(new ClientChannelJoinRequest(firstChannel.getId()));

        var error = client.recv(NoPermissionMessage.class);
        assertNotNull(error);
    }
}
