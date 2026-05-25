package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import com.attech.amhs.ua.repository.TestCaseRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Left-column panel for selecting CTSW test cases and their messages.
 *
 * Layout:
 *   ┌─ CASE: [CTSW0xx combo] ─────────────────┐
 *   │  |_ default message                      │
 *   │  |_ message 1  (subcase 1)               │
 *   │  |_ message 2  (subcase 2)               │
 *   │  ...                                     │
 *   │  [description text area]                 │
 *   ├─────────────────────────────────────────┤
 *   │  <Load defaults>   <Send defaults>       │
 *   └─────────────────────────────────────────┘
 */
public class TestCaseSelectorPanel extends JPanel {

    private final TestCaseRepository repository;

    // Top combo
    private JComboBox<TestCase> cboTestCases;

    // Tree
    private JTree subcaseTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    // Description
    private JTextArea txtDescription;

    // Buttons
    private JButton btnLoadDefaults;
    private JButton btnSendDefaults;

    // Listener maps (kept for backward-compatibility with AMHSMessageUI wiring)
    private final Map<String, Runnable> defaultsLoadedListeners = new HashMap<>();
    private final Map<String, Runnable> copyDefaultsListeners   = new HashMap<>();
    private final Map<String, Runnable> sendDefaultsListeners   = new HashMap<>();

    public TestCaseSelectorPanel(TestCaseRepository repository) {
        this.repository = repository;
        initUI();
    }

    // ── UI construction ───────────────────────────────────────────────────

    private void initUI() {
        setBorder(new TitledBorder("Test Case"));
        setLayout(new BorderLayout(4, 4));

        add(buildTopCombo(),   BorderLayout.NORTH);
        add(buildCenterTree(), BorderLayout.CENTER);
        add(buildBottomBtns(), BorderLayout.SOUTH);

        populateTestCases();
    }

    /** CTSW case selector row */
    private JPanel buildTopCombo() {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.setBorder(new EmptyBorder(2, 2, 2, 2));

        JLabel lbl = new JLabel("CASE:");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
        row.add(lbl, BorderLayout.WEST);

        cboTestCases = new JComboBox<>();
        cboTestCases.addActionListener(e -> handleTestCaseChanged());
        row.add(cboTestCases, BorderLayout.CENTER);
        return row;
    }

    /** Tree + description split */
    private JSplitPane buildCenterTree() {
        // ── Tree ─────────────────────────────────────────────────────────
        rootNode  = new DefaultMutableTreeNode("Messages");
        treeModel = new DefaultTreeModel(rootNode);
        subcaseTree = new JTree(treeModel);
        subcaseTree.setRootVisible(false);
        subcaseTree.setShowsRootHandles(true);
        subcaseTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        subcaseTree.setCellRenderer(new SubcaseCellRenderer());
        subcaseTree.addTreeSelectionListener(e -> handleTreeSelectionChanged());

        JScrollPane treeScroll = new JScrollPane(subcaseTree);
        treeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // ── Description ──────────────────────────────────────────────────
        txtDescription = new JTextArea(3, 20);
        txtDescription.setEditable(false);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(UIManager.getFont("Label.font"));
        txtDescription.setBackground(UIManager.getColor("Panel.background"));
        txtDescription.setBorder(BorderFactory.createTitledBorder("Description"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                treeScroll, new JScrollPane(txtDescription));
        split.setResizeWeight(0.70);
        split.setDividerSize(5);
        split.setContinuousLayout(true);
        return split;
    }

    /** Load defaults / Send defaults button row */
    private JPanel buildBottomBtns() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setBorder(new EmptyBorder(3, 2, 3, 2));

        btnLoadDefaults = new JButton("Load defaults");
        btnLoadDefaults.setToolTipText("Copy default AMHS fields for selected message to the Message Operations panel");
        btnLoadDefaults.addActionListener(e -> handleLoadDefaults());

        btnSendDefaults = new JButton("Send defaults");
        btnSendDefaults.setToolTipText("Load defaults and immediately send the message");
        btnSendDefaults.addActionListener(e -> handleSendDefaults());

        panel.add(btnLoadDefaults);
        panel.add(btnSendDefaults);
        return panel;
    }

    // ── Event handlers ────────────────────────────────────────────────────

    private void handleTestCaseChanged() {
        TestCase tc = (TestCase) cboTestCases.getSelectedItem();
        rootNode.removeAllChildren();

        if (tc != null) {
            // "default message" node (no linked subcase)
            rootNode.add(new DefaultMutableTreeNode(
                    new SubcaseNode("default message", null, true)));

            // One tree node per subcase
            if (tc.getSubcases() != null) {
                int idx = 1;
                for (TestSubcase sc : tc.getSubcases()) {
                    String label = "message " + idx + "  (" + sc.getId() + ")";
                    rootNode.add(new DefaultMutableTreeNode(
                            new SubcaseNode(label, sc, false)));
                    idx++;
                }
            }
        }

        treeModel.reload();
        for (int i = 0; i < subcaseTree.getRowCount(); i++) {
            subcaseTree.expandRow(i);
        }
        if (subcaseTree.getRowCount() > 0) {
            subcaseTree.setSelectionRow(0);
        }
        txtDescription.setText("");
    }

