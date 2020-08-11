package github.chriscn.command;

import github.chriscn.StaffTicket;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StaffTicketCommand implements CommandExecutor {

    StaffTicket plugin;
    public StaffTicketCommand(StaffTicket instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String firstOption = args[0].toLowerCase();
        
        return false;
    }
}
