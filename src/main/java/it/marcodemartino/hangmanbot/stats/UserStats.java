package it.marcodemartino.hangmanbot.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserStats {

    @Setter
    private String username;
    private long startedMatches;
    private long guessedLetters;
    private long wrongLetters;

    public UserStats(String username) {
        this.username = username;
    }

    public long getSummedStats() {
        return guessedLetters - (wrongLetters / 4);
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
