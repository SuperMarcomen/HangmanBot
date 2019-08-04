package it.marcodemartino.hangmanbot.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStats {

    private long startedMatches;
    private long guessedLetters;
    private long wrongLetters;

    public UserStats() {
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
