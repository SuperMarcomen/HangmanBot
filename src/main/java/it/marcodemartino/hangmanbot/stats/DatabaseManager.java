package it.marcodemartino.hangmanbot.stats;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.ageofwar.telejam.users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DatabaseManager {

    private final HikariDataSource hikari;

    public DatabaseManager() {
        HikariConfig config = new HikariConfig("hikari.properties");
        hikari = new HikariDataSource(config);
        hikari.setMaximumPoolSize(15);
        hikari.setIdleTimeout(30L);
    }

    public void updateUserStatistics(UserStats userStats) {
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO bot (`user_id`, `username`, `started_matches`, `guessed_letters`, `wrong_letters`) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE `username` = ?, `started_matches` = ?, `guessed_letters` = ?, `wrong_letters` = ?")) {

            statement.setLong(1, userStats.getUserId());

            statement.setString(2, userStats.getUsername());
            statement.setString(2 + 4, userStats.getUsername());

            statement.setLong(3, userStats.getStartedMatches());
            statement.setLong(3 + 4, userStats.getStartedMatches());

            statement.setLong(4, userStats.getGuessedLetters());
            statement.setLong(4 + 4, userStats.getGuessedLetters());

            statement.setLong(5, userStats.getWrongLetters());
            statement.setLong(5 + 4, userStats.getWrongLetters());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Locale getUserLanguage(User user) {
        Locale locale = user.getLocale();

        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM user_language WHERE `user_id` = ?")) {

            statement.setLong(1, user.getId());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String languageCode = result.getString("language_code");
                locale = Locale.forLanguageTag(languageCode);
            }

            statement.close();
            connection.close();
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locale;
    }

    public void updateUserLanguage(long userId, String language) {
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO user_language (`user_id`, `language_code`) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE `language_code` = ?")) {

            statement.setLong(1, userId);
            statement.setString(2, language);
            statement.setString(3, language);

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserStats getUserStatistics(long userId) {
        UserStats userStats = null;

        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bot WHERE `user_id` = ?")) {

            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String username = result.getString("username");
                long startedMatches = result.getLong("started_matches");
                long guessedLetters = result.getLong("guessed_letters");
                long wrongLetters = result.getLong("wrong_letters");

                userStats = new UserStats(username, userId, startedMatches, guessedLetters, wrongLetters);
            }

            statement.close();
            connection.close();
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userStats;
    }

    public Map<Long, UserStats> getAllUserStatistics() {
        Map<Long, UserStats> userStatistics = new HashMap<>();

        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bot")) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                long userId = result.getLong("user_id");
                String username = result.getString("username");
                long startedMatches = result.getLong("started_matches");
                long guessedLetters = result.getLong("guessed_letters");
                long wrongLetters = result.getLong("wrong_letters");

                UserStats userStats = new UserStats(username, userId, startedMatches, guessedLetters, wrongLetters);
                userStatistics.put(userId, userStats);
            }

            statement.close();
            connection.close();
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userStatistics;
    }


}
