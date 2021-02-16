package github.chriscn;

import github.chriscn.api.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class StaffTicket extends JavaPlugin {

    PluginDescriptionFile pdfFile = this.getDescription();
    public MySQL mySQL;

    @Override
    public void onEnable() {
        try {
            this.mySQL = new MySQL("localhost", 3306, "staffticket", "root", "");

            mySQL.openConnection();
        } catch (ClassNotFoundException e) {
            getLogger().severe("Unable to find the MySQL class.");
            getLogger().severe(e.getMessage());

            Bukkit.getPluginManager().disablePlugin(this);
        } catch (SQLException e) {
            getLogger().info("Unable to connect to the database.");
            getLogger().info(e.getMessage());

            Bukkit.getPluginManager().disablePlugin(this);
        }

        try {
            ArrayList<UUID> players = mySQL.testingDB();

            for (UUID player : players) {
                getLogger().info(player.toString());
            }
        } catch (SQLException e) {
            getLogger().info(e.getMessage());
        }
    }
}
