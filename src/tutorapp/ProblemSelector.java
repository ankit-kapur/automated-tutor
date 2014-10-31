package tutorapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProblemSelector {

    /* Levels that define the problem */
    int lengthOfList;
    int typeOfList;
    int difficultyLevel;

    int upperThreshold;
    int minWordLength;
    int maxWordLength;
    String wordListFilePath;

    /* Constructor */
    ProblemSelector(int length, int type, int upperThreshold, int minWordLength, int maxWordLength, String wordListFilePath) {
        this.lengthOfList = length;
        this.typeOfList = type;
        this.upperThreshold = upperThreshold;
        this.minWordLength = minWordLength;
        this.maxWordLength = maxWordLength;
        this.wordListFilePath = wordListFilePath;
    }

    /* Generate randomized list */
    public Map<Integer, Object> generateRandomizedList() {
        Map<Integer, Object> randomizedList = new HashMap<>();

        for (int i = 0; i < lengthOfList; i++) {
            switch (typeOfList) {

                case ListType.INTEGER_TYPE:
                    int randomNumber;
                    do {
                        randomNumber = (int) (upperThreshold * Math.random());
                    } while (randomizedList.entrySet().contains(new Integer(randomNumber)));
                    randomizedList.put(i, new Integer(randomNumber));
                    break;

                case ListType.FLOAT_TYPE:
                    float randomFloat;
                    do {
                        randomFloat = (float) (upperThreshold * Math.random());
                        randomFloat = (float) (Math.round(randomFloat * 100.0) / 100.0);
                    } while (randomizedList.entrySet().contains(new Float(randomFloat)));

                    randomizedList.put(i, new Float(randomFloat));
                    break;

                case ListType.CHARACTER_TYPE:
                    char randomCharacter;
                    do {
                        int randomCharNumber = (int) (26 * Math.random());
                        randomCharacter = (char) (65 + randomCharNumber);
                    } while (randomizedList.entrySet().contains(new Character(randomCharacter)));

                    randomizedList.put(i, new Character(randomCharacter));
                    break;

                case ListType.WORD_TYPE:
                    String word;
                    do {
                        word = generateRandomWord().toUpperCase();
                    } while (randomizedList.entrySet().contains(word));

                    randomizedList.put(i, word);
                    break;
            }
        }

        return randomizedList;
    }

    public String generateRandomWord() {
        String generatedWord = null, currentWord;
        List<String> wordList = new ArrayList<>();
        BufferedReader fileReader;
        File wordListFile = new File(wordListFilePath);
        try {

            /* Read from the word-list txt file, and add each word to the List */
            fileReader = new BufferedReader(new FileReader(wordListFile));
            while ((currentWord = fileReader.readLine()) != null) {
                wordList.add(currentWord);
            }

            /* Pick a random word from the list until the word length constraints are met */
            do {
                int randomPosition = (int) (Math.random() * wordList.size());
                generatedWord = wordList.get(randomPosition);
            } while (generatedWord.length() < minWordLength || generatedWord.length() > maxWordLength);

        } catch (FileNotFoundException e) {
            System.err.println("Word-list file not found: " + e);
        } catch (IOException e) {
            System.err.println("Error reading from the word-list file: " + e);
        }
        return generatedWord;
    }

    /* Getters and setters */
    public void setLengthOfList(int length) {
        this.lengthOfList = length;
    }

    public int getLengthOfList() {
        return this.lengthOfList;
    }

    public void setTypeOfList(int type) {
        this.typeOfList = type;
    }

    public int getTypeOfList() {
        return this.typeOfList;
    }

    public void setDifficultyLevel(int level) {
        this.typeOfList = level;
    }

    public int getDifficultyLevel() {
        return this.difficultyLevel;
    }

    public class ListType {

        /* Data type of the list */
        public static final int INTEGER_TYPE = 1;
        public static final int FLOAT_TYPE = 2;
        public static final int CHARACTER_TYPE = 3;
        public static final int WORD_TYPE = 4;

        /* Difficulty levels */
        public static final int FULLY_SORTED = 1;
        public static final int PARTIALLY_SORTED = 2;
        public static final int UNSORTED = 3;
    }
}
