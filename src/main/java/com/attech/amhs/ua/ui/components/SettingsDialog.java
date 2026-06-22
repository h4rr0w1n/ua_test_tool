package com.attech.amhs.ua.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tool-level settings dialog.
 * Currently exposes: default export directory, auto-scroll preference.
 */
public class SettingsDialog extends JDialog {

    private JTextField txtDefaultExportPath;
    private JCheckBox  chkAutoScrollLogs;
    private JCheckBox  chkAutoScrollMessages;
    private JCheckBox  chkLimitMessages;
    private JSpinner   spinMessageLimit;
    private JCheckBox  chkClearOldLogsOnSend;

    // Persisted settings (static so they survive dialog re-opens)
    private static String defaultExportPath  = ".";
    private static boolean autoScrollLogs    = true;
    private static boolean autoScrollMessages = true;
    private static boolean limitMessages = false;
    private static int messageLimit = 100;
    private static boolean clearOldLogsOnSend = false;

    public SettingsDialog(Frame parent) {
        super(parent, "Tool Settings", true);
        initUI();
        pack();
        setMinimumSize(new Dimension(420, 200));
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(6, 6));
        getRootPane().setBorder(new EmptyBorder(8, 8, 8, 8));

        // ── Form ──────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(5, 5, 5, 5);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        // Default export path
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("Default export directory:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDefaultExportPath = new JTextField(defaultExportPath, 28);
        form.add(txtDefaultExportPath, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        JButton btnBrowse = new JButton("Browse…");
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(txtDefaultExportPath.getText());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtDefaultExportPath.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        form.add(btnBrowse, gbc);

        // Auto-scroll logs
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        chkAutoScrollLogs = new JCheckBox("Auto-scroll action logs", autoScrollLogs);
        form.add(chkAutoScrollLogs, gbc);

        // Auto-scroll messages
        gbc.gridy = 2;
        chkAutoScrollMessages = new JCheckBox("Auto-scroll message panels", autoScrollMessages);
        form.add(chkAutoScrollMessages, gbc);

        // Limit Messages
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        chkLimitMessages = new JCheckBox("Limit number of messages shown", limitMessages);
        form.add(chkLimitMessages, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        spinMessageLimit = new JSpinner(new SpinnerNumberModel(messageLimit, 1, 10000, 10));
        spinMessageLimit.setEnabled(limitMessages);
        chkLimitMessages.addActionListener(e -> spinMessageLimit.setEnabled(chkLimitMessages.isSelected()));
        form.add(spinMessageLimit, gbc);

        // Clear Old Logs on Send
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        chkClearOldLogsOnSend = new JCheckBox("Clear old sending logs when sending", clearOldLogsOnSend);
        form.add(chkClearOldLogsOnSend, gbc);

        add(form, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> {
            defaultExportPath    = txtDefaultExportPath.getText().trim();
            autoScrollLogs       = chkAutoScrollLogs.isSelected();
            autoScrollMessages   = chkAutoScrollMessages.isSelected();
            limitMessages        = chkLimitMessages.isSelected();
            messageLimit         = (Integer) spinMessageLimit.getValue();
            clearOldLogsOnSend   = chkClearOldLogsOnSend.isSelected();
            dispose();
        });
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        footer.add(btnOk);
        footer.add(btnCancel);
        add(footer, BorderLayout.SOUTH);
    }

    // ── Static accessors so the rest of the app can read saved prefs ──────

    public static String getDefaultExportPath()   { return defaultExportPath; }
    public static boolean isAutoScrollLogs()      { return autoScrollLogs; }
    public static boolean isAutoScrollMessages()  { return autoScrollMessages; }
    public static boolean isLimitMessages()       { return limitMessages; }
    public static int getMessageLimit()           { return messageLimit; }
    public static boolean isClearOldLogsOnSend()  { return clearOldLogsOnSend; }
}
