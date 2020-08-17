package github.chriscn.command;

import github.chriscn.StaffTicket;
import github.chriscn.api.VirtualTicket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StaffTicketCommand implements TabExecutor {

    String[] options = {"reload", "status", "debug"};
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
                sender.sendMessage(ChatColor.GREEN + "Attempting to reload the plugin.");

                plugin.reloadPlugin();

                sender.sendMessage("Plugin reloaded");
            } else {
                if (plugin.PLUGIN_ENABLED) {
                    if (firstOption.equalsIgnoreCase("debug")) {
                        VirtualTicket ticket = new VirtualTicket(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "This is a debug ticket.");
                        sender.sendMessage("Generated new ticket with id " + ticket.getID());
                        plugin.db.createTicket(ticket);
                        sender.sendMessage("Submitted ticket to database");

                        VirtualTicket dbTicket = plugin.db.getTicket(ticket.getID());
                        sender.sendMessage("Got ticket from db with id " + dbTicket.getID());
                        sender.sendMessage(
                                        dbTicket.getID() + "\n" +
                                        dbTicket.getTicketMessage() + "\n" +
                     //                   dbTicket.getSenderName() + "\n" +
                                        dbTicket.getISO8601() + "\n" +
                                        dbTicket.getResolved()
                                );

                        sender.sendMessage("Resolving this ticket");
                        plugin.db.resolveTicket(dbTicket.getID(), true);

                        sender.sendMessage("Does this ticket exist (true) " + plugin.db.ticketExists(ticket.getID()));

                        sender.sendMessage("Getting all tickets");
                        for (VirtualTicket alltickets : plugin.db.getAllTickets()) {
                            sender.sendMessage(alltickets.getID());
                        }
                        return true;

                    }
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
