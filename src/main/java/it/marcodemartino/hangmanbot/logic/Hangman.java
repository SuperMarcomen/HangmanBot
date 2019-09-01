package it.marcodemartino.hangmanbot.logic;

import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton;
import io.github.ageofwar.telejam.inline.InlineKeyboardButton;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import lombok.Getter;
import lombok.Setter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class Hangman {

    @Getter
    private String word;
    private List<Integer> wordArray;
    @Getter
    private long senderId;
    @Getter
    @Setter
    private boolean multiplayer;
    @Getter
    private String category;
    private List<Integer> guessedLetters;
    private List<Integer> wrongLetters;
    @Getter
    private int errors;
    @Getter
    private int maxErrors;

    public Hangman(String word, long senderId, String category, int maxErrors) {
        this.word = word;
        this.senderId = senderId;
        this.category = category;
        this.maxErrors = maxErrors;
        wordArray = new ArrayList<>();
        guessedLetters = new ArrayList<>();
        wrongLetters = new ArrayList<>();

        final int length = word.length();
        for (int offset = 0; offset < length; ) {
            final int codepoint = word.codePointAt(offset);
            if (Character.toChars(codepoint)[0] == ' ') continue;
            wordArray.add(codepoint);
            offset += Character.charCount(codepoint);
        }
    }

    public String getCurrentState() {
        StringBuilder currentWord = new StringBuilder();
        GuessResult guessResult = getStatus();
        if (guessResult != null && guessResult.equals(GuessResult.MATCH_WIN)) return word;

        for (int codepoint : wordArray) {
            if (guessedLetters.contains(getCodepointWithoutAccent(Character.toLowerCase(codepoint))))
                currentWord.append(Character.toChars(codepoint)).append(" ");
            else if (Character.toChars(codepoint)[0] == ' ')
                currentWord.append("  ");
            else if (Character.toChars(codepoint)[0] == '\'')
                currentWord.append("' ");
            else if (Character.toChars(codepoint)[0] == '-')
                currentWord.append("- ");
            else
                currentWord.append("_ ");
        }

        return currentWord.toString();
    }

    public GuessResult guessLetter(String letter) {
        if (letter.equals("-")) return GuessResult.LETTER_ALREADY_SAID;

        final int length = letter.length();
        List<Integer> codepoints = new ArrayList<>();

        for (int offset = 0; offset < length; ) {
            final int codepoint = letter.toLowerCase().codePointAt(offset);

            if (wordArray.stream().anyMatch(c -> getCodepointWithoutAccent(Character.toLowerCase(c)) == codepoint)) {
                codepoints.add(codepoint);
            } else {
                wrongLetters.add(codepoint);
                if (errors < maxErrors) errors++;
                return GuessResult.LETTER_WRONG;
            }

            offset += Character.charCount(codepoint);
        }

        guessedLetters.addAll(codepoints);
        return GuessResult.LETTER_GUESSED;
    }

    public GuessResult getStatus() {
        if (areLetterRight()) return GuessResult.MATCH_WIN;
        if (errors >= maxErrors) return GuessResult.MATCH_LOSE;
        return null;
    }

    private int getCodepointWithoutAccent(int codepoint) {
        String letter = String.valueOf(Character.toChars(codepoint));

        return Normalizer.normalize(letter, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").codePointAt(0);
    }

    private boolean areLetterRight() {
        for (Integer codepoint : wordArray) {
            if (Character.toChars(codepoint)[0] == '\'' || Character.toChars(codepoint)[0] == '-') continue;

            if (!guessedLetters.contains(getCodepointWithoutAccent(Character.toLowerCase(codepoint)))) return false;
        }
        return true;
    }

    public InlineKeyboardMarkup generateKeyboard(List<String> alphabet) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (String letter : alphabet) {

            final int length = letter.length();
            for (int offset = 0; offset < length; ) {
                final int codepoint = letter.codePointAt(offset);

                CallbackDataInlineKeyboardButton button;

                if (guessedLetters.contains(Character.toLowerCase(codepoint)) || wrongLetters.contains(Character.toLowerCase(codepoint))) //letter was already said
                    button = new CallbackDataInlineKeyboardButton("-", "letter_-");
                else
                    button = new CallbackDataInlineKeyboardButton(letter, "letter_" + letter);

                buttons.add(button);
                offset += Character.charCount(codepoint);
            }
        }

        return InlineKeyboardMarkup.fromColumns(6, buttons);
    }

}
