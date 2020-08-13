package github.chriscn.api;

import github.chriscn.StaffTicket;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class SQLManager {

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final String table;
    private final int port;
    
    private Connection connection;
    private final FileConfiguration config;

    StaffTicket plugin;
    public SQLManager(StaffTicket instance) {
        this.plugin = instance;
        this.config = plugin.getConfig();

        plugin.SUCCESSFUL_CONNECTION = false;

        this.host = config.getString("database.address");
        this.port = config.getInt("database.port");
        this.database = config.getString("database.database");
        this.username = config.getString("database.username");
        this.password = config.getString("database.password");

        this.table = "staffticket";

        try {
            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);

                plugin.getLogger().info("Successfully connected to the MySQL DatabaseManager.");
                plugin.SUCCESSFUL_CONNECTION = true;
            }

            if (!tableExists(table)) {
                try {
                    // CREATE TABLE `minecraft`.`staffticket` ( `ID` VARCHAR(10) NOT NULL , `TIMESTAMP` INT NOT NULL , `UUID` VARCHAR(36) NOT NULL , `MESSAGE` TEXT NOT NULL , `RESOLVED` BOOLEAN NOT NULL ) ENGINE = InnoDB;
                    //  connection.prepareStatement("CREATE TABLE ")
                    PreparedStatement generateTable = connection.prepareStatement(
                            "CREATE TABLE " + this.database + "." + this.table + " ( `ID` VARCHAR(" + 8 + ") NOT NULL , `TIMESTAMP` BIGINT NOT NULL , `UUID` VARCHAR(36) NOT NULL , `MESSAGE` TEXT NOT NULL , `RESOLVED` BOOLEAN NOT NULL ) ENGINE = InnoDB;"
                    );

                    generateTable.executeUpdate();

                    plugin.getLogger().info("Generated StaffTicket table.");
                } catch (SQLException e) {
                    handleException(e);
                }
            } else {
                plugin.getLogger().info("StaffTicket table already existed.");
            }
        } catch (SQLException | ClassNotFoundException e) {
          //  handleException(e);
            plugin.getLogger().severe("SQL Setup Failed with message " + e.getMessage());
            plugin.getLogger().severe("Shutting down StaffTicket");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private boolean tableExists(String tableName) {
        boolean tExists = false;
        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null)) {
            while (rs.next()) {
                String tName = rs.getString("TABLE_NAME");
                if (tName != null && tName.equals(tableName)) {
                    tExists = true;
                    break;
                }
            }
        } catch (SQLException e) {
            handleException(e);
        }
        return tExists;
    }

    public void submitTicket(VirtualTicket virtualTicket) {
        try {
            PreparedStatement ticket = connection.prepareStatement("INSERT INTO " + table + " (ID,TIMESTAMP,UUID,MESSAGE,RESOLVED) VALUES (?,?,?,?,?)");

            ticket.setString(1, virtualTicket.getID());
            ticket.setLong(2, virtualTicket.getTimestamp());
            ticket.setString(3, virtualTicket.getPlayerUUID().toString());
            ticket.setString(4, virtualTicket.getTicketMessage());
            ticket.setBoolean(5, virtualTicket.getResolved());

            ticket.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public VirtualTicket getVirtualTicket(String id) {
        try {
            PreparedStatement ticket = connection.prepareStatement("SELECT * FROM " + table + " WHERE ID=?");
            ticket.setString(1, id);

            ResultSet resultSet = ticket.executeQuery();
            resultSet.next();

            return new VirtualTicket(
                id,
                resultSet.getLong("TIMESTAMP"),
                resultSet.getString("UUID"),
                resultSet.getString("MESSAGE"),
                resultSet.getBoolean("RESOLVED")
            );
        } catch (SQLException e) {
            handleException(e);
        }
        return null;
    }

    public boolean ticketExists(String id) {
        return false;
    }

    public void resolveTicket(String id, boolean resolved) {
        try {
            PreparedStatement update = connection.prepareStatement("UPDATE " + table + " SET RESOLVED=? WHERE ID=?");
            update.setBoolean(1, resolved);
            update.setString(2, id);

            update.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }

        // TODO implemented method
    }

    public ArrayList<VirtualTicket> getAllTickets() {
        ArrayList<VirtualTicket> tickets = new ArrayList<>();

        try {
            PreparedStatement ticket = connection.prepareStatement("SELECT * FROM " + table);

            ResultSet resultSet = ticket.executeQuery();

            while (resultSet.next()) {
                tickets.add(new VirtualTicket(
                        resultSet.getString("ID"),
                        resultSet.getLong("TIMESTAMP"),
                        resultSet.getString("UUID"),
                        resultSet.getString("MESSAGE"),
                        resultSet.getBoolean("RESOLVED")
                ));
            }
            resultSet.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return tickets;
    }

    public ArrayList<VirtualTicket> getAllTickets(UUID uuid) {
        ArrayList<VirtualTicket> tickets = new ArrayList<>();

        try {
            PreparedStatement ticket = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            ticket.setString(1, uuid.toString());

            ResultSet resultSet = ticket.executeQuery();

            while (resultSet.next()) {
                tickets.add(new VirtualTicket(
                        resultSet.getString("ID"),
                        resultSet.getLong("TIMESTAMP"),
                        resultSet.getString("UUID"),
                        resultSet.getString("MESSAGE"),
                        resultSet.getBoolean("RESOLVED")
                ));
            }
            resultSet.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return tickets;
    }

    public ArrayList<VirtualTicket> getAllTickets(UUID uuid, boolean isResolved) {
        ArrayList<VirtualTicket> tickets = new ArrayList<>();

        try {
            PreparedStatement ticket = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=? AND RESOLVED=?");
            ticket.setString(1, uuid.toString());
            ticket.setBoolean(2, isResolved);

            ResultSet resultSet = ticket.executeQuery();

            while (resultSet.next()) {
                tickets.add(new VirtualTicket(
                        resultSet.getString("ID"),
                        resultSet.getLong("TIMESTAMP"),
                        resultSet.getString("UUID"),
                        resultSet.getString("MESSAGE"),
                        resultSet.getBoolean("RESOLVED")
                ));
            }
            resultSet.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return tickets;
    }

    public Connection getConnection() {
        return connection;
    }

    public void handleException(SQLException e) {
        Bukkit.getLogger().info("[MYSQL] Error: " + e.getMessage());
    }
}
