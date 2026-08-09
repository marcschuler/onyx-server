package de.marcschuler.onyxserver.data.permission;

public enum PermissionType {
    /*
       SERVER MANAGEMENT
        */
    SERVER,
    SERVER_JOIN,
    SERVER_EDIT,
    SERVER_EDIT_DESCRIPTION,

    SERVER_GROUP_CREATE,
    SERVER_GROUP_EDIT,
    SERVER_GROUP_DELETE,

    SERVER_SECTION_CREATE,
    SERVER_SECTION_MOVE,

    SERVER_INVITE,

    /*
    SECTION MANAGEMENT
     */
    SECTION,
    SECTION_EDIT,
    SECTION_DELETE,

    SECTION_CHANNEL_CREATE,
    SECTION_CHANNEL_ORDER,
    SECTION_CHANNEL_MOVE,

    /*
    CHANNEL MANAGEMENT
     */
    CHANNEL,
    CHANNEL_JOIN,
    CHANNEL_EDIT,
    CHANNEL_DELETE,

    CHANNEL_CHAT,
    CHANNEL_CHAT_CREATE,
    CHANNEL_CHAT_READ,
    CHANNEL_CHAT_DELETE,


    CHANNEL_USER_KICK,

    /*
        USER MANAGEMENT
     */
    USER,
    USER_AVATAR,
    USER_BAN,
    USER_UNBAN,
    USER_ACTIVATE,
    USER_KICK,
    USER_GROUP, //TODO add USER_GROUP_LOWER to allow them to add a group with lower priority?

    /*
        SELF - things users should do
     */
    SELF,
    SELF_AVATAR;


    /**
     * Get the root element, e.g. from CHANNEL_CREATE -> CHANNEL. May return itself.
     *
     * @return the root element or this
     */
    public PermissionType root() {
        if (!name().contains("_"))
            return this;
        return PermissionType.valueOf(name().substring(0, name().indexOf("_")));
    }
}
