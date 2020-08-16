package github.chriscn.command;

import github.chriscn.StaffTicket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.List;

public class StaffTicketCommand implements TabExecutor {

    String[] options = {"reload", "status"};
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

                plugin.reloadPlugin();
                
                sender.sendMessage("Plugin reloaded");
            } else {
                if (plugin.PLUGIN_ENABLED) {

                } else {
                    sender.sendMessage(ChatColor.RED + "Plugin disabled, check your config and use /staffchat reload");
                }
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
