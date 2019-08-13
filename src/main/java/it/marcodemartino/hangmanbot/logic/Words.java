package it.marcodemartino.hangmanbot.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@AllArgsConstructor
@Getter
public class Words {

    private Map<String, List<String>> wordCategory;

    public String getRandomWord(String category) {
        List<String> words = wordCategory.get(category);
        if (category.equals("random")) words = getRandomCategory();

        return words.get(new Random().nextInt(words.size()));
    }

    private List<String> getRandomCategory() {
        List<String> keysAsArray = new ArrayList<>(wordCategory.keySet());
        keysAsArray.remove("Java");
        String randomKey = keysAsArray.get(new Random().nextInt(keysAsArray.size()));
        return wordCategory.get(randomKey);
    }

}
