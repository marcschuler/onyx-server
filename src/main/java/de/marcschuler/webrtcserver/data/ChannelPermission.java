package de.marcschuler.webrtcserver.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum ChannelPermission {
    JOIN,
    EDIT,
    EDIT_TITLE(EDIT),
    EDIT_DESCRIPTION(EDIT),
    EDIT_AVATAR(EDIT);


    private ChannelPermission parent;

}
