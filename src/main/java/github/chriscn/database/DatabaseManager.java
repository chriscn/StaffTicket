package github.chriscn.database;

import github.chriscn.api.VirtualTicket;

public interface DatabaseManager {
    void setupDatabase();
    void createTicket(VirtualTicket ticket);
    boolean ticketExits(VirtualTicket ticket);
}
