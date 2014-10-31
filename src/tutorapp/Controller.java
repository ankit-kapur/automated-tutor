package tutorapp;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Controller {

    static Map<Integer, Object> problemSet;
    static Map<Integer, Map<Integer, Object>> solutionSet;
    int currentLevelNumber = 0;

    public final String configurationFilePath = "tutor_files/configuration.properties";
    DisplayComponent tutorFrame;
    
    public void refreshTutor() {
        this.tutorFrame = new DisplayComponent(this);
        ProblemSelector problemSelector = new ProblemSelector(problemSetLength.get(currentLevelNumber), problemSetDataType.get(currentLevelNumber), upperThreshold, minWordLength, maxWordLength, wordListFilePath);
        SortingComponent sortingComponent = new SortingComponent();
        IOComponent ioComponent;

        try {
            /* Generate the list */
            problemSet = problemSelector.generateRandomizedList();

            correctAnswersFile = new File(sortSolutionFilePath);
            System.out.println(correctAnswersFile.getAbsolutePath());
            ioComponent = new IOComponent(correctAnswersFile);

            String problemBeforeSorting = IOComponent.getDisplayString(problemSet);
            System.out.println("List BEFORE sorting : " + problemBeforeSorting);
            sortingComponent.performBubbleSort(problemSet, ioComponent);
            System.out.println("List AFTER sorting : " + IOComponent.getDisplayString(sortingComponent.getFullySortedSolution()));

            tutorFrame.initializeTutor(correctAnswersFile, problemBeforeSorting, problemSet);
            tutorFrame.setVisible(true);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void loadConfiguration() {
        Properties prop = new Properties();
        FileInputStream in = null;
        problemSetLength = new ArrayList<>();
        problemSetDataType = new ArrayList<>();
        problemSetSortingLevel = new ArrayList<>();
        
        try {
            in = new FileInputStream(configurationFilePath);
            prop.load(in);

            /* Read the configurations */
            sortSolutionFilePath = prop.getProperty("sort.solution.file.path");
            wordListFilePath = prop.getProperty("wordlist.file");

            String numberOfLevelsStr = prop.getProperty("number.of.difficulty.levels");

            String upperThres = prop.getProperty("number.size.upper.threshold");
            String minLength = prop.getProperty("word.minimum.length");
            String maxLength = prop.getProperty("word.maximum.length");

            /* Validations of the configurations will be performed by this component */
            ValidationComponent validationComponent = new ValidationComponent();
            if (validationComponent.checkForEmptyConfigs(Arrays.asList("sort.solution.file.path", "wordlist.file"), Arrays.asList(sortSolutionFilePath, wordListFilePath))) {
                if (!validationComponent.validateIntegerConfigs(Arrays.asList("number.size.upper.threshold", "word.minimum.length", "word.maximum.length"), Arrays.asList(upperThres, minLength, maxLength))) {
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }

            /* Once all validations are done, we assign the values */
            numberOfLevels = Integer.parseInt(numberOfLevelsStr);
            upperThreshold = Integer.parseInt(upperThres);
            minWordLength = Integer.parseInt(minLength);
            maxWordLength = Integer.parseInt(maxLength);

            /* Get configs of each LEVEL */
            for (int levelNumber = 0; levelNumber < numberOfLevels; levelNumber++) {
                String setType = prop.getProperty("level." + (levelNumber+1) + ".problem.set.data.type");
                String setLength = prop.getProperty("level." + (levelNumber+1) + ".problem.set.length");
                String setSortingLevel = prop.getProperty("level." + (levelNumber+1) + ".problem.set.sorting.level");
                problemSetLength.add(Integer.parseInt(setLength));
                problemSetDataType.add(Integer.parseInt(setType));
                problemSetSortingLevel.add(Integer.parseInt(setSortingLevel));
            }
        } catch (FileNotFoundException e) {
            System.err.print(e);
        } catch (IOException e) {
            System.err.print(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.print(e);
                }
            }
        }
    }
    
    public void increaseLevel() {
        currentLevelNumber++;
    }
    
    public void decreaseLevel() {
        currentLevelNumber--;
    }
    
    public int getNumberOfLevels() {
        return this.numberOfLevels;
    }
    
    public int getCurrentLevelNumber() {
        return this.currentLevelNumber;
    }

    /* Parameters that are read from the configuration */
    List<Integer> problemSetLength;
    List<Integer> problemSetDataType;
    List<Integer> problemSetSortingLevel;

    int numberOfLevels;
    int upperThreshold;
    int minWordLength;
    int maxWordLength;
    String wordListFilePath;
    String sortSolutionFilePath;
    File correctAnswersFile;
}
