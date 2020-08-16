package github.chriscn.database;

import github.chriscn.api.VirtualTicket;

import java.sql.SQLException;

public interface DatabaseManager {
    void closeConnection();

    void createTicket(VirtualTicket ticket);
    void resolveTicket(String id, boolean resolved);

    boolean ticketExists(VirtualTicket ticket);
    VirtualTicket getTicket(String id);
}
