package github.chriscn.command;

import github.chriscn.StaffTicket;
import github.chriscn.api.VirtualTicket;
import net.md_5.bungee.api.ChatColor;
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
        if (plugin.PLUGIN_ENABLED) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Check your syntax");
                    return false;
                } else if (args.length == 1) { // list
                    String option = args[0].toLowerCase();

                    if (option.equalsIgnoreCase("list")) {
                        sender.sendMessage(ChatColor.RED + "Implement LIST");
                    } else {
                        return unknownArgument(player);
                    }

                    return true;
                } else if (args.length == 2) { // review,close (stuff where you provide an id)
                    String option = args[0].toLowerCase();
                    String id = args[1].toLowerCase();

                    switch (option) {
                        case "review":
                            if (player.hasPermission(plugin.stReview)) {
                                if (!plugin.db.ticketExists(id)) {
                                    player.sendMessage(ChatColor.RED + "Unfortunately the ticket with id, " + ChatColor.YELLOW + id + ChatColor.RED + " does not exist.");
                                } else {
                                    VirtualTicket ticket = plugin.db.getTicket(id);

                                    player.sendMessage("ID " + ticket.getID());
                                    player.sendMessage("Timestamp " + ticket.getISO8601());
                                    player.sendMessage("Player " + ticket.getSenderName());
                                    player.sendMessage("Ticket Message " + ticket.getTicketMessage());
                                    player.sendMessage("Resolved " + ticket.getResolved());
                                }
                            } else player.sendMessage(plugin.NO_PERMISSION);
                            break;
                        case "close":
                        case "resolve":
                            if (player.hasPermission(plugin.stClose)) {
                                plugin.db.resolveTicket(id, true);
                                player.sendMessage(ChatColor.GREEN + "Resolved ticket with ID " + id);

                            } else player.sendMessage(plugin.NO_PERMISSION);
                            break;
                        default:
                            return unknownArgument(player);
                    }
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "too many arguments");
                    // you fucked up
                }

            } else {
                sender.sendMessage(plugin.NOT_PLAYER);
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
            ArrayList<String> firstArgument = new ArrayList<>();

            if (sender.hasPermission(plugin.stList)) firstArgument.add("list");
            if (sender.hasPermission(plugin.stReview)) firstArgument.add("review");
            if (sender.hasPermission(plugin.stClose)) firstArgument.add("resolve");

            return firstArgument;
        } else if (args.length == 2) {
            ArrayList<String> ticketID = new ArrayList<>();

            // contact database and get all tickets

            for (VirtualTicket ticket : plugin.tickets) {
                ticketID.add(ticket.getID());
            }

            return ticketID;
        }
        return null;
    }


    private boolean unknownArgument(Player player) {
        player.sendMessage(ChatColor.RED + "Unknown argument");
        return false;
    }
}
