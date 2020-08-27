package github.chriscn.database;

import github.chriscn.StaffTicket;
import github.chriscn.api.VirtualTicket;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;

public class MySQL implements DatabaseManager {

    private Connection connection;
    private final StaffTicket plugin;

    public MySQL(StaffTicket instance) {
        this.plugin = instance;

        try {
            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + plugin.host + ":" + plugin.port + "/" + plugin.database, plugin.username, plugin.password);

                plugin.getLogger().info("Successfully connected to the MySQL Database.");
                plugin.PLUGIN_ENABLED = true;
            }

            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + plugin.table + "(ID varchar(" + StaffTicket.ID_LENGTH + "), TIMESTAMP bigint(20), UUID varchar(36), MESSAGE text, RESOLVED tinyint(1))");

                plugin.PLUGIN_ENABLED = true;
                plugin.getLogger().info("[SQL] Created StaffTicket table if it didn't exist.");
            } catch (SQLException e) {
                handleException(e);
            }
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("[SQL] Setup Failed with message: " + e.getMessage());
            e.printStackTrace();
            plugin.PLUGIN_ENABLED = false;
        }
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
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + plugin.table + " (ID,TIMESTAMP,UUID,MESSAGE,RESOLVED) VALUES (?,?,?,?,?)");

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
            PreparedStatement update = connection.prepareStatement("UPDATE " + plugin.table + " SET RESOLVED=? WHERE ID=?");
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
            statement = connection.prepareStatement("SELECT * FROM " + plugin.table + " WHERE ID=?");
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
            PreparedStatement ticket = connection.prepareStatement("SELECT * FROM " + plugin.table + " WHERE ID=?");
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
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + plugin.table);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                tickets.add(new VirtualTicket(
                        resultSet.getString("ID").toUpperCase(),
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

    @Override
    public ArrayList<VirtualTicket> getResolvedTickets(boolean resolved) {
        ArrayList<VirtualTicket> tickets = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + plugin.table + " WHERE RESOLVED=?");
            statement.setBoolean(1, resolved);

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
        Bukkit.getLogger().info("[SQL] Error: " + e.getMessage());
    }
}
