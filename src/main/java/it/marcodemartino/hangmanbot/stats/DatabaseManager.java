package it.marcodemartino.hangmanbot.stats;

import lombok.Getter;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {

    private final String hostname;
    private final String username;
    private final String password;
    private final String database;
    private final String tableName;
    private final int port;

    @Getter
    private Connection connection;

    public DatabaseManager(String hostname, String username, String password, String database, String tableName, int port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.tableName = tableName;
        this.port = port;
    }

    public void updateUserStatistics(long userId, UserStats userStats) throws SQLException {
        System.out.println("Hi");
        PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + tableName + " (`user_id`, `started_matches`, `guessed_letters`, `wrong_letters`) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `started_matches` = ?, `guessed_letters` = ?, `wrong_letters` = ?");
        statement.setLong(1, userId);

        statement.setLong(2, userStats.getStartedMatches());
        statement.setLong(2 + 3, userStats.getStartedMatches());

        statement.setLong(3, userStats.getGuessedLetters());
        statement.setLong(3 + 3, userStats.getGuessedLetters());

        statement.setLong(4, userStats.getGuessedLetters());
        statement.setLong(4 + 3, userStats.getGuessedLetters());

        statement.executeUpdate();

        statement.close();
        closeConnection();
    }

    public Map<Long, UserStats> getAllUserStatistics() throws SQLException {
        Map<Long, UserStats> userStatistics = new HashMap<>();

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + tableName);
        ResultSet result = statement.executeQuery();

        while (result.next()) {
            long userId = result.getLong("user_id");
            long startedMatches = result.getLong("started_matches");
            long guessedLetters = result.getLong("guessed_letters");
            long wrongLetters = result.getLong("wrong_letters");

            UserStats userStats = new UserStats(startedMatches, guessedLetters, wrongLetters);
            userStatistics.put(userId, userStats);
        }

        result.close();
        statement.close();
        closeConnection();
        return userStatistics;
    }


    private Connection getConnection() {
        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false&user=" + username;
        if (password != null && !password.isEmpty())
            url += "&password=" + password;

        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return connection;
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
