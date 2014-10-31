package tutorapp;

import java.util.HashMap;
import java.util.Map;
import static tutorapp.IOComponent.fileWriter;

public class SortingComponent {

    Map<Integer, Map<Integer, Object>> solutionSet;

    public SortingComponent() {
        solutionSet = new HashMap<>();
    }

    public Map<Integer, Object> getFullySortedSolution() {
        if (solutionSet != null && solutionSet.size() > 0) {
            return this.solutionSet.get(solutionSet.size() - 1);
        } else {
            return null;
        }
    }

    public Map<Integer, Map<Integer, Object>> getSolutionSet() {
        return this.solutionSet;
    }

    public void performBubbleSort(Map<Integer, Object> problemList, IOComponent ioComponent) throws Exception {

        Map<Integer, Object> sortedList = new HashMap<>(problemList);
        int flag = 0;

        for (int i = 0; i < sortedList.size() - 1; i++) {
            ioComponent.writeToFile("Pass " + (i + 1));
            for (int j = 0; j < sortedList.size() - i - 1; j++) {
                flag = 0;

                Object currentObject = sortedList.get(j);
                Object nextObject = sortedList.get(j + 1);

                ioComponent.writeToFile("Compare " + currentObject.toString() + " and " + nextObject.toString());

                if (isGreaterThan(currentObject, nextObject)) {
                    ioComponent.writeToFile("Swap");
                    Object tempObject = sortedList.get(j);
                    sortedList.put(j, sortedList.get(j + 1));
                    sortedList.put(j + 1, tempObject);
                    flag = 1;
                } else {
                    ioComponent.writeToFile("Skip");
                }
            }

            /* Store the snapshot of this pass in the solution set */
            solutionSet.put(i, new HashMap<>(sortedList));

            if (flag == 0) {
                break;
            }
        }        
        
        if (fileWriter != null) {
            fileWriter.flush();
            fileWriter.close();
        }
    }

    public static boolean isGreaterThan(Object object1, Object object2) {
        boolean isGreater = false;
        if (object1 instanceof Integer && object2 instanceof Integer) {
            int value1 = ((Integer) object1).intValue();
            int value2 = ((Integer) object2).intValue();
            isGreater = ((value1 > value2) ? true : false);
        } else if (object1 instanceof Float && object2 instanceof Float) {
            float value1 = ((Float) object1).floatValue();
            float value2 = ((Float) object2).floatValue();
            isGreater = ((value1 > value2) ? true : false);
        } else if (object1 instanceof Character && object2 instanceof Character) {
            char value1 = ((Character) object1).charValue();
            char value2 = ((Character) object2).charValue();
            isGreater = ((value1 > value2) ? true : false);
        } else if (object1 instanceof String && object2 instanceof String) {
            String value1 = ((String) object1).toString();
            String value2 = ((String) object2).toString();
            isGreater = ((value1.compareTo(value2) > 0) ? true : false);
        }
        return isGreater;
    }
}