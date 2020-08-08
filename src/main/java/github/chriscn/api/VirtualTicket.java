package github.chriscn.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public class VirtualTicket {

    private final String id;
    private final long timestamp;
    private final UUID uuid;
    private final String ticketMessage;
    private final boolean resolved;

    /**
     * Generates a new VirtualTicket with a unique ID and the current unix timestamp when called.
     * @param uuid The UUID of the person that has opened the Ticket
     * @param ticketMessage The message supplied by opening the ticket
     */
    public VirtualTicket(UUID uuid, String ticketMessage) {
        this.id = this.generateID();
        this.timestamp = Instant.now().getEpochSecond();
        this.uuid = uuid;
        this.ticketMessage = ticketMessage;
        this.resolved = false;
    }

    /**
     * Creating a VirtualTicket from information from the backend database.
     * @param id 8 Character ID
     * @param timestamp A Unix timestamp
     * @param uuid UUID of the person who generated the ticket
     * @param ticketMessage Message contained within the ticket
     * @param resolved Whether or not the ticket has been resolved
     */
    public VirtualTicket(String id, long timestamp, String uuid, String ticketMessage, boolean resolved) {
        this.id = id;
        this.timestamp = timestamp;
        this.uuid = UUID.fromString(uuid);
        this.ticketMessage = ticketMessage;
        this.resolved = resolved;
    }

    public String getSenderName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(this.uuid);
        return player.getName();
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

    public String getTicketMessage() {
        return this.ticketMessage;
    }

    /**
     * Generates an uppercase Base36 id, length depending on max length variable
     * @return String ID
     */
    public String generateID() {
        char[] availableChars = "ABCDEFGHJKLMNOPQRSTVXYZ0123456789".toCharArray();

        Random random = new Random();
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            id.append(availableChars[random.nextInt(availableChars.length)]);
        }

        return id.toString().trim();
    }
}
