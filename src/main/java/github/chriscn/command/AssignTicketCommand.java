package github.chriscn.command;

import github.chriscn.StaffTicket;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class AssignTicketCommand implements TabExecutor {

    StaffTicket plugin;
    public AssignTicketCommand(StaffTicket instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.PLUGIN_ENABLED) {

            } else {

            }

        } else {
            sender.sendMessage(plugin.NOT_PLAYER);
            return true;
        }


        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
