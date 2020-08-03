package github.chriscn.staffticket;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public final class StaffTicket extends JavaPlugin {

    public Permission stOpen = new Permission("staffticket.open");
    public Permission stHelper = new Permission("staffticket.helper");
    public Permission stModerator = new Permission("staffticket.moderator");
    public Permission stAdmin = new Permission("staffticket.admin");
    
    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().addPermission(stOpen);
        Bukkit.getPluginManager().addPermission(stHelper);
        Bukkit.getPluginManager().addPermission(stModerator);
        Bukkit.getPluginManager().addPermission(stAdmin);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
