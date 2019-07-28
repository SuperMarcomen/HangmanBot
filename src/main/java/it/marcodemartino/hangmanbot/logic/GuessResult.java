package it.marcodemartino.hangmanbot.logic;

public enum GuessResult {

    LETTER_GUESSED,
    LETTER_ALREADY_SAID,
    LETTER_WRONG,
    WORD_GUESSED,
    WORD_ALREADY_SAID,
    WORD_WRONG,
    MATCH_WIN,
    MATCH_LOSE

}
