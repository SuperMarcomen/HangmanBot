package it.marcodemartino.hangmanbot.stats;

import io.github.ageofwar.telejam.users.User;
import it.marcodemartino.hangmanbot.logic.GuessResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatsManager {

    private DatabaseManager databaseManager;

    public StatsManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public UserStats getUserStats(long userId) {
        return databaseManager.getUserStatistics(userId);
    }

    public List<UserStats> getBestUsers() {
        return databaseManager.getAllUserStatistics().values().stream()
                .filter(userStats -> userStats.getStartedMatches() >= 50)
                .sorted(Comparator.comparingDouble(UserStats::getRatio)
                        .reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public UserStats increaseStats(User user, GuessResult guessResult) {
        UserStats userStats = databaseManager.getUserStatistics(user.getId());
        if (userStats == null)
            userStats = new UserStats(user.getName(), user.getId());


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

        databaseManager.updateUserStatistics(userStats);
        return userStats;
    }

}
