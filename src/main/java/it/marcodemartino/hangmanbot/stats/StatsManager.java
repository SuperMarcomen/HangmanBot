package it.marcodemartino.hangmanbot.stats;

import io.github.ageofwar.telejam.users.User;
import it.marcodemartino.hangmanbot.logic.GuessResult;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsManager {

    private static DatabaseManager databaseManager;
    private static Map<Long, UserStats> userStatistics;

    public StatsManager(DatabaseManager databaseManager) throws SQLException {
        StatsManager.databaseManager = databaseManager;
        userStatistics = databaseManager.getAllUserStatistics();
    }

    public static void reloadUsers() throws SQLException {
        userStatistics = databaseManager.getAllUserStatistics();
    }

    public List<UserStats> getBestUsers() {
        return userStatistics.values().stream()
                .sorted(Comparator.comparingLong(UserStats::getSummedStats)
                        .reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public UserStats increaseStats(User user, GuessResult guessResult) throws SQLException {
        UserStats userStats = userStatistics.get(user.getId());
        if (userStats == null) {
            userStats = new UserStats();
            userStatistics.put(user.getId(), userStats);
        }

        if (user.getUsername().isPresent()) userStats.setUsername(user.getUsername().get());
        else userStats.setUsername(user.getFirstName());

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

        databaseManager.updateUserStatistics(user.getId(), userStats);
        return userStats;
    }

}
