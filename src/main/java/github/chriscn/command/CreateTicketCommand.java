package github.chriscn.command;

import github.chriscn.StaffTicket;
import github.chriscn.api.VirtualTicket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateTicketCommand implements CommandExecutor {

    StaffTicket plugin;
    public CreateTicketCommand(StaffTicket instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(plugin.stOpen)) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }

                String msg = sb.toString().trim();

                VirtualTicket ticket = new VirtualTicket(player.getUniqueId(), msg);

                plugin.db.createTicket(ticket);
                player.sendMessage(ChatColor.GREEN + "Generated you a ticket with ID " + ChatColor.YELLOW + ticket.getID());

                plugin.tickets.add(ticket);

            } else {
                player.sendMessage(plugin.NO_PERMISSION);
            }
        } else {
            sender.sendMessage(plugin.NOT_PLAYER);
        }
        return true;
    }
}
