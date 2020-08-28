package github.chriscn.api;

import github.chriscn.StaffTicket;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public abstract class STCommandExecutor implements TabExecutor {

    StaffTicket plugin;
    public STCommandExecutor(StaffTicket instance) {
        this.plugin = instance;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.PLUGIN_ENABLED) {
                // handle logic
            } else {
                player.sendMessage(plugin.PLUGIN_DISABLED);
            }
        } else {
            sender.sendMessage(plugin.NOT_PLAYER);
        }
        return false;
    }
}
