package com.attech.amhs.ua.ui.components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panel for displaying descriptions and expectations for test cases and subcases.
 * This replaces the previous ActionLogsPanel to focus strictly on test documentation.
 */
public class DescriptionPanel extends JPanel {
    
    private JTextArea txtDescription;
    private JScrollPane scrollPane;
    
    public DescriptionPanel() {
        initUI();
    }
    
    private void initUI() {
        setBorder(new TitledBorder("Test Case / Subcase Description & Expectations"));
        setLayout(new BorderLayout());
        
        txtDescription = new JTextArea();
        txtDescription.setEditable(false);
        txtDescription.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(txtDescription);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(0, 200));
    }
    
    /**
     * Display test case description with expectations
     */
    public void displayTestCaseDescription(String testCaseId, String name, String description, String testCriteria, String reference) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TEST CASE: ").append(testCaseId).append(" ===\n\n");
        sb.append("Name: ").append(name).append("\n\n");
        if (description != null && !description.isEmpty()) {
            sb.append("Description:\n").append(description).append("\n\n");
        }
        if (testCriteria != null && !testCriteria.isEmpty()) {
            sb.append("Test Criteria (Expectation):\n").append(testCriteria).append("\n\n");
        }
        if (reference != null && !reference.isEmpty()) {
            sb.append("Reference: ").append(reference).append("\n");
        }
        txtDescription.setText(sb.toString());
    }
    
    /**
     * Display subcase description with expectations
     */
    public void displaySubcaseDescription(String subcaseId, String name, String description, String expectation) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SUBCASE: ").append(subcaseId).append(" ===\n\n");
        sb.append("Name: ").append(name).append("\n\n");
        if (description != null && !description.isEmpty()) {
            sb.append("Description:\n").append(description).append("\n\n");
        }
        if (expectation != null && !expectation.isEmpty()) {
            sb.append("Expected Result:\n").append(expectation).append("\n");
        } else {
            sb.append("Expected Result: Message shall be sent/received according to the test case criteria.\n");
        }
        txtDescription.setText(sb.toString());
    }
    
    public void clear() {
        txtDescription.setText("");
    }
}
