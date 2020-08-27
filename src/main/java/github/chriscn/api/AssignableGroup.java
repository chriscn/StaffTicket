package github.chriscn.api;

import org.bukkit.permissions.Permission;

public class AssignableGroup {

    private Permission permission;
    private int hierarchy;

    public AssignableGroup(String groupName, int hierarchy) {
        this.permission = new Permission("staffticket.group." + groupName);
        this.hierarchy = hierarchy;
    }

    public Permission getPermission() {
        return permission;
    }
}
