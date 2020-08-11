package github.chriscn.command;

import github.chriscn.StaffTicket;
import github.chriscn.api.SQLManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffTicketCommand implements TabExecutor {

    String[] options = {"reload"};
    List<String> commandOptions = Arrays.asList(options);

    StaffTicket plugin;
    public StaffTicketCommand(StaffTicket instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String firstOption = args[0].toLowerCase();

        if (Arrays.asList(args).contains(firstOption)) {
            if (firstOption.equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatColor.GREEN + "Attempting to reload the config.");

                plugin.reloadConfig();
                if (plugin.SUCCESSFUL_CONNECTION) {
                    try {
                        plugin.sql.getConnection().close();
                        sender.sendMessage(ChatColor.GREEN + "Successfully closed the old SQL connection.");
                    } catch (SQLException e) {
                        sender.sendMessage(ChatColor.RED + "Failed to close the SQL Connection. Disabling this plugin.");
                        plugin.getPluginLoader().disablePlugin(plugin);
                    }
                }

                plugin.sql = null; // clears the old sql manager
                plugin.sql = new SQLManager(plugin); // reinitalises the sql manager

                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded!");
            }

            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown Option.");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return commandOptions;
    }
}
