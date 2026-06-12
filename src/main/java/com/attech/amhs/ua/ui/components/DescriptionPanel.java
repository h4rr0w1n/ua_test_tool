package com.attech.amhs.ua.ui.components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Bottom-of-UI panel that combines test case / subcase descriptions and expectations
 * with the action log strip.
 *
 * <p>It replaces the previous {@code ActionLogsPanel}. Test documentation is shown
 * by calling {@link #displayTestCaseDescription} or {@link #displaySubcaseDescription},
 * which REPLACES the current text. Send / receive / general action events are
 * appended on top of whatever description is currently displayed via
 * {@link #logSendMessage}, {@link #logReceiveMessage} and {@link #logAction}.</p>
 */
public class DescriptionPanel extends JPanel {

    private JTextArea txtContent;
    private JScrollPane scrollPane;
    private SimpleDateFormat dateFormat;

    public DescriptionPanel() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        initUI();
    }

    private void initUI() {
        setBorder(new TitledBorder("Description"));
        setLayout(new BorderLayout());

        txtContent = new JTextArea();
        txtContent.setEditable(false);
        txtContent.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);

        scrollPane = new JScrollPane(txtContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(0, 200));
    }

    // ── Description display ───────────────────────────────────────────────

    /**
     * Display test case description with expectations (replaces current text).
     */
    public void displayTestCaseDescription(String testCaseId, String name,
                                           String description, String testCriteria,
                                           String reference) {
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
        txtContent.setText(sb.toString());
    }

    /**
     * Display subcase description with expectations (replaces current text).
     */
    public void displaySubcaseDescription(String subcaseId, String name,
                                          String description, String expectation) {
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
        txtContent.setText(sb.toString());
    }

    public void clear() {
        txtContent.setText("");
    }


}
