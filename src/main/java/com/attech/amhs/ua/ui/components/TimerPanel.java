package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.service.TestSessionRecorder;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * UI Panel for controlling test session timer
 */
public class TimerPanel extends JPanel {
    
    private TestSessionRecorder recorder;
    private JButton btnStartStop;
    private JLabel lblTimer;
    private Timer swingTimer;
    
    public TimerPanel(TestSessionRecorder recorder) {
        this.recorder = recorder;
        initUI();
    }
    
    private void initUI() {
        setBorder(new TitledBorder("Session Timer"));
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // Timer display
        lblTimer = new JLabel("00:00:00");
        lblTimer.setFont(new Font("Monospaced", Font.BOLD, 24));
        add(lblTimer);
        
        // Start/Stop button
        btnStartStop = new JButton("Start");
        btnStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStartStop();
            }
        });
        add(btnStartStop);
        
        // Set up swing timer to update display
        swingTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDisplay();
            }
        });
        swingTimer.start();
    }
    
    private void handleStartStop() {
        if (recorder.isTimerRunning()) {
            // Stop
            long elapsed = recorder.stopTimer();
            btnStartStop.setText("Start");
            lblTimer.setText(TestSessionRecorder.formatElapsedTime(elapsed));
        } else {
            // Start
            recorder.startTimer();
            btnStartStop.setText("Stop");
        }
    }
    
    private void updateDisplay() {
        if (recorder.isTimerRunning()) {
            long elapsed = recorder.getElapsedTime();
            lblTimer.setText(TestSessionRecorder.formatElapsedTime(elapsed));
        }
    }
    
    public void resetTimer() {
        recorder.resetTimer();
        btnStartStop.setText("Start");
        lblTimer.setText("00:00:00");
    }
}
