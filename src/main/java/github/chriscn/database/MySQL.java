package github.chriscn.database;

import github.chriscn.StaffTicket;
import github.chriscn.api.VirtualTicket;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;

public class MySQL implements DatabaseManager {

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;
    private final String table;

    private Connection connection;
    private final FileConfiguration config;
    private final StaffTicket plugin;

    public MySQL(StaffTicket instance) {
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

                plugin.getLogger().info("Successfully connected to the MySQL Database.");
                plugin.SUCCESSFUL_CONNECTION = true;
                plugin.PLUGIN_ENABLED = true;
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
            plugin.getLogger().severe("[SQL] Setup Failed with message " + e.getMessage());
            e.printStackTrace();
            plugin.PLUGIN_ENABLED = false;
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

    @Override
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    @Override
    public void createTicket(VirtualTicket ticket) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + table + " (ID,TIMESTAMP,UUID,MESSAGE,RESOLVED) VALUES (?,?,?,?,?)");

            statement.setString(1, ticket.getID());
            statement.setLong(2, ticket.getTimestamp());
            statement.setString(3, ticket.getPlayerUUID().toString());
            statement.setString(4, ticket.getTicketMessage());
            statement.setBoolean(5, ticket.getResolved());

            statement.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    @Override
    public void resolveTicket(String id, boolean resolved) {
        try {
            PreparedStatement update = connection.prepareStatement("UPDATE " + table + " SET RESOLVED=? WHERE ID=?");
            update.setBoolean(1, resolved);
            update.setString(2, id);

            update.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    @Override
    public boolean ticketExists(String id) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE ID=?");
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public VirtualTicket getTicket(String id) {
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

    @Override
    public ArrayList<VirtualTicket> getAllTickets() {
        ArrayList<VirtualTicket> tickets = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                tickets.add(new VirtualTicket(
                        resultSet.getString("ID"),
                        resultSet.getLong("TIMESTAMP"),
                        resultSet.getString("UUID"),
                        resultSet.getString("MESSAGE"),
                        resultSet.getBoolean("RESOLVED")
                ));
            }

        }  catch (SQLException e) {
            handleException(e);
        }

        return tickets;
    }

    private void handleException(SQLException e) {
        Bukkit.getLogger().info("[MYSQL] Error: " + e.getMessage());
    }
}
