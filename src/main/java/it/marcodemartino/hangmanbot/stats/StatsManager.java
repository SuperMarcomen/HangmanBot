package it.marcodemartino.hangmanbot.stats;

import it.marcodemartino.hangmanbot.logic.GuessResult;
import it.marcodemartino.hangmanbot.stats.chart.StatsBarChart;
import javafx.application.Application;

import java.sql.SQLException;
import java.util.Map;

public class StatsManager {

    private static DatabaseManager databaseManager;
    private static Map<Long, UserStats> userStatistics;

    public StatsManager(DatabaseManager databaseManager) throws SQLException {
        StatsManager.databaseManager = databaseManager;
        userStatistics = databaseManager.getAllUserStatistics();
    }

    public static Map<String, Integer> getGuessedLettersStats() {
        long first = 0, second = 0, third = 0;

        for (UserStats value : userStatistics.values()) {
            if (value.getGuessedLetters() > first) {
                first = value.getGuessedLetters();
            } else if (value.getGuessedLetters() > second) {
                second = value.getGuessedLetters();
            } else if (value.getGuessedLetters() > third) {
                third = value.getGuessedLetters();
            }
        }
        return null;
    }

    public UserStats increaseStats(long userId, GuessResult guessResult) throws SQLException {
        UserStats userStats = userStatistics.get(userId);
        if (userStats == null) {
            userStats = new UserStats();
            userStatistics.put(userId, userStats);
        }

        switch (guessResult) {
            case MATCH_STARTED:
                userStats.increaseStartedMatches();
                break;
            case LETTER_GUESSED:
                userStats.increaseGuessedLetters();
                break;
            case LETTER_WRONG:
                userStats.increaseWrongLetters();
                break;
        }

        databaseManager.updateUserStatistics(userId, userStats);
        return userStats;
    }

    public void generateChart() {
        new Thread(() -> Application.launch(StatsBarChart.class)).start();
    }
}
