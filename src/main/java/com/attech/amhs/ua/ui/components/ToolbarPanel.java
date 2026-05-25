package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.service.TestSessionRecorder;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Top toolbar containing all primary action buttons grouped by function:
 * [Timer] | [Case Pass/Fail/Note] | [Message Pass/Fail/Note] | [Results] [Export] | [Settings]
 */
public class ToolbarPanel extends JPanel {

    private final TestSessionRecorder recorder;

    // Timer widgets
    private JButton btnStartStopTimer;
    private JLabel lblTimer;
    private Timer swingTimer;
    private boolean timerRunning = false;

    // Callbacks
    private Runnable onMarkCasePass;
    private Runnable onMarkCaseFail;
    private Runnable onNoteCase;
    private Runnable onMarkMsgPass;
    private Runnable onMarkMsgFail;
    private Runnable onNoteMsg;
    private Runnable onShowResults;
    private Runnable onExport;
    private Runnable onSettings;

    public ToolbarPanel(TestSessionRecorder recorder) {
        this.recorder = recorder;
        initUI();
    }

    private void initUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 3));
        setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(180, 180, 180)),
            new EmptyBorder(2, 4, 2, 4)
        ));
        setBackground(new Color(245, 245, 248));

        // ── Timer group ──────────────────────────────────────────────────
        lblTimer = new JLabel("00:00:00");
        lblTimer.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTimer.setForeground(new Color(40, 40, 40));
        lblTimer.setBorder(new EmptyBorder(0, 4, 0, 4));
        add(lblTimer);

        btnStartStopTimer = makeButton("\u25B6 Start", new Color(30, 130, 30), null);
        btnStartStopTimer.addActionListener(e -> handleStartStopTimer());
        add(btnStartStopTimer);

        add(makeSeparator());

        // ── Case marking group ───────────────────────────────────────────
        JPanel caseGroup = makeGroup("Case");
        caseGroup.add(makeButton("\u2714 Pass", new Color(0, 120, 0),
                e -> { if (onMarkCasePass != null) onMarkCasePass.run(); }));
        caseGroup.add(makeButton("\u2718 Fail", new Color(180, 0, 0),
                e -> { if (onMarkCaseFail != null) onMarkCaseFail.run(); }));
        caseGroup.add(makeButton("\u270E Note", null,
                e -> { if (onNoteCase != null) onNoteCase.run(); }));
        add(caseGroup);

        add(makeSeparator());

        // ── Message marking group ────────────────────────────────────────
        JPanel msgGroup = makeGroup("Message");
        msgGroup.add(makeButton("\u2714 Pass", new Color(0, 120, 0),
                e -> { if (onMarkMsgPass != null) onMarkMsgPass.run(); }));
        msgGroup.add(makeButton("\u2718 Fail", new Color(180, 0, 0),
                e -> { if (onMarkMsgFail != null) onMarkMsgFail.run(); }));
        msgGroup.add(makeButton("\u270E Note", null,
                e -> { if (onNoteMsg != null) onNoteMsg.run(); }));
        add(msgGroup);

        add(makeSeparator());

        // ── Output group ─────────────────────────────────────────────────
        add(makeButton("\uD83D\uDCCA Results", null,
                e -> { if (onShowResults != null) onShowResults.run(); }));
        add(makeButton("\uD83D\uDCE5 Export XLSX", null,
                e -> { if (onExport != null) onExport.run(); }));

        add(makeSeparator());

        // ── Settings ─────────────────────────────────────────────────────
        add(makeButton("\u2699 Settings", null,
                e -> { if (onSettings != null) onSettings.run(); }));

        // Swing timer to refresh the elapsed-time display every 100 ms
        swingTimer = new Timer(100, e -> updateTimerDisplay());
        swingTimer.start();
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private JButton makeButton(String text, Color fg, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(btn.getFont().deriveFont(11f));
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(2, 6, 2, 6));
        if (fg != null) btn.setForeground(fg);
        if (listener != null) btn.addActionListener(listener);
        return btn;
    }

    private JPanel makeGroup(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.PLAIN, 9),
            Color.GRAY
        ));
        return panel;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(6, 32));
        sep.setForeground(new Color(190, 190, 190));
        return sep;
    }

    // ── Timer logic ───────────────────────────────────────────────────────

    private void handleStartStopTimer() {
        if (timerRunning) {
            recorder.stopTimer();
            btnStartStopTimer.setText("\u25B6 Start");
            btnStartStopTimer.setForeground(new Color(30, 130, 30));
            timerRunning = false;
        } else {
            recorder.startTimer();
            btnStartStopTimer.setText("\u25A0 Stop");
            btnStartStopTimer.setForeground(new Color(180, 0, 0));
            timerRunning = true;
        }
    }

    private void updateTimerDisplay() {
        if (timerRunning) {
            long elapsed = recorder.getElapsedTime();
            lblTimer.setText(TestSessionRecorder.formatElapsedTime(elapsed));
        }
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }

    // ── Callback setters ──────────────────────────────────────────────────

    public void setOnMarkCasePass(Runnable r) { this.onMarkCasePass = r; }
    public void setOnMarkCaseFail(Runnable r) { this.onMarkCaseFail = r; }
    public void setOnNoteCase(Runnable r)     { this.onNoteCase = r; }
    public void setOnMarkMsgPass(Runnable r)  { this.onMarkMsgPass = r; }
    public void setOnMarkMsgFail(Runnable r)  { this.onMarkMsgFail = r; }
    public void setOnNoteMsg(Runnable r)      { this.onNoteMsg = r; }
    public void setOnShowResults(Runnable r)  { this.onShowResults = r; }
    public void setOnExport(Runnable r)       { this.onExport = r; }
    public void setOnSettings(Runnable r)     { this.onSettings = r; }
}
