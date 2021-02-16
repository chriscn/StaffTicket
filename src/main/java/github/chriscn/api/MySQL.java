package github.chriscn.api;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class MySQL {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    private static Connection connection;
    public Statement statement;

    public MySQL(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);

        this.statement = connection.createStatement();
    }

    public ArrayList<UUID> testingDB() throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM player_balance;");
        ArrayList<UUID> players = new ArrayList<>();
        while(resultSet.next()) {
            UUID player = UUID.fromString(resultSet.getString("uuid"));
            players.add(player);
        }

        return players;
    }
}