    private void handleTreeSelectionChanged() {
        SubcaseNode sn = getSelectedNode();
        if (sn == null) {
            txtDescription.setText("");
            return;
        }
        if (sn.isDefault) {
            txtDescription.setText(
                "Default message: saved and loaded from default configuration.\n" +
                "Follows the ICAO testbook baseline for the selected test case.");
        } else if (sn.subcase != null) {
            String desc = sn.subcase.getDescription();
            txtDescription.setText(desc != null ? desc : "(no description)");
        }
    }

    private void handleLoadDefaults() {
        TestSubcase sc = getSelectedSubcase();
        if (sc != null && !sc.getAmhsDefaults().isEmpty()) {
            defaultsLoadedListeners.values().forEach(Runnable::run);
        } else if (sc == null) {
            JOptionPane.showMessageDialog(this,
                "No subcase selected (select a numbered message, not 'default message').",
                "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No AMHS defaults configured for this subcase.",
                "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleSendDefaults() {
        TestSubcase sc = getSelectedSubcase();
        if (sc != null && !sc.getAmhsDefaults().isEmpty()) {
            sendDefaultsListeners.values().forEach(Runnable::run);
        } else if (sc == null) {
            JOptionPane.showMessageDialog(this,
                "No subcase selected (select a numbered message, not 'default message').",
                "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No AMHS defaults configured for this subcase.",
                "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── Data helpers ──────────────────────────────────────────────────────

    private void populateTestCases() {
        cboTestCases.removeAllItems();
        if (repository == null || repository.getTestCasesList() == null) return;
        for (TestCase tc : repository.getTestCasesList()) {
            cboTestCases.addItem(tc);
        }
        if (cboTestCases.getItemCount() > 0) {
            cboTestCases.setSelectedIndex(0);
            handleTestCaseChanged();
        }
    }

    private SubcaseNode getSelectedNode() {
        TreePath path = subcaseTree.getSelectionPath();
        if (path == null) return null;
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) path.getLastPathComponent();
        Object uo = node.getUserObject();
        return (uo instanceof SubcaseNode) ? (SubcaseNode) uo : null;
    }

    // ── Public API (used by AMHSMessageUI) ────────────────────────────────

    public TestCase getSelectedTestCase() {
        return (TestCase) cboTestCases.getSelectedItem();
    }

    /** Returns null when the "default message" node is selected */
    public TestSubcase getSelectedSubcase() {
        SubcaseNode sn = getSelectedNode();
        return (sn != null && !sn.isDefault) ? sn.subcase : null;
    }

    public Map<String, String> getSelectedSubcaseDefaults() {
        TestSubcase sc = getSelectedSubcase();
        return sc != null ? new HashMap<>(sc.getAmhsDefaults()) : new HashMap<>();
    }

    /** Refresh the tree after external repository changes */
    public void refresh() {
        populateTestCases();
        subcaseTree.repaint();
    }

    // Listener registration (same API as before)
    public void addDefaultsLoadedListener(String key, Runnable r) { defaultsLoadedListeners.put(key, r); }
    public void addCopyDefaultsListener(String key,   Runnable r) { copyDefaultsListeners.put(key, r); }
    public void addSendDefaultsListener(String key,   Runnable r) { sendDefaultsListeners.put(key, r); }

    // ── Inner classes ─────────────────────────────────────────────────────

    /** Data object held in each JTree node */
    static class SubcaseNode {
        final String       label;
        final TestSubcase  subcase;   // null for the "default message" node
        final boolean      isDefault;

        SubcaseNode(String label, TestSubcase subcase, boolean isDefault) {
            this.label     = label;
            this.subcase   = subcase;
            this.isDefault = isDefault;
        }

        @Override public String toString() { return label; }
    }

    /** Colour-codes tree nodes by result; italicises the default-message row */
    private static class SubcaseCellRenderer extends DefaultTreeCellRenderer {

        private static final Color PASS_FG = new Color(0,  120, 0);
        private static final Color FAIL_FG = new Color(170, 0,  0);

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(
                    tree, value, sel, expanded, leaf, row, hasFocus);
            setIcon(null);     // remove default folder/leaf icons

            if (value instanceof DefaultMutableTreeNode) {
                Object uo = ((DefaultMutableTreeNode) value).getUserObject();
                if (uo instanceof SubcaseNode) {
                    SubcaseNode sn = (SubcaseNode) uo;
                    if (sn.isDefault) {
                        setFont(getFont().deriveFont(Font.ITALIC));
                        if (!sel) setForeground(Color.GRAY);
                    } else if (sn.subcase != null) {
                        String result = sn.subcase.getResult();
                        if (!sel) {
                            if ("PASS".equals(result))      setForeground(PASS_FG);
                            else if ("FAIL".equals(result)) setForeground(FAIL_FG);
                        }
                    }
                }
            }
            return this;
        }
    }
}
