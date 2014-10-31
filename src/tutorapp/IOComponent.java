/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tutorapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class IOComponent {
    
    static BufferedWriter fileWriter;

    public IOComponent() {
    }
    public IOComponent(File correctAnswersFile) throws IOException {
        fileWriter = new BufferedWriter(new FileWriter(correctAnswersFile));        
    }

    public static String getColoredDisplayString(Map<Integer, Object> problemList, int position1, int position2, String color) {
        String displayString = "<html>";
        for (int i = 0; i < problemList.size(); i++) {
            
            String element = (i==position1 || i==position2) ? "<font color='" + color + "'>" : "";
            element += problemList.get(i).toString();            
            element += (i==position1 || i==position2) ? "</font>" : "";
            
            displayString += (element + ((i == problemList.size()-1) ? "" : "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
        }
        displayString += "</html>";
        return displayString;
    }
    
    public static String getDisplayString(Map<Integer, Object> problemList) {
        String displayString = "";
        for (int i = 0; i < problemList.size(); i++) {
            displayString += (problemList.get(i) + ((i == problemList.size()-1) ? "" : "         "));
        }
        return displayString;
    }

    public void writeToFile(String s) throws Exception {
        fileWriter.write(s);
        fileWriter.newLine();
    }
}
