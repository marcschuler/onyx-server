package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.data.ClientState;
import de.marcschuler.onyxserver.dto.data.InviteDTO;
import de.marcschuler.onyxserver.error.InviteException;
import de.marcschuler.onyxserver.repository.InviteRepository;
import de.marcschuler.onyxserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@OnyxTest
@Transactional
@Slf4j
class InviteServiceTest {

    @Autowired
    private InviteService inviteService;

    @Autowired
    private TestService testService;

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private UserRepository userRepository;

    private InviteDTO validDto(String code) {
        var dto = new InviteDTO();
        dto.setCode(code);
        dto.setTitle("Test Invite");
        dto.setEndDate(LocalDateTime.now().plusDays(7));
        return dto;
    }

    @Test
    void create() {
        var invite = inviteService.create(validDto("create-test"));
        assertNotNull(invite);
        assertEquals("create-test", invite.getCode());
        assertEquals("Test Invite", invite.getTitle());
        assertTrue(inviteRepository.existsById("create-test"));
    }

    @Test
    void createDuplicateCode() {
        inviteService.create(validDto("dup"));
        assertThrows(IllegalStateException.class, () -> inviteService.create(validDto("dup")));
    }

    @Test
    void createExpired() {
        var dto = validDto("expired");
        dto.setEndDate(LocalDateTime.now().minusDays(1));
        assertThrows(InviteException.class, () -> inviteService.create(dto));
    }

    @Test
    void enterInvalidCode() {
        var user = testService.userAdmin();
        assertThrows(InviteException.class, () -> inviteService.enterInviteCode(user, "no-such-code"));
    }

    @Test
    void enterBannedUser() {
        var user = testService.userAdmin();
        user.setState(ClientState.BANNED);
        userRepository.save(user);

        inviteService.create(validDto("ban-test"));

        var banned = userRepository.findById(TestService.USER_ADMIN_ID).orElseThrow();
        assertThrows(InviteException.class, () -> inviteService.enterInviteCode(banned, "ban-test"));
    }

    @Test
    void enterNotYetActive() {
        var dto = validDto("future");
        dto.setStartDate(LocalDateTime.now().plusDays(1));
        inviteService.create(dto);

        var user = testService.userAdmin();
        assertThrows(InviteException.class, () -> inviteService.enterInviteCode(user, "future"));
    }

    @Test
    void enterMaxUsagesReached() {
        var dto = validDto("maxed");
        dto.setMaxUsages(1);
        dto.setUsages(1);
        inviteService.create(dto);

        var user = testService.userAdmin();
        assertThrows(InviteException.class, () -> inviteService.enterInviteCode(user, "maxed"));
    }

    @Test
    void enterActivatesPendingUser() {
        var user = testService.userAdmin();
        user.setState(ClientState.PENDING_ACCESS);
        userRepository.save(user);

        inviteService.create(validDto("activate"));

        var pending = userRepository.findById(TestService.USER_ADMIN_ID).orElseThrow();
        inviteService.enterInviteCode(pending, "activate");

        var activated = userRepository.findById(TestService.USER_ADMIN_ID).orElseThrow();
        assertEquals(ClientState.ACTIVE, activated.getState());
    }

    @Test
    void enterAddsGroups() {
        var modGroupId = UUID.fromString("d8ccd166-2556-48c2-b5bd-c7d42995a2db");
        var dto = validDto("group-invite");
        dto.setGroups(List.of(modGroupId));
        inviteService.create(dto);

        var user = testService.userAdmin();
        inviteService.enterInviteCode(user, "group-invite");

        var updated = userRepository.findById(TestService.USER_ADMIN_ID).orElseThrow();
        assertTrue(updated.getGroups().stream().anyMatch(g -> g.getId().equals(modGroupId)));
    }

    @Test
    void enterAlreadyActiveNoChange() {
        var user = testService.userAdmin();
        inviteService.create(validDto("noop"));
        assertThrows(InviteException.class, () -> inviteService.enterInviteCode(user, "noop"));
    }
}
