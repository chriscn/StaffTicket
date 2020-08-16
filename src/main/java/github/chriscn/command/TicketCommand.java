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
import java.util.UUID;

public class TicketCommand implements TabExecutor {

    StaffTicket plugin;
    public TicketCommand(StaffTicket instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.PLUGIN_ENABLED) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Check your syntax");
                    return false;
                } else if (args.length == 1) { // list
                    String option = args[0].toLowerCase();

                    switch (option) {
                        case "list":
                            sender.sendMessage(ChatColor.RED + "Implement LIST");
                            break;
                        default:
                            return unknownArgument(player);
                    }
                } else if (args.length == 2) { // review,close,get (stuff where you provide an id)
                    String option = args[0].toLowerCase();
                    String id = args[1].toLowerCase();

                    switch (option) {
                        case "review":
                            if (player.hasPermission(plugin.stReview)) {

                                VirtualTicket ticket = plugin.db.getTicket(id);

                                player.sendMessage("ID " + ticket.getID());
                                player.sendMessage("Timestamp " + ticket.getISO8601() + " Unix Time " + ticket.getTimestamp());
                                player.sendMessage("Player " + ticket.getSenderName());
                                player.sendMessage("Ticket Message " + ticket.getTicketMessage());
                                player.sendMessage("Resolved " + ticket.getResolved());

                            } else noPermission(player);
                        case "close":
                        case "resolve":
                            if (player.hasPermission(plugin.stClose)) {
                                plugin.db.resolveTicket(id, true);
                                player.sendMessage(ChatColor.GREEN + "Resolved ticket with ID " + id);

                            } else noPermission(player);
                        default:
                            return unknownArgument(player);
                    }

                } else {
                    player.sendMessage(ChatColor.RED + "too many arguments");
                    // you fucked up
                }

            } else {
                sender.sendMessage(ChatColor.RED + "Unfortunately, this command can only be used by a Player.");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Plugin is disabled. Check your config and reload it with /staffticket reload");
            return true;
        }
        return false;
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

    private void noPermission(Player player) {
        player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
    }

    private boolean unknownArgument(Player player) {
        player.sendMessage(ChatColor.RED + "Unknown argument");
        return false;
    }
}
