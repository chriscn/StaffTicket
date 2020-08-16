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
                } else {
                    String option = args[0].toLowerCase(); // create,review,close,list,get
                    if (option.equalsIgnoreCase("create")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        String msg = sb.toString().trim();

                        VirtualTicket ticket = new VirtualTicket(player.getUniqueId(), msg);

                        plugin.db.createTicket(ticket);

                        // generate ticket
                        player.sendMessage(ChatColor.GREEN + "Generated ticket with ID: " + ChatColor.YELLOW + ticket.getID());
                        return true;
                    } else {
                        String id = args[1].toLowerCase();

                        if (option.equalsIgnoreCase("review")) {
                            VirtualTicket ticket = plugin.db.getTicket(id);

                            player.sendMessage("ID " + ticket.getID());
                            player.sendMessage("Timestamp " + ticket.getISO8601() + " Unix Time " + ticket.getTimestamp());
                            player.sendMessage("Player " + ticket.getSenderName());
                            player.sendMessage("Ticket Message " + ticket.getTicketMessage());
                            player.sendMessage("Resolved " + ticket.getResolved());

                            return true;
                        }/* else if (option.equalsIgnoreCase("close")) {
                        // by closing a ticket you are setting it as resolved it doesn't delete it from the mysql system
                        // TODO test to see if the ticket exists before trying to mark it as resolved

                        if (plugin.db.ticketExists(id)) {
                            plugin.db.resolveTicket(id, true);
                            player.sendMessage(ChatColor.GREEN + "Resolved ticket with ID " + ChatColor.YELLOW + id);
                        } else {
                            player.sendMessage(ChatColor.RED + "I couldn't find that ticket on our system, use " + ChatColor.GREEN + "/ticket list");
                        }
                        return true;
                    } else if (option.equalsIgnoreCase("list")) {
                        String submittedBy = args[2].toLowerCase(); // this doesn't have to be supplied but allows getting
                        if (submittedBy == "" || submittedBy.isEmpty()) {
                            plugin.db.getAllTickets().forEach(virtualTicket -> {
                                player.sendMessage(virtualTicket.getID());
                            });
                            // get all unresolved tickets
                        } else {
                            if (submittedBy.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")) {
                                // they've supplied a uuid for an unknown reason
                            } else {
                                UUID uuid = Bukkit.getPlayer(submittedBy).getUniqueId();

                                plugin.db.getAllTickets(uuid).forEach(virtualTicket -> {
                                    player.sendMessage(virtualTicket.getID());
                                });
                            }

                        }*/
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Unfortunately, this command can only be used by a Player.");
                return true;
            }
            return false;
        } else {
            sender.sendMessage(ChatColor.RED + "Plugin is disabled. Check your config and reload it with /staffchat reload");
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
