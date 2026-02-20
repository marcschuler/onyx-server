package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class Permission implements Serializable {
    private PermissionType type;

    public enum PermissionType{
        CHANNEL_JOIN,
        CHANNEL_EDIT,
        CHANNEL_EDIT_TITLE,
        CHANNEL_EDIT_DESCRIPTION,
        CHANNEL_EDIT_AVATAR,
        CHANNEL_DELETE;
    }
}
