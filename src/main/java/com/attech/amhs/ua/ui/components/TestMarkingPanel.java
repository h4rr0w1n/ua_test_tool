package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import com.attech.amhs.ua.repository.TestCaseRepository;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UI Panel for marking test cases and subcases as pass or fail
 * Redesigned for compact horizontal layout at the bottom
 */
public class TestMarkingPanel extends JPanel {
    
    private TestCaseRepository repository;
    private JRadioButton rbSubcasePass;
    private JRadioButton rbSubcaseFail;
    private JTextArea txtSubcaseComment;
    private JButton btnMarkSubcase;
    private JLabel lblSubcaseStatus;
    
    private JRadioButton rbCasePass;
    private JRadioButton rbCaseFail;
    private JTextArea txtCaseComment;
    private JButton btnMarkCase;
    private JLabel lblCaseStatus;
    
    private JSplitPane mainSplitPane;
    
    public TestMarkingPanel(TestCaseRepository repository) {
        this.repository = repository;
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Test Marking"));
        
        // Subcase Marking Panel (left)
        JPanel subcasePanel = createSubcaseMarkingPanel();
        
        // Case Marking Panel (right)
        JPanel casePanel = createCaseMarkingPanel();
        
        // Split pane with subcase on left, case on right
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, subcasePanel, casePanel);
        mainSplitPane.setDividerLocation(0.5);
        mainSplitPane.setResizeWeight(0.5);
        mainSplitPane.setContinuousLayout(true);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(0, 120));
    }
    
    private JPanel createSubcaseMarkingPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Subcase Marking (One-Time Only)"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Result selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Result:"), gbc);
        
        rbSubcasePass = new JRadioButton("PASS");
        rbSubcaseFail = new JRadioButton("FAIL");
        rbSubcasePass.setSelected(true);
        ButtonGroup grp = new ButtonGroup();
        grp.add(rbSubcasePass);
        grp.add(rbSubcaseFail);
        
        gbc.gridx = 1;
        panel.add(rbSubcasePass, gbc);
        
        gbc.gridx = 2;
        panel.add(rbSubcaseFail, gbc);
        
        // Comment field (compact)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Comment:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtSubcaseComment = new JTextArea(2, 15);
        txtSubcaseComment.setLineWrap(true);
        txtSubcaseComment.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtSubcaseComment);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, gbc);
        
        // Mark button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnMarkSubcase = new JButton("Mark Subcase");
        btnMarkSubcase.addActionListener(this::handleMarkSubcase);
        panel.add(btnMarkSubcase, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        lblSubcaseStatus = new JLabel("Ready to mark");
        lblSubcaseStatus.setForeground(Color.BLUE);
        lblSubcaseStatus.setFont(new Font(lblSubcaseStatus.getFont().getName(), Font.PLAIN, 10));
        panel.add(lblSubcaseStatus, gbc);
        
        return panel;
    }
    
    private JPanel createCaseMarkingPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Test Case Marking (Changeable)"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Result selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Result:"), gbc);
        
        rbCasePass = new JRadioButton("PASS");
        rbCaseFail = new JRadioButton("FAIL");
        rbCasePass.setSelected(true);
        ButtonGroup grp = new ButtonGroup();
        grp.add(rbCasePass);
        grp.add(rbCaseFail);
        
        gbc.gridx = 1;
        panel.add(rbCasePass, gbc);
        
        gbc.gridx = 2;
        panel.add(rbCaseFail, gbc);
        
        // Comment field (compact)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Comment:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtCaseComment = new JTextArea(2, 15);
        txtCaseComment.setLineWrap(true);
        txtCaseComment.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtCaseComment);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, gbc);
        
        // Mark button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnMarkCase = new JButton("Mark Test Case");
        btnMarkCase.addActionListener(this::handleMarkCase);
        panel.add(btnMarkCase, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        lblCaseStatus = new JLabel("Ready to mark");
        lblCaseStatus.setForeground(Color.BLUE);
        lblCaseStatus.setFont(new Font(lblCaseStatus.getFont().getName(), Font.PLAIN, 10));
        panel.add(lblCaseStatus, gbc);
        
        return panel;
    }
    
    private void handleMarkSubcase(ActionEvent e) {
        // This will be called from the UI; subclass or pass callback to implement
        lblSubcaseStatus.setText("Subcase marked!");
        lblSubcaseStatus.setForeground(new Color(0, 128, 0));
    }
    
    private void handleMarkCase(ActionEvent e) {
        // This will be called from the UI; subclass or pass callback to implement
        lblCaseStatus.setText("Test case marked!");
        lblCaseStatus.setForeground(new Color(0, 128, 0));
    }
    
    public String getSubcaseResult() {
        return rbSubcasePass.isSelected() ? "PASS" : "FAIL";
    }
    
    public String getSubcaseComment() {
        return txtSubcaseComment.getText();
    }
    
    public String getCaseResult() {
        return rbCasePass.isSelected() ? "PASS" : "FAIL";
    }
    
    public String getCaseComment() {
        return txtCaseComment.getText();
    }
    
    public void clearSubcaseForm() {
        rbSubcasePass.setSelected(true);
        txtSubcaseComment.setText("");
        lblSubcaseStatus.setText("Ready to mark");
        lblSubcaseStatus.setForeground(Color.BLUE);
    }
    
    public void clearCaseForm() {
        rbCasePass.setSelected(true);
        txtCaseComment.setText("");
        lblCaseStatus.setText("Ready to mark");
        lblCaseStatus.setForeground(Color.BLUE);
    }
    
    public void setSubcaseStatus(String message, Color color) {
        lblSubcaseStatus.setText(message);
        lblSubcaseStatus.setForeground(color);
    }
    
    public void setCaseStatus(String message, Color color) {
        lblCaseStatus.setText(message);
        lblCaseStatus.setForeground(color);
    }
}
