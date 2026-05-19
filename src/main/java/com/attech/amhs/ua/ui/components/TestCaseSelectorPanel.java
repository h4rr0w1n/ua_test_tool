package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import com.attech.amhs.ua.repository.TestCaseRepository;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * UI Panel for selecting test cases and subcases
 */
public class TestCaseSelectorPanel extends JPanel {
    
    private TestCaseRepository repository;
    private JComboBox<TestCase> cboTestCases;
    private JComboBox<TestSubcase> cboSubcases;
    private JLabel lblCaseDescription;
    private JLabel lblSubcaseDescription;
    private JButton btnLoadDefaults;
    private Map<String, Runnable> defaultsLoadedListeners;
    
    public TestCaseSelectorPanel(TestCaseRepository repository) {
        this.repository = repository;
        this.defaultsLoadedListeners = new HashMap<>();
        initUI();
    }
    
    private void initUI() {
        setBorder(new TitledBorder("Test Case Selection"));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Test Case Label & Combo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        add(new JLabel("Test Case:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboTestCases = new JComboBox<>();
        populateTestCases();
        cboTestCases.addActionListener(e -> handleTestCaseChanged());
        add(cboTestCases, gbc);
        
        // Subcase Label & Combo
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        add(new JLabel("Subcase:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboSubcases = new JComboBox<>();
        cboSubcases.addActionListener(e -> handleSubcaseChanged());
        add(cboSubcases, gbc);
        
        // Test Case Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        add(new JLabel("Case Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        lblCaseDescription = new JLabel("");
        lblCaseDescription.setForeground(Color.GRAY);
        lblCaseDescription.setPreferredSize(new Dimension(300, 30));
        add(lblCaseDescription, gbc);
        
        // Subcase Description
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        add(new JLabel("Subcase Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        lblSubcaseDescription = new JLabel("");
        lblSubcaseDescription.setForeground(Color.GRAY);
        add(lblSubcaseDescription, gbc);
        
        // Load Defaults Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        btnLoadDefaults = new JButton("Load Default AMHS Configuration");
        btnLoadDefaults.addActionListener(e -> handleLoadDefaults());
        add(btnLoadDefaults, gbc);
    }
    
    private void populateTestCases() {
        cboTestCases.removeAllItems();
        for (TestCase testCase : repository.getTestCasesList()) {
            cboTestCases.addItem(testCase);
        }
        if (cboTestCases.getItemCount() > 0) {
            cboTestCases.setSelectedIndex(0);
            handleTestCaseChanged();
        }
    }
    
    private void handleTestCaseChanged() {
        TestCase selectedCase = (TestCase) cboTestCases.getSelectedItem();
        if (selectedCase != null) {
            lblCaseDescription.setText(selectedCase.getDescription() != null ? 
                                      selectedCase.getDescription() : "");
            
            // Populate subcases
            cboSubcases.removeAllItems();
            for (TestSubcase subcase : selectedCase.getSubcases()) {
                cboSubcases.addItem(subcase);
            }
            if (cboSubcases.getItemCount() > 0) {
                cboSubcases.setSelectedIndex(0);
            }
            handleSubcaseChanged();
        }
    }
    
    private void handleSubcaseChanged() {
        TestSubcase selectedSubcase = (TestSubcase) cboSubcases.getSelectedItem();
        if (selectedSubcase != null) {
            lblSubcaseDescription.setText(selectedSubcase.getDescription() != null ? 
                                         selectedSubcase.getDescription() : "");
        }
    }
    
    private void handleLoadDefaults() {
        TestSubcase selectedSubcase = (TestSubcase) cboSubcases.getSelectedItem();
        if (selectedSubcase != null && !selectedSubcase.getAmhsDefaults().isEmpty()) {
            // Notify listeners that defaults should be loaded
            for (Runnable listener : defaultsLoadedListeners.values()) {
                listener.run();
            }
        }
    }
    
    public TestCase getSelectedTestCase() {
        return (TestCase) cboTestCases.getSelectedItem();
    }
    
    public TestSubcase getSelectedSubcase() {
        return (TestSubcase) cboSubcases.getSelectedItem();
    }
    
    public void addDefaultsLoadedListener(String key, Runnable listener) {
        defaultsLoadedListeners.put(key, listener);
    }
    
    public void refresh() {
        populateTestCases();
    }
}
