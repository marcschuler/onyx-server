package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import de.marcschuler.webrtcserver.data.Permission;
import de.marcschuler.webrtcserver.data.policy.AccessPowerPolicy;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import de.marcschuler.webrtcserver.service.policy.PolicyCheckerContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@OnyxTest
@Slf4j
class PolicyServiceTest {

    private static final AccessPowerPolicy POLICY_AP_100 = new AccessPowerPolicy();

    @Autowired
    private PolicyService policyService;

    @Autowired
    private TestService testService;

    @BeforeAll
    static void setUp() {
        POLICY_AP_100.setAccessPower(100);
        POLICY_AP_100.setId(UUID.randomUUID());
        POLICY_AP_100.setName("Access Power 100");
    }

    @Test
    @Disabled("define channel rules first")
    void testDenyNoRulesForChannel() throws PolicyCheckException {
        assertDoesNotThrow(() -> policyService.checkAccess(List.of(), new PolicyCheckerContext(Permission.PermissionType.CHANNEL, testService.userAdmin(), testService.channelLobby(), Map.of())));
    }

    @Test
    @Disabled("define channel rules first")
    void testSimpleAdminChannelPolicy() throws PolicyCheckException {
        assertDoesNotThrow(() -> policyService.checkAccess(List.of(POLICY_AP_100), new PolicyCheckerContext(Permission.PermissionType.CHANNEL, testService.userAdmin(), testService.channelLobby(), Map.of())));
    }

}