package github.chriscn.command;

import github.chriscn.StaffTicket;
import github.chriscn.api.VirtualTicket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TicketCommand implements TabExecutor {

    StaffTicket plugin;
    public TicketCommand(StaffTicket instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Check your syntax");
                return false;
            } else {
                String option = args[0].toLowerCase(); // create,review
                if (option.equalsIgnoreCase("create")) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String msg = sb.toString().trim();

                    VirtualTicket ticket = new VirtualTicket(player.getUniqueId(), msg);

                    plugin.sql.submitTicket(ticket);

                    // generate ticket
                    player.sendMessage(ChatColor.GREEN + "Generated ticket with ID: " + ChatColor.YELLOW + ticket.getID());
                    player.sendMessage("msg " + msg);
                    return true;
                } else if (option.equalsIgnoreCase("review")) {
                    player.sendMessage("getting review");
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> player.sendMessage("async ran"));
                    return true;
                } else {
                    player.sendMessage("unknown argument");
                    // unknown option
                    return false;
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Unfortunately, this command can only be used by a Player.");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("create");
            if (sender.hasPermission(plugin.stReview)) {
                options.add("review");
            }
            return options;
        }
        return null;
    }
}
