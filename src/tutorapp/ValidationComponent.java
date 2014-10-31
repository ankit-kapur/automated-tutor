/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorapp;

import java.util.List;

public class ValidationComponent {

    public boolean checkForEmptyConfigs(List<String> configNames, List<String> configList) {
        boolean allValid = true;

        if (configList != null) {
            for (int i = 0; i < configList.size(); i++) {
                String config = configList.get(i);
                if (config == null || config.isEmpty()) {
                    String configName = configNames.get(i);
                    System.err.println("Configuration is invalid for " + configName + ". Please check the configuration.properties file.");
                    allValid = false;
                    break;
                }
            }
        }

        return allValid;
    }

    public boolean validateIntegerConfigs(List<String> configNames, List<String> configList) {
        boolean allValid = true;

        if (configList != null) {
            for (int i = 0; i < configList.size(); i++) {
                String config = configList.get(i);
                if (config == null || config.isEmpty()) {
                    String configName = configNames.get(i);
                    System.err.println("Configuration is invalid for " + configName + ". Please check the configuration.properties file.");
                    allValid = false;
                    break;
                } else {
                    try {
                        Integer.parseInt(config);
                    } catch (NumberFormatException e) {
                        String configName = configNames.get(i);
                        System.err.println("Configuration is invalid for " + configName + ". Please check the configuration.properties file.");
                        allValid = false;
                        break;
                    }
                }
            }
        }

        return allValid;
    }
}