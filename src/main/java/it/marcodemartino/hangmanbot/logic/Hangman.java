package it.marcodemartino.hangmanbot.logic;

import java.util.List;

public class Hangman {

    private long chatId;
    private String word;
    private List<Character> guessedLetters;
    private List<Character> wrongLetters;
    private List<String> saidWords;
    private int errors;
    private int maxErrors;

    public Hangman(long chatId, String word) {
        this.chatId = chatId;
        this.word = word;
    }

    public GuessResult guessLetter(char c) {
        if(guessedLetters.contains(c) || wrongLetters.contains(c))
            return GuessResult.LETTER_ALREADY_SAID;

        if (word.indexOf(c) != -1) {
            guessedLetters.add(c);
            return GuessResult.LETTER_GUESSED;
        } else {
            wrongLetters.add(c);
            errors++;
            return GuessResult.LETTER_WRONG;
        }
    }

    public GuessResult guessWord(String word) {
        if(saidWords.contains(word))
            return GuessResult.WORD_ALREADY_SAID;

        if(this.word.equalsIgnoreCase(word)) {
            return GuessResult.WORD_GUESSED;
        } else {
            saidWords.add(word.toLowerCase());
            errors++;
            return GuessResult.WORD_WRONG;
        }
    }

    public GuessResult getStatus() {
        if(areLetterRight() || saidWords.contains(word)) return GuessResult.MATCH_WIN;
        if(errors >= maxErrors) return GuessResult.MATCH_LOSE;
        return null;
    }

    private boolean areLetterRight() {
        for (char c : word.toCharArray())
            if(!guessedLetters.contains(c))
                return false;

        return true;
    }

}
