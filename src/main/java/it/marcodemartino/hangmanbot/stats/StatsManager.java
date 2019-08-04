package it.marcodemartino.hangmanbot.stats;

import it.marcodemartino.hangmanbot.logic.GuessResult;

import java.sql.SQLException;
import java.util.Map;

public class StatsManager {

    private final DatabaseManager databaseManager;
    private final Map<Long, UserStats> userStatistics;

    public StatsManager(DatabaseManager databaseManager) throws SQLException {
        this.databaseManager = databaseManager;
        userStatistics = databaseManager.getAllUserStatistics();
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
}
