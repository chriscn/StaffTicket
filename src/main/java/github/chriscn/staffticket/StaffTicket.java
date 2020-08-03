package github.chriscn.staffticket;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public final class StaffTicket extends JavaPlugin {

    public Permission stOpen = new Permission("staffticket.open");
    public Permission stHelper = new Permission("staffticket.helper");
    public Permission stModerator = new Permission("staffticket.moderator");
    public Permission stAdmin = new Permission("staffticket.admin");
    
    public final int ID_LENGTH = 8;

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().addPermission(stOpen);
        Bukkit.getPluginManager().addPermission(stHelper);
        Bukkit.getPluginManager().addPermission(stModerator);
        Bukkit.getPluginManager().addPermission(stAdmin);

        getCommand("ticket").setExecutor(new TicketCommand(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public String generateID() {
        char[] availableChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

        Random random = new Random();
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < ID_LENGTH; i++) {
            id.append(availableChars[random.nextInt(availableChars.length)]);
        }

        return id.toString().trim();
    }
}
