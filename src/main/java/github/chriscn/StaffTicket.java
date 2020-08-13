package github.chriscn;

import github.chriscn.database.DatabaseManager;
import github.chriscn.database.SQLManager;
import github.chriscn.command.StaffTicketCommand;
import github.chriscn.command.TicketCommand;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class StaffTicket extends JavaPlugin {

    public Permission stOpen = new Permission("staffticket.open");
    public Permission stReview = new Permission("staffticket.review");
    public Permission stClose = new Permission("staffticket.close");

    public final int ID_LENGTH = 8;

    public SQLManager sql;
    public boolean SUCCESSFUL_CONNECTION;

    public DatabaseManager db;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().addPermission(stOpen);
        Bukkit.getPluginManager().addPermission(stReview);
        Bukkit.getPluginManager().addPermission(stClose);

        getConfig().options().copyDefaults(true);
        saveConfig();

        this.sql = new SQLManager(this);

        getCommand("ticket").setExecutor(new TicketCommand(this));
        getCommand("staffticket").setExecutor(new StaffTicketCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // close db connections
        if (SUCCESSFUL_CONNECTION) {
            try {
                sql.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
