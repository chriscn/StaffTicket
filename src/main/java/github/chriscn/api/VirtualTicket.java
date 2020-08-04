package github.chriscn.api;

import github.chriscn.StaffTicket;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class VirtualTicket {

    private String id;
    private long timestamp;
    private UUID uuid;
    private String ticketMessage;
    private boolean resolved;

    // this is for creating the ticket when using /ticket create
    // haven't yet decided how their gonna implement the method
    public VirtualTicket(UUID uuid, String ticketMessage, boolean resolved) {
        this.id = new StaffTicket().generateID();
        this.timestamp = Instant.now().getEpochSecond();
        this.uuid = uuid;
        this.ticketMessage = ticketMessage;
        this.resolved = resolved;
    }

    public VirtualTicket(String id) {
        // if just given the id, when player uses /ticket review <id> then fetch all the details of the ticket
    }

    public void postToDatabase() {
        String insert = "INSERT INTO staffticket(id,timestamp,uuid,message,resolved) VALUES (?, ?, ?, ?, ?)";
        Connection connection = new StaffTicket().sqlManager.connection;
        try {
            PreparedStatement ps = connection.prepareStatement(insert);
            ps.setString(1, this.id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSenderName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(this.uuid);
        return player.getName();
    }

    public void updateResolved(boolean resolved) {
        // connect to the db in order to update resolved
    }

    public String getID() {
        return this.id;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getISO8601() {
        // 2020-08-04T10:53:47Z an example of the date that should be produced
        Instant is = Instant.ofEpochSecond(this.timestamp);
        return is.toString();
    }

    public UUID getPlayerUUID() {
        return this.uuid;
    }

    public boolean getResolved() {
        return this.resolved;
    }
}
