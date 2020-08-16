package github.chriscn;

import github.chriscn.command.CreateTicketCommand;
import github.chriscn.database.DatabaseManager;
import github.chriscn.database.MySQL;
import github.chriscn.command.StaffTicketCommand;
import github.chriscn.command.TicketCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public final class StaffTicket extends JavaPlugin {

    public Permission stOpen = new Permission("staffticket.open");
    public Permission stReview = new Permission("staffticket.review");
    public Permission stList = new Permission("staffticket.list");
    public Permission stClose = new Permission("staffticket.close");

    public String NOT_PLAYER = ChatColor.RED + "You must be a player to use this command.";
    public String NO_PERMISSION = ChatColor.RED + "You do not have permission to use this command";

    public final int ID_LENGTH = 8;

    public boolean SUCCESSFUL_CONNECTION;
    public boolean PLUGIN_ENABLED;

    public DatabaseManager db;
    public FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().addPermission(stOpen);
        Bukkit.getPluginManager().addPermission(stReview);
        Bukkit.getPluginManager().addPermission(stClose);

        getConfig().options().copyDefaults(true);
        saveConfig();

        this.config = getConfig();
        this.PLUGIN_ENABLED = true;
        
        getCommand("ticket").setExecutor(new TicketCommand(this));
        getCommand("createticket").setExecutor(new CreateTicketCommand(this));
        getCommand("staffticket").setExecutor(new StaffTicketCommand(this));

        setupStorageMethod();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // close db connections
        closeConnection();
    }

    public void reloadPlugin() {
        closeConnection();
        this.db = null;

        reloadConfig();
        this.config = this.getConfig(); // refresh the config

        setupStorageMethod();
    }

    private void setupStorageMethod() {
        String storageMethod = config.getString("storage-method").toLowerCase();

        switch (storageMethod) {
            case "mysql":
                this.db = new MySQL(this);
                break;
            default:
                getLogger().info("Unknown storage-method, " + storageMethod + ", check your configuration file.");
                getLogger().info("Soft disabling the plugin.");
                this.PLUGIN_ENABLED = false;
        }
    }

    private void closeConnection() {
        if (SUCCESSFUL_CONNECTION) {
            try {
                db.closeConnection();
            } catch (Exception e) {
                getLogger().severe("Error when trying to close Database connection.");
                e.printStackTrace();
            }
        }
    }
}
