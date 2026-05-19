package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.export.XlsxExporter;
import com.attech.amhs.ua.repository.TestCaseRepository;
import com.attech.amhs.ua.service.TestSessionRecorder;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * UI Panel for test session control and export
 */
public class TestControlPanel extends JPanel {
    
    private TestCaseRepository repository;
    private TestSessionRecorder recorder;
    private JButton btnStartSession;
    private JButton btnEndSession;
    private JButton btnExport;
    private JLabel lblSessionStatus;
    private JLabel lblMessageCount;
    
    public TestControlPanel(TestCaseRepository repository, TestSessionRecorder recorder) {
        this.repository = repository;
        this.recorder = recorder;
        initUI();
    }
    
    private void initUI() {
        setBorder(new TitledBorder("Test Session Control"));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Session Status
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(new JLabel("Session Status:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        lblSessionStatus = new JLabel("Not Started");
        lblSessionStatus.setForeground(Color.GRAY);
        add(lblSessionStatus, gbc);
        
        // Message Count
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("Messages Sent:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        lblMessageCount = new JLabel("0");
        add(lblMessageCount, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        btnStartSession = new JButton("Start Session");
        btnStartSession.addActionListener(this::handleStartSession);
        add(btnStartSession, gbc);
        
        gbc.gridx = 1;
        btnEndSession = new JButton("End Session");
        btnEndSession.setEnabled(false);
        btnEndSession.addActionListener(this::handleEndSession);
        add(btnEndSession, gbc);
        
        gbc.gridx = 2;
        btnExport = new JButton("Export to XLSX");
        btnExport.setEnabled(false);
        btnExport.addActionListener(this::handleExport);
        add(btnExport, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        JButton btnReset = new JButton("Reset Session");
        btnReset.addActionListener(this::handleReset);
        add(btnReset, gbc);
    }
    
    private void handleStartSession(ActionEvent e) {
        recorder.createNewSession();
        recorder.startSession();
        lblSessionStatus.setText("Running");
        lblSessionStatus.setForeground(new Color(0, 128, 0));
        btnStartSession.setEnabled(false);
        btnEndSession.setEnabled(true);
        btnExport.setEnabled(false);
    }
    
    private void handleEndSession(ActionEvent e) {
        recorder.endSession();
        lblSessionStatus.setText("Ended");
        lblSessionStatus.setForeground(new Color(128, 0, 0));
        btnStartSession.setEnabled(true);
        btnEndSession.setEnabled(false);
        btnExport.setEnabled(true);
        updateMessageCount();
    }
    
    private void handleExport(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("amhs_test_results_" + System.currentTimeMillis() + ".xlsx"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                XlsxExporter exporter = new XlsxExporter(repository, recorder.getTestSession());
                exporter.export(file.getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        "Test results exported successfully to:\n" + file.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Export failed: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleReset(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset the session?\nThis cannot be undone.",
                "Reset Session",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            recorder.createNewSession();
            lblSessionStatus.setText("Not Started");
            lblSessionStatus.setForeground(Color.GRAY);
            lblMessageCount.setText("0");
            btnStartSession.setEnabled(true);
            btnEndSession.setEnabled(false);
            btnExport.setEnabled(false);
        }
    }
    
    public void updateMessageCount() {
        lblMessageCount.setText(String.valueOf(recorder.getMessageLogCount()));
    }
    
    public boolean isSessionRunning() {
        return btnEndSession.isEnabled();
    }
}
