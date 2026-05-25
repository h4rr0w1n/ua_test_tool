package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import com.attech.amhs.ua.repository.TestCaseRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Modal dialog showing a summary of all test case pass/fail results with colour coding.
 */
public class ResultsPopupDialog extends JDialog {

    private final TestCaseRepository repository;

    public ResultsPopupDialog(Frame parent, TestCaseRepository repository) {
        super(parent, "Test Results Summary", true);
        this.repository = repository;
        initUI();
        pack();
        setMinimumSize(new Dimension(750, 450));
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(6, 6));
        getRootPane().setBorder(new EmptyBorder(8, 8, 8, 8));

        // ── Summary bar ───────────────────────────────────────────────────
        List<TestCase> cases = repository.getTestCasesList();
        int totalCases = cases.size();
        int totalSubs  = repository.getSubcaseCount();
        int passed     = repository.getPassedSubcaseCount();
        int failed     = repository.getFailedSubcaseCount();
        int untested   = totalSubs - passed - failed;

        JPanel summaryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        summaryBar.setBackground(new Color(240, 242, 248));
        summaryBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        summaryBar.add(makeStatLabel("Cases:", String.valueOf(totalCases), Color.DARK_GRAY));
        summaryBar.add(makeStatLabel("Subcases:", String.valueOf(totalSubs), Color.DARK_GRAY));
        summaryBar.add(makeStatLabel("Passed:", String.valueOf(passed), new Color(0, 130, 0)));
        summaryBar.add(makeStatLabel("Failed:", String.valueOf(failed), new Color(180, 0, 0)));
        summaryBar.add(makeStatLabel("Untested:", String.valueOf(untested), Color.GRAY));
        add(summaryBar, BorderLayout.NORTH);

        // ── Main table ────────────────────────────────────────────────────
        String[] caseColumns = {"Case ID", "Name", "Result", "Comment"};
        DefaultTableModel caseModel = new DefaultTableModel(caseColumns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (TestCase tc : cases) {
            String result = tc.getResult() != null ? tc.getResult() : "Not Tested";
            caseModel.addRow(new Object[]{
                tc.getId(),
                tc.getName(),
                result,
                tc.getComment() != null ? tc.getComment() : ""
            });
        }

        JTable caseTable = new JTable(caseModel);
        caseTable.setRowHeight(22);
        caseTable.setFont(caseTable.getFont().deriveFont(12f));
        caseTable.getTableHeader().setFont(caseTable.getFont().deriveFont(Font.BOLD, 12f));
        caseTable.setDefaultRenderer(Object.class, new ResultCellRenderer());
        caseTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        caseTable.getColumnModel().getColumn(1).setPreferredWidth(280);
        caseTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        caseTable.getColumnModel().getColumn(3).setPreferredWidth(250);

        // ── Subcase table (detail for selected case) ──────────────────────
        String[] subColumns = {"Subcase ID", "Name", "Result", "Comment"};
        DefaultTableModel subModel = new DefaultTableModel(subColumns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable subTable = new JTable(subModel);
        subTable.setRowHeight(20);
        subTable.setFont(subTable.getFont().deriveFont(11f));
        subTable.getTableHeader().setFont(subTable.getFont().deriveFont(Font.BOLD, 11f));
        subTable.setDefaultRenderer(Object.class, new ResultCellRenderer());

        caseTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = caseTable.getSelectedRow();
            subModel.setRowCount(0);
            if (row >= 0) {
                TestCase tc = cases.get(row);
                for (TestSubcase sc : tc.getSubcases()) {
                    String r = sc.getResult() != null ? sc.getResult() : "Not Tested";
                    subModel.addRow(new Object[]{
                        sc.getId(), sc.getName(), r,
                        sc.getComment() != null ? sc.getComment() : ""
                    });
                }
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(caseTable), new JScrollPane(subTable));
        split.setDividerLocation(200);
        split.setResizeWeight(0.55);
        split.setContinuousLayout(true);
        add(split, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel makeStatLabel(String label, String value, Color valueColor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12f));
        JLabel val = new JLabel(value);
        val.setFont(val.getFont().deriveFont(Font.BOLD, 13f));
        val.setForeground(valueColor);
        p.add(lbl);
        p.add(val);
        return p;
    }

    /** Colour-codes table rows by result value in column 2 */
    private static class ResultCellRenderer extends DefaultTableCellRenderer {
        private static final Color PASS_BG   = new Color(220, 255, 220);
        private static final Color FAIL_BG   = new Color(255, 220, 220);
        private static final Color NONE_BG   = new Color(250, 250, 250);

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            if (!isSelected) {
                String result = (String) table.getValueAt(row, 2);
                if ("PASS".equals(result)) {
                    setBackground(PASS_BG);
                    if (col == 2) setForeground(new Color(0, 110, 0));
                } else if ("FAIL".equals(result)) {
                    setBackground(FAIL_BG);
                    if (col == 2) setForeground(new Color(160, 0, 0));
                } else {
                    setBackground(NONE_BG);
                    setForeground(Color.GRAY);
                }
            }
            return this;
        }
    }
}
