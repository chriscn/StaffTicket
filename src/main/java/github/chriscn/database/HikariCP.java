package github.chriscn.database;

import com.zaxxer.hikari.HikariDataSource;
import github.chriscn.StaffTicket;
import github.chriscn.api.VirtualTicket;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;

public class HikariCP implements DatabaseManager {

    private HikariDataSource hikari;
    private Connection connection;
    private FileConfiguration config;

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;
    private final String table;

    StaffTicket plugin;
    public HikariCP(StaffTicket instance) {
        this.plugin = instance;
        this.config = plugin.getConfig();

        plugin.SUCCESSFUL_CONNECTION = false;

        this.host = config.getString("database.address");
        this.port = config.getInt("database.port");
        this.database = config.getString("database.database");
        this.username = config.getString("database.username");
        this.password = config.getString("database.password");
        this.table = "staffticket";

        this.hikari = new HikariDataSource();

        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", database);
        hikari.addDataSourceProperty("user", username);
        hikari.addDataSourceProperty("password", password);

        try {
            this.connection = hikari.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + "(ID varchar(" + 8 + "), TIMESTAMP bigint(20), UUID varchar(36), MESSAGE text, RESOLVED tinyint(1))");

            plugin.SUCCESSFUL_CONNECTION = true;
            plugin.PLUGIN_ENABLED = true;
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.PLUGIN_ENABLED = false;
        }
    }

    @Override
    public void closeConnection() {
        if (hikari != null) {
            hikari.close();
        }
    }

    @Override
    public void createTicket(VirtualTicket ticket) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO " + table + " (ID,TIMESTAMP,UUID,MESSAGE,RESOLVED) VALUES (?,?,?,?,?)");

                    statement.setString(1, ticket.getID());
                    statement.setLong(2, ticket.getTimestamp());
                    statement.setString(3, ticket.getPlayerUUID().toString());
                    statement.setString(4, ticket.getTicketMessage());
                    statement.setBoolean(5, ticket.getResolved());

                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void resolveTicket(String id, boolean resolved) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement update = connection.prepareStatement("UPDATE " + table + " SET RESOLVED=? WHERE ID=?");
                    update.setBoolean(1, resolved);
                    update.setString(2, id);

                    update.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean ticketExists(String id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE ID=?");
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
        // TODO check local cache first!
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE ID=?");
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return new VirtualTicket(
                    id,
                    resultSet.getLong("TIMESTAMP"),
                    resultSet.getString("UUID"),
                    resultSet.getString("MESSAGE"),
                    resultSet.getBoolean("RESOLVED")
            );
        } catch (SQLException e) {
            e.printStackTrace();
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

    @Override
    public ArrayList<VirtualTicket> getResolvedTickets(boolean resolved) {
        ArrayList<VirtualTicket> tickets = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE RESOLVED=?");
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

    public void handleException(SQLException e) {
        Bukkit.getLogger().severe("[HIKARICP] Error: " + e.getMessage());
    }
}
