package github.chriscn.api;

import github.chriscn.StaffTicket;

import java.sql.*;

public class SQLManager {

    private String host, database, username, password, table;
    private int port;

    private Connection connection;

    StaffTicket plugin;
    public SQLManager(StaffTicket instance) {
        this.plugin = instance;

        this.host = plugin.getConfig().getString("database.address");
        this.port = plugin.getConfig().getInt("database.port");
        this.database = plugin.getConfig().getString("database.database");
        this.username = plugin.getConfig().getString("database.username");
        this.password = plugin.getConfig().getString("database.password");

        this.table = "staffticket";

        try {
            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);

                plugin.getLogger().info("Successfully connected to the MySQL Database.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            generateTable();
        }
    }

    private void generateTable() {
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
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().info("StaffTicket table already existed.");
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnection() {
        return connection;
    }
}
