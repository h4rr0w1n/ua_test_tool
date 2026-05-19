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
    
    public TestMarkingPanel(TestCaseRepository repository) {
        this.repository = repository;
        initUI();
    }
    
    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Subcase Marking Section
        add(createSubcaseMarkingPanel());
        add(Box.createVerticalStrut(10));
        
        // Case Marking Section
        add(createCaseMarkingPanel());
    }
    
    private JPanel createSubcaseMarkingPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Subcase Marking (One-Time Only)"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Result selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Result:"), gbc);
        
        rbSubcasePass = new JRadioButton("PASS");
        rbSubcaseFail = new JRadioButton("FAIL");
        rbSubcasePass.setSelected(true);
        ButtonGroup grp = new ButtonGroup();
        grp.add(rbSubcasePass);
        grp.add(rbSubcaseFail);
        
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        panel.add(rbSubcasePass, gbc);
        
        gbc.gridx = 3;
        panel.add(rbSubcaseFail, gbc);
        
        // Comments
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        panel.add(new JLabel("Comment:"), gbc);
        
        gbc.gridy = 2;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        txtSubcaseComment = new JTextArea(3, 30);
        txtSubcaseComment.setLineWrap(true);
        txtSubcaseComment.setWrapStyleWord(true);
        panel.add(new JScrollPane(txtSubcaseComment), gbc);
        
        // Mark button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnMarkSubcase = new JButton("Mark Subcase");
        btnMarkSubcase.addActionListener(this::handleMarkSubcase);
        panel.add(btnMarkSubcase, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        lblSubcaseStatus = new JLabel("Ready to mark");
        lblSubcaseStatus.setForeground(Color.BLUE);
        panel.add(lblSubcaseStatus, gbc);
        
        return panel;
    }
    
    private JPanel createCaseMarkingPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Test Case Marking (Changeable)"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Result selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Result:"), gbc);
        
        rbCasePass = new JRadioButton("PASS");
        rbCaseFail = new JRadioButton("FAIL");
        rbCasePass.setSelected(true);
        ButtonGroup grp = new ButtonGroup();
        grp.add(rbCasePass);
        grp.add(rbCaseFail);
        
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        panel.add(rbCasePass, gbc);
        
        gbc.gridx = 3;
        panel.add(rbCaseFail, gbc);
        
        // Comments
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        panel.add(new JLabel("Comment:"), gbc);
        
        gbc.gridy = 2;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        txtCaseComment = new JTextArea(3, 30);
        txtCaseComment.setLineWrap(true);
        txtCaseComment.setWrapStyleWord(true);
        panel.add(new JScrollPane(txtCaseComment), gbc);
        
        // Mark button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnMarkCase = new JButton("Mark Test Case");
        btnMarkCase.addActionListener(this::handleMarkCase);
        panel.add(btnMarkCase, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        lblCaseStatus = new JLabel("Ready to mark");
        lblCaseStatus.setForeground(Color.BLUE);
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
