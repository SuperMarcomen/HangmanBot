package it.marcodemartino.hangmanbot.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserStats {

    @Setter
    private String username;
    @Getter
    private long userId;
    private long startedMatches;
    private long guessedLetters;
    private long wrongLetters;

    public UserStats(String username, long userId) {
        this.username = username;
        this.userId = userId;
    }

    public double getRatio() {
        try {
            return (float) guessedLetters / ((float) guessedLetters + (float) wrongLetters);
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    public void increaseStartedMatches() {
        startedMatches++;
    }

    public void increaseGuessedLetters() {
        guessedLetters++;
    }

    public void increaseWrongLetters() {
        wrongLetters++;
    }

}
