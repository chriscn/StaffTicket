package github.chriscn.database;

import github.chriscn.StaffTicket;
import github.chriscn.api.VirtualTicket;

public class MySQL implements DatabaseManager {

    private final StaffTicket plugin;

    public MySQL(StaffTicket instance) {
        this.plugin = instance;
    }

    @Override
    public void setupDatabase() {

    }

    @Override
    public void createTicket(VirtualTicket ticket) {

    }

    @Override
    public void clearCache() {

    }

    @Override
    public boolean ticketExits(VirtualTicket ticket) {
        return false;
    }
}
