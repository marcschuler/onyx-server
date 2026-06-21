package de.marcschuler.webrtcserver.config.starter;

import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.data.permission.Permission;
import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.dto.GroupCreateDTO;
import de.marcschuler.webrtcserver.dto.data.InviteDTO;
import de.marcschuler.webrtcserver.error.webclient.NoClientException;
import de.marcschuler.webrtcserver.service.GroupService;
import de.marcschuler.webrtcserver.service.InviteService;
import de.marcschuler.webrtcserver.service.ServerService;
import de.marcschuler.webrtcserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminResetStartupRunner implements CommandLineRunner {

    private static final String OPTION_EMERGENCY_GRANT_ADMIN_POWER = "emergency-grant-admin-power";

    private final ServerService serverService;
    private final GroupService groupService;
    private final InviteService inviteService;

    @Override
    public void run(String... args) throws Exception {
        log.debug("Command line is {}", Arrays.toString(args));
        var options = new Options();
        options.addOption(Option.builder()
                .longOpt(OPTION_EMERGENCY_GRANT_ADMIN_POWER)
                .hasArg(false)
                .desc("Creates an emergency administrator group you can use to regain access to the server")
                .get());

        var parser = new DefaultParser();
        ;
        var line = parser.parse(options, args, false);

        if (line.hasOption(OPTION_EMERGENCY_GRANT_ADMIN_POWER)) {
            log.info("Creating an emergency admin group to recover your admin rights");

            var permission = new Permission();
            permission.setPermissions(Set.of(PermissionType.SERVER, PermissionType.SECTION, PermissionType.CHANNEL, PermissionType.USER, PermissionType.SELF));
            permission.setPriority(Integer.MAX_VALUE);

            var groupDto = new GroupCreateDTO();
            groupDto.setName("Admin (Emergency Grant " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ")");
            groupDto.setDescription("Emergency administrator group to regain access to the server");
            groupDto.setLabel(true);
            groupDto.setDefaultForNewUsers(false);
            var group = groupService.create(groupDto);
            group.setPermissions(List.of(permission));
            var server = serverService.defaultServer();
            server.getGroups().add(group);
            serverService.save(server);

            var inviteDto = new InviteDTO();
            inviteDto.setCode(Util.randomCode(16));
            inviteDto.setTitle("Emergency Admin Group");
            inviteDto.setUsages(1);
            inviteDto.setEndDate(LocalDateTime.now().plusDays(14));
            inviteDto.setGroups(List.of(group.getId()));

            var invite = inviteService.create(inviteDto);

            server.setInvites(List.of(invite));
            log.info(" ---------- START RECOVERY CODE ----------");
            log.info("Your recovery code is: {}", invite.getCode());
            log.info("Enter it in your app. Do not share it with anyone");
            log.info(" ---------- END RECOVERY CODE ----------");
        }
    }
}
