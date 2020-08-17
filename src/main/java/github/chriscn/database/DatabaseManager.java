package github.chriscn.database;

import github.chriscn.api.VirtualTicket;

import java.util.ArrayList;

public interface DatabaseManager {
    void closeConnection();

    void createTicket(VirtualTicket ticket);
    void resolveTicket(String id, boolean resolved);

    boolean ticketExists(String id);
    VirtualTicket getTicket(String id);

    ArrayList<VirtualTicket> getAllTickets();
    ArrayList<VirtualTicket> getResolvedTickets(boolean resolved);
}
