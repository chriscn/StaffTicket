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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.PLUGIN_ENABLED) {
                if (args.length < 1) {
                    return false;
                } else {
                    String option = args[0];
                    if (plugin.permissionOptions.containsKey(option)) {
                        if (player.hasPermission(plugin.permissionOptions.get(option))) {
                            if (option.equalsIgnoreCase("create")) {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }

                                VirtualTicket ticket = new VirtualTicket(player.getUniqueId(), sb.toString().trim());
                                plugin.db.createTicket(ticket);

                                player.sendMessage(ChatColor.GREEN + "Created you a ticket with id, " + ChatColor.YELLOW + ticket.getID());
                                return true;
                            } else {
                                if (option.equalsIgnoreCase("assign")) {
                                    if (args.length == 3) {
                                        // TODO implement assign logic
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    if (args.length == 2) {
                                        String id = args[1];
                                        if (option.equalsIgnoreCase("review")) {
                                            VirtualTicket review = plugin.db.getTicket(id);
                                            player.sendMessage(ChatColor.GREEN + review.getID() + ": " + review.getTicketMessage());
                                            player.sendMessage("Made at " + review.getISO8601());
                                        } else if (option.equalsIgnoreCase("resolve")) {
                                            plugin.db.resolveTicket(id, true);
                                            player.sendMessage("Resolved ticket, " + id + ".");
                                        }
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        } else {
                            player.sendMessage(plugin.NO_PERMISSION);
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                player.sendMessage(plugin.PLUGIN_DISABLED);
                return true;
            }
        } else {
            sender.sendMessage(plugin.NOT_PLAYER);
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> options = new ArrayList<>();

            plugin.permissionOptions.inverse().forEach((perm, option) -> {
                if (sender.hasPermission(perm)) {
                    options.add(option);
                }
            });

            return options;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("assign")) {
                ArrayList<String> groups = new ArrayList<>();

                for (String group : plugin.groups.keySet()) {
                    groups.add(group);
                }

                return groups;
            }
        }
        return null;
    }
}
