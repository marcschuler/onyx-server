package de.marcschuler.webrtcserver.data;

import de.marcschuler.webrtcserver.data.policy.PolicyItem;

import java.util.Map;

/**
 * Defines an class that has policy access
 */
public interface Policyable {

    Map<Permission.PermissionType, PolicyItem> getPolicies();
}
