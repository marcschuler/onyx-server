package de.marcschuler.onyxserver.config.liquibase;

import de.marcschuler.onyxserver.Util;
import de.marcschuler.onyxserver.service.CryptoService;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Second half of the changeSet {@code 002-default-server}: creates everything of the default server
 * that must be unique per installation. The static rows are inserted by {@code data/002_default-server.sql}.
 * <ul>
 *     <li>the server key pair (signs the JWTs)</li>
 *     <li>a single-use admin invite, valid for 14 days, printed to the log</li>
 * </ul>
 */
@Slf4j
public class DefaultServerSecretsChange implements CustomTaskChange {

    /**
     * Ids from data/002_default-server.sql
     */
    static final UUID SERVER_ID = UUID.fromString("0e5e7a1c-0000-4000-8000-000000000001");
    static final UUID ADMIN_GROUP_ID = UUID.fromString("0e5e7a1c-0000-4000-8000-000000000002");

    @Override
    public void execute(Database database) throws CustomChangeException {
        var cryptoService = LiquibaseSpringContext.getBean(CryptoService.class);
        var connection = ((JdbcConnection) database.getConnection()).getUnderlyingConnection();
        try {
            var keys = cryptoService.generateKeyPair();
            try (var stmt = connection.prepareStatement("UPDATE SERVER SET KEYS = ? WHERE ID = ?")) {
                stmt.setString(1, keys.toJSONString());
                stmt.setObject(2, SERVER_ID);
                if (stmt.executeUpdate() != 1)
                    throw new CustomChangeException("Default server " + SERVER_ID + " not found");
            }

            var code = Util.randomCode(16);
            try (var stmt = connection.prepareStatement(
                    "INSERT INTO INVITE (CODE, TITLE, END_DATE, MAX_USAGES, USAGES, SERVER_ID) VALUES (?, ?, ?, 1, 0, ?)")) {
                stmt.setString(1, code);
                stmt.setString(2, "Admin Invite");
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusDays(14)));
                stmt.setObject(4, SERVER_ID);
                stmt.executeUpdate();
            }
            try (var stmt = connection.prepareStatement("INSERT INTO INVITE_GROUPS (INVITE_CODE, GROUPS_ID) VALUES (?, ?)")) {
                stmt.setString(1, code);
                stmt.setObject(2, ADMIN_GROUP_ID);
                stmt.executeUpdate();
            }
            try (var stmt = connection.prepareStatement("INSERT INTO SERVER_INVITES (SERVER_ID, INVITES_CODE) VALUES (?, ?)")) {
                stmt.setObject(1, SERVER_ID);
                stmt.setString(2, code);
                stmt.executeUpdate();
            }

            log.info("Server with id '{}' and public key '{}' generated", SERVER_ID, keys.toPublicJWK().toJSONString());
            log.info(" ---------- ADMIN CODE ----------");
            log.info("Your Admin Invite Code is: {}", code);
            log.info("Enter it in your app. Do not share it with anyone");
            log.info(" ---------- ADMIN CODE ----------");
        } catch (SQLException e) {
            throw new CustomChangeException("Could not create the default server secrets", e);
        }
    }

    @Override
    public String getConfirmationMessage() {
        return "Default server created";
    }

    @Override
    public void setUp() {
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
    }

    @Override
    public ValidationErrors validate(Database database) {
        return new ValidationErrors();
    }
}
