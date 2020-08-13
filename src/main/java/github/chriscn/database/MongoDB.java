package github.chriscn.database;

import github.chriscn.api.VirtualTicket;

public class MongoDB implements DatabaseManager {

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
