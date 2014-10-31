package tutorapp;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class DisplayComponent extends javax.swing.JFrame {

    File correctAnswersFile;
    Map<Integer, Object> problemSet;
    UserListArrangement userArrangement;
    Controller controller;

    public DisplayComponent(Controller controller) {
        this.controller = controller;
    }

    public void initializeTutor(File file, String displayString, Map<Integer, Object> problemSet) {
        initComponents();
        this.correctAnswersFile = file;
        this.problemSet = problemSet;

        problemDisplayLabel.setText(displayString);

        /* Initially hide the pass feedback labels */
        passDisplayLabel.setText("");
        passNumberLabel.setText("");
        modifiedSetPanel.setVisible(false);
        
        if (controller.getCurrentLevelNumber() == 0) {
            previousLevelButton.setEnabled(false);
        } else {            
            previousLevelButton.setEnabled(true);
        }
        if (controller.getCurrentLevelNumber() == controller.getNumberOfLevels()-1) {            
            nextLevelButton.setEnabled(false);
        } else {           
            nextLevelButton.setEnabled(true);            
        }

        /* Set the title */
        this.setTitle("Sorting tutor: Level " + (controller.getCurrentLevelNumber()+1));
    }

    public void changePassDisplay() {
        /* This object stores the arrangement of the set,
         * as entered by the user */
        userArrangement = new UserListArrangement(problemSet);
        Map<Integer, Object> userSet = userArrangement.getUserSet();

        /* Get the student's text, and break it down into words, separated by \n */
        String studentText = studentInputBox.getText();
        StringTokenizer studentTextTokens = new StringTokenizer(studentText, "\n");
        boolean firstPass = true, lastWasSwap = false;
        int swapPosition1 = -1, swapPosition2 = -1;
        int currentPassNumber = 1;
        String modifiedSetString;
        
        while (studentTextTokens.hasMoreTokens()) {
            String originalTextLine = studentTextTokens.nextToken();
            String textLine = originalTextLine.toUpperCase();
            if (textLine.contains("PASS")) {
                /* Validate whether the "Pass" action has been written properly */
                if (textLine.length() < "PASS ".length()) {
                    /* He's mentioned "Pass" but no pass # */
                    feedbackBox.setText("No pass number has been mentioned. Please specify the right pass number.");
                    break;
                } else if (!textLine.substring("PASS ".length(), textLine.length()).equals(Integer.toString(currentPassNumber++))) {
                    /* Wrong pass # has been given */
                    feedbackBox.setText("Wrong pass number. Please specify the right pass #");
                    break;
                } else {
                    /* Right pass number */
                    feedbackBox.setText("The pass number is correct. Please begin comparisons.");
                }
                lastWasSwap = false;
            } else if (firstPass) {
                /* If it's the FIRST pass and no mention of the word "Pass" was found. */
                feedbackBox.setText("Please specify the right pass number. Begin with 'Pass 1'.");
                break;
            } else if (textLine.contains("COMPARE")) {

                /* If a comparison was already under way, throw an exception */
                if (userArrangement.getPointerOne() != -1 || userArrangement.getPointerTwo() != -1) {
                    feedbackBox.setText("A comparison was already being done before this step: '" + originalTextLine + "'. Please complete it with a 'swap' or a 'skip'");
                    break;
                } else {
                    /* Validate whether the "Compare" action has been written properly */
                    if (textLine.length() < "COMPARE ".length()) {
                        /* He's mentioned "Compare" but no pass # */
                        feedbackBox.setText("Please specify WHICH two elements are to be compared.");
                        break;
                    } else if (!textLine.contains(" AND ")) {
                        /* Wrong pass # has been given */
                        feedbackBox.setText("Please specify which elements are to be compared in the following format: 'Compare 4 and 7'");
                        break;
                    } else {
                        /* Correct input scenario */
                        String element1 = textLine.substring("COMPARE ".length(), textLine.indexOf(" AND "));
                        String element2 = textLine.substring(textLine.indexOf(" AND ") + " AND ".length(), textLine.length());
                        int position1 = -1, position2 = -1;

                        Set valueSet = userSet.entrySet();
                        Iterator<Object> iterator = valueSet.iterator();
                        int counter = 0;
                        while (iterator.hasNext()) {
                            String value = iterator.next().toString();
                            if (value.length() > value.indexOf("=")) {
                                value = value.substring(value.indexOf("=") + 1);
                            } else {
                                value = "";
                            }

                            if (value.equals(element1) && position1 == -1) {
                                position1 = counter;
                            }
                            if (value.equals(element2)) {
                                if (counter != position1) {
                                    position2 = counter;
                                }
                            }
                            if (position1 > -1 && position2 > -1) {
                                break;
                            }
                            counter++;
                        }

                        if (position1 == -1) {
                            feedbackBox.setText("No such element found '" + element1 + "'. Please check the elements you are comparing in this step: " + originalTextLine);
                            break;
                        } else if (position2 == -1) {
                            feedbackBox.setText("No such element found '" + element2 + "'. Please check the elements you are comparing in this step: " + originalTextLine);
                            break;
                        } else {
                            /* Set the pointers */
                            userArrangement.setPointerOne(position1);
                            userArrangement.setPointerTwo(position2);
                        }

                        feedbackBox.setText("Comparing '" + element1 + "' and '" + element2 + "'");
                    }
                }
                lastWasSwap = false;
            } else if (textLine.contains("SKIP")) {
                /* If a comparison had not been done before this step, throw an exception */
                if (userArrangement.getPointerOne() == -1 || userArrangement.getPointerTwo() == -1) {
                    feedbackBox.setText("A comparison needs to be done before performing a 'skip'");
                    break;
                } else {
                    feedbackBox.setText("Skipped " + userSet.get(userArrangement.getPointerOne()) + " and " + userSet.get(userArrangement.getPointerTwo()));
                    userArrangement.resetPointers();
                }
                lastWasSwap = false;
            } else if (textLine.contains("SWAP")) {

                /* Store the swap indices (to be used for coloring them) */
                swapPosition1 = userArrangement.getPointerOne();
                swapPosition2 = userArrangement.getPointerTwo();

                /* If a comparison had not been done before this step, throw an exception */
                if (userArrangement.getPointerOne() == -1 || userArrangement.getPointerTwo() == -1) {
                    feedbackBox.setText("A comparison needs to be done before performing a 'swap'");
                    break;
                } else {
                    /* Do the swap */
                    Object tempElement = userSet.get(userArrangement.getPointerOne());
                    userSet.put(userArrangement.getPointerOne(), userSet.get(userArrangement.getPointerTwo()));
                    userSet.put(userArrangement.getPointerTwo(), tempElement);

                    feedbackBox.setText("Swapped " + userSet.get(userArrangement.getPointerOne()) + " and " + userSet.get(userArrangement.getPointerTwo()));
                    userArrangement.resetPointers();
                }

                lastWasSwap = true;
            } else {
                feedbackBox.setText("Invalid step '" + originalTextLine + "'. Please give a valid sort operation.");
                lastWasSwap = false;
                break;
            }

            firstPass = false;
        }

        if (!firstPass) {
            if (lastWasSwap && swapPosition1 > -1 && swapPosition2 > -1) {
                modifiedSetString = IOComponent.getColoredDisplayString(userSet, swapPosition1, swapPosition2, "gray");
            } else {
                modifiedSetString = IOComponent.getColoredDisplayString(userSet, userArrangement.getPointerOne(), userArrangement.getPointerTwo(), "orange");
            }

            passDisplayLabel.setText(modifiedSetString);
            passDisplayLabel.setVisible(true);
            passNumberLabel.setText("Modified list: ");
            passNumberLabel.setVisible(true);
            modifiedSetPanel.setVisible(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        problemStatementLabel = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        instructionLabel = new javax.swing.JLabel();
        problemDisplayLabel = new javax.swing.JLabel();
        modifiedSetPanel = new javax.swing.JPanel();
        passNumberLabel = new javax.swing.JLabel();
        passDisplayLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        studentInputBox = new javax.swing.JTextArea();
        studentInputLabel = new javax.swing.JLabel();
        dropdownSelector = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        feedbackBox = new javax.swing.JTextArea();
        feedbackBoxLabel = new javax.swing.JLabel();
        previousLevelButton = new javax.swing.JButton();
        nextLevelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        problemStatementLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        problemStatementLabel.setText("Problem Statement :");

        submitButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        submitButton.setText("Submit");
        submitButton.setEnabled(false);
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        clearButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        clearButton.setText("Clear");
        clearButton.setEnabled(false);
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        instructionLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        instructionLabel.setText("Sort the list below using the Bubble sort method");

        problemDisplayLabel.setFont(new java.awt.Font("Impact", 0, 24)); // NOI18N
        problemDisplayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        problemDisplayLabel.setText("[List comes here]");

        modifiedSetPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        passNumberLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        passNumberLabel.setText("Modified list:");

        passDisplayLabel.setFont(new java.awt.Font("Impact", 0, 20)); // NOI18N
        passDisplayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        passDisplayLabel.setText("Modified set comes here");

        javax.swing.GroupLayout modifiedSetPanelLayout = new javax.swing.GroupLayout(modifiedSetPanel);
        modifiedSetPanel.setLayout(modifiedSetPanelLayout);
        modifiedSetPanelLayout.setHorizontalGroup(
            modifiedSetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, modifiedSetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(passNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passDisplayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        modifiedSetPanelLayout.setVerticalGroup(
            modifiedSetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(passDisplayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(modifiedSetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(passNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        studentInputBox.setColumns(20);
        studentInputBox.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        studentInputBox.setLineWrap(true);
        studentInputBox.setRows(5);
        studentInputBox.setToolTipText("Enter steps of the passes needed to sort the list given above here");
        studentInputBox.setWrapStyleWord(true);
        studentInputBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                studentInputBoxMouseClicked(evt);
            }
        });
        studentInputBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                studentInputBoxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                studentInputBoxKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                studentInputBoxKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(studentInputBox);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 330, -1));

        studentInputLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        studentInputLabel.setText("Student's input box");
        jPanel1.add(studentInputLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 160, -1));

        dropdownSelector.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dropdownSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pass", "Compare", "Swap", "Skip" }));
        dropdownSelector.setToolTipText("");
        dropdownSelector.setName(""); // NOI18N
        dropdownSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropdownSelectorActionPerformed(evt);
            }
        });
        dropdownSelector.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dropdownSelectorPropertyChange(evt);
            }
        });
        jPanel1.add(dropdownSelector, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 140, -1));

        feedbackBox.setEditable(false);
        feedbackBox.setBackground(new java.awt.Color(221, 221, 221));
        feedbackBox.setColumns(20);
        feedbackBox.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        feedbackBox.setLineWrap(true);
        feedbackBox.setRows(5);
        feedbackBox.setToolTipText("Feedback about your inputs will be displayed here");
        feedbackBox.setWrapStyleWord(true);
        feedbackBox.setOpaque(false);
        jScrollPane1.setViewportView(feedbackBox);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(353, 40, 310, -1));

        feedbackBoxLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        feedbackBoxLabel.setText(" Feedback");
        jPanel1.add(feedbackBoxLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 259, -1));

        previousLevelButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        previousLevelButton.setText("<< Previous Level");
        previousLevelButton.setEnabled(false);
        previousLevelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousLevelButtonActionPerformed(evt);
            }
        });

        nextLevelButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        nextLevelButton.setText("Next Level >>");
        nextLevelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextLevelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(problemDisplayLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(instructionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(problemStatementLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(411, 411, 411))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(previousLevelButton)
                        .addGap(89, 89, 89)
                        .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(clearButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(81, 81, 81)
                        .addComponent(nextLevelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(modifiedSetPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(problemStatementLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instructionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(problemDisplayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                .addGap(28, 28, 28)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(modifiedSetPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(clearButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nextLevelButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(previousLevelButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(submitButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed

        String successMessage = "Congratulations. You have got the steps for all passes right.";
        String fileNotFoundMessage = "File with correct answers was not found. Please check the configuration.";

        /* Get the student's text, and break it down into words, separated by \n */
        String studentText = studentInputBox.getText();
        StringTokenizer studentTextTokens = new StringTokenizer(studentText, "\n");
        BufferedReader fileReader = null;

        /* Check if the file with correct answers exists, and read from it */
        try {
            if (!correctAnswersFile.exists()) {
                feedbackBox.setText(fileNotFoundMessage);
                System.err.println(fileNotFoundMessage);
            }
            fileReader = new BufferedReader(new FileReader(correctAnswersFile));
        } catch (FileNotFoundException e) {
            feedbackBox.setText(fileNotFoundMessage);
            System.err.println(fileNotFoundMessage + ": " + e);
        }

        if (fileReader != null) {

            /* Keeps track of no. of successful passes */
            int passSuccessCount = 0;
            String errorMessage = null;

            try {
                while (studentTextTokens.hasMoreTokens()) {
                    /* The step entered by the student */
                    String studentStep = studentTextTokens.nextToken();

                    /* The correct step */
                    String correctStep;
                    if ((correctStep = fileReader.readLine()) != null) {

                        if (!studentStep.equalsIgnoreCase(correctStep)) {
                            errorMessage = "Incorrect step '" + studentStep + "' in pass " + passSuccessCount + ". Try again. You have entered " + (passSuccessCount == 0 ? 0 : (passSuccessCount - 1)) + " passes correctly.";
                            break;
                        } else if (correctStep.contains("Pass") || correctStep.contains("pass") || correctStep.contains("PASS")) {
                            passSuccessCount++;
                        }
                    }
                }

                if (errorMessage != null) {
                    feedbackBox.setText(errorMessage);
                } else {
                    /* If there's still some operations remaining,
                     show an error message */
                    if (fileReader.readLine() != null) {
                        feedbackBox.setText("You have written the steps for " + (passSuccessCount == 0 ? 0 : (passSuccessCount - 1)) + " passes correctly. " + (passSuccessCount == 0 ? "" : "But there are still some passes/steps remaining."));
                    } else {
                        feedbackBox.setText(successMessage);
                        JOptionPane.showMessageDialog(null, successMessage);
                    }
                }

            } catch (IOException e) {
                System.err.print(e);
            }
        }
    }//GEN-LAST:event_submitButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        // TODO add your handling code here:
        studentInputBox.setText("");
        feedbackBox.setText("");
        passDisplayLabel.setText("");
        passNumberLabel.setText("");
    }//GEN-LAST:event_clearButtonActionPerformed

    private void studentInputBoxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentInputBoxKeyTyped

    }//GEN-LAST:event_studentInputBoxKeyTyped

    private void dropdownSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropdownSelectorActionPerformed
        String studentText = studentInputBox.getText();
        System.out.println(studentText);
        String text = (studentText == null || studentText.trim().equals("")) ? "" : studentText + "\n";
        studentInputBox.setText(text + dropdownSelector.getSelectedItem().toString());
        
        changePassDisplay();
    }//GEN-LAST:event_dropdownSelectorActionPerformed

    private void studentInputBoxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentInputBoxKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_studentInputBoxKeyReleased

    private void dropdownSelectorPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dropdownSelectorPropertyChange

    }//GEN-LAST:event_dropdownSelectorPropertyChange

    private void studentInputBoxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentInputBoxKeyPressed

        /* To enable/disable the submit & clear buttons */
        if (studentInputBox.getText() != null && !studentInputBox.getText().trim().equals("")) {
            submitButton.setEnabled(true);
            clearButton.setEnabled(true);
        } else {
            submitButton.setEnabled(false);
            clearButton.setEnabled(false);
        }

        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE || evt.getKeyCode() == KeyEvent.VK_DELETE) {
            //passDisplayLabel.setText("<html>You pressed <font color=blue>" + KeyEvent.getKeyText(evt.getKeyCode()) + "</font><html>");
            changePassDisplay();
        }
    }//GEN-LAST:event_studentInputBoxKeyPressed

    private void studentInputBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_studentInputBoxMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_studentInputBoxMouseClicked

    private void previousLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousLevelButtonActionPerformed
        // TODO add your handling code here:

        int selectedOption = JOptionPane.showConfirmDialog(null,
                "Are you sure you would like to move back to the previous level?",
                "Confirm move to previous level",
                JOptionPane.YES_NO_OPTION);
        if (selectedOption == JOptionPane.YES_OPTION) {
            this.setVisible(false);
            controller.decreaseLevel();
            controller.refreshTutor();            
        }
    }//GEN-LAST:event_previousLevelButtonActionPerformed

    private void nextLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextLevelButtonActionPerformed
        // TODO add your handling code here:

        int selectedOption = JOptionPane.showConfirmDialog(null,
                "Are you sure you would like to move to the next level?",
                "Confirm move to next level",
                JOptionPane.YES_NO_OPTION);
        if (selectedOption == JOptionPane.YES_OPTION) {
            this.setVisible(false);
            controller.increaseLevel();
            controller.refreshTutor();
        }

    }//GEN-LAST:event_nextLevelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearButton;
    private javax.swing.JComboBox dropdownSelector;
    private javax.swing.JTextArea feedbackBox;
    private javax.swing.JLabel feedbackBoxLabel;
    private javax.swing.JLabel instructionLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel modifiedSetPanel;
    private javax.swing.JButton nextLevelButton;
    private javax.swing.JLabel passDisplayLabel;
    private javax.swing.JLabel passNumberLabel;
    private javax.swing.JButton previousLevelButton;
    private javax.swing.JLabel problemDisplayLabel;
    private javax.swing.JLabel problemStatementLabel;
    private javax.swing.JTextArea studentInputBox;
    private javax.swing.JLabel studentInputLabel;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables

}

class UserListArrangement {

    Map<Integer, Object> userSet;
    int pointerOne, pointerTwo;

    public int getPointerOne() {
        return this.pointerOne;
    }

    public int getPointerTwo() {
        return this.pointerTwo;
    }

    public void setPointerOne(int position) {
        this.pointerOne = position;
    }

    public void setPointerTwo(int position) {
        this.pointerTwo = position;
    }

    public void resetPointers() {
        pointerOne = -1;
        pointerTwo = -1;
    }

    public UserListArrangement() {
    }

    public UserListArrangement(Map<Integer, Object> problemSet) {
        this.userSet = new LinkedHashMap<>(problemSet);
        resetPointers();
    }

    public Map<Integer, Object> getUserSet() {
        return this.userSet;
    }
}
