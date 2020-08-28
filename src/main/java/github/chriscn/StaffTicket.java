package github.chriscn;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import github.chriscn.api.VirtualTicket;
import github.chriscn.database.DatabaseManager;
import github.chriscn.database.HikariCP;
import github.chriscn.command.StaffTicketCommand;
import github.chriscn.command.TicketCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class StaffTicket extends JavaPlugin {

    public Permission stCreate = new Permission("staffticket.create");
    public Permission stReview = new Permission("staffticket.review");
    public Permission stList = new Permission("staffticket.list");
    public Permission stResolve = new Permission("staffticket.resolve");
    public Permission stAssign = new Permission("staffticket.assign");

    public BiMap<String, Permission> permissionOptions = HashBiMap.create();

    public String NOT_PLAYER = ChatColor.RED + "You must be a player to use this command.";
    public String NO_PERMISSION = ChatColor.RED + "You do not have permission to use this command";
    public String PLUGIN_DISABLED = ChatColor.RED + "Uh oh! This plugin is disabled, ask an administrator to check the config and then run " + ChatColor.YELLOW + "/st reload";


    public static final int ID_LENGTH = 8;

    public boolean PLUGIN_ENABLED;

    public DatabaseManager db;
    public FileConfiguration config;

    public ArrayList<VirtualTicket> tickets = new ArrayList<>();
    public BiMap<String, Permission> groups = HashBiMap.create();

    public String host;
    public int port;
    public String database;
    public String username;
    public String password;
    public String table;

    @Override
    public void onEnable() {
        // Plugin startup logic
        permissionOptions.put("create", stCreate);
        permissionOptions.put("review", stReview);
        permissionOptions.put("list", stList);
        permissionOptions.put("resolve", stResolve);
        permissionOptions.put("assign", stAssign);

        for (Permission perm : permissionOptions.values()) {
            Bukkit.getPluginManager().addPermission(perm);
        }

        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("ticket").setExecutor(new TicketCommand(this));
        getCommand("staffticket").setExecutor(new StaffTicketCommand(this));

        reloadPlugin();

        // fetch tickets from database
        if (this.PLUGIN_ENABLED) this.tickets = this.db.getAllTickets();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            tickets = null;
            if (PLUGIN_ENABLED) tickets = db.getAllTickets();
        }, 0L, config.getInt("refresh-time") * 20);
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

        for (String s : config.getStringList("assignable-groups")) {
            groups.put(s, new Permission("staffticket.group." + s));
        }

        this.tickets = null;
        if(this.PLUGIN_ENABLED) this.tickets = this.db.getAllTickets();
    }

    private void setupStorageMethod() {
        String storageMethod = config.getString("storage-method").toLowerCase();
        this.host = config.getString("database.address");
        this.port = config.getInt("database.port");
        this.database = config.getString("database.database");
        this.username = config.getString("database.username");
        this.password = config.getString("database.password");
        this.table = "staffticket";

        switch (storageMethod) {
            case "mysql":
                this.db = new HikariCP(this);
                break;
            default:
                getLogger().info("Unknown storage-method, " + storageMethod + ", check your configuration file.");
                getLogger().info("Soft disabling the plugin. Once you've updated your config run /st reload");
                this.PLUGIN_ENABLED = false;
        }
    }

    private void closeConnection() {
        if (PLUGIN_ENABLED) {
            try {
                db.closeConnection();
            } catch (Exception e) {
                getLogger().severe("Error when trying to close Database connection.");
                e.printStackTrace();
            }
        }
    }
}
