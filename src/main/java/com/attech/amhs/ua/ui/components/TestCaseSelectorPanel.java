package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import com.attech.amhs.ua.repository.TestCaseRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Left-column panel for selecting CTSW test cases and their messages.
 * 
 * Directory Model Display:
 *   ┌─ CASE: [CTSW001 (001 to 020)] ────────────────┐
 *   │  |------ Message/Subcase 1                    │
 *   │  |------ Message/Subcase 2                    │
 *   │  |------ ...                                  │
 *   │  |------ Message/Subcase n                    │
 *   └───────────────────────────────────────────────┘
 * 
 * Description is now displayed in the ActionLogsPanel (bottom panel).
 * Added "Send All Subcases" button to send messages under all subcases for a case.
 */
public class TestCaseSelectorPanel extends JPanel {

    private final TestCaseRepository repository;

    // Top combo - shows case ID with range info
    private JComboBox<TestCase> cboTestCases;

    // Tree - displays directory-style structure
    private JTree subcaseTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    // Buttons
    private JButton btnLoadDefaults;
    private JButton btnSendDefaults;
    private JButton btnSendAllSubcases;  // New button for sending all subcases

    // Listener maps
    private final Map<String, Runnable> defaultsLoadedListeners = new HashMap<>();
    private final Map<String, Runnable> copyDefaultsListeners   = new HashMap<>();
    private final Map<String, Runnable> sendDefaultsListeners   = new HashMap<>();
    private final Map<String, Runnable> sendAllSubcasesListeners = new HashMap<>();

    public TestCaseSelectorPanel(TestCaseRepository repository) {
        this.repository = repository;
        initUI();
    }

    // ── UI construction ───────────────────────────────────────────────────

    private void initUI() {
        setBorder(new TitledBorder("Test Case Directory"));
        setLayout(new BorderLayout(4, 4));

        add(buildTopCombo(),   BorderLayout.NORTH);
        add(buildCenterTree(), BorderLayout.CENTER);
        add(buildBottomBtns(), BorderLayout.SOUTH);

        populateTestCases();
    }

    /** CTSW case selector row with directory-style naming */
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

    /** Tree with directory-style display */
    private JScrollPane buildCenterTree() {
        // ── Tree ─────────────────────────────────────────────────────────
        rootNode  = new DefaultMutableTreeNode("root");
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
        treeScroll.setBorder(BorderFactory.createTitledBorder("Messages/Subcases"));

        return treeScroll;
    }

    /** Load defaults / Send defaults / Send All Subcases button row */
    private JPanel buildBottomBtns() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 5, 0));
        panel.setBorder(new EmptyBorder(3, 2, 3, 2));

        btnLoadDefaults = new JButton("Load defaults");
        btnLoadDefaults.setToolTipText("Copy default AMHS fields for selected message to the Message Operations panel");
        btnLoadDefaults.addActionListener(e -> handleLoadDefaults());

        btnSendDefaults = new JButton("Send defaults");
        btnSendDefaults.setToolTipText("Load defaults and immediately send the message");
        btnSendDefaults.addActionListener(e -> handleSendDefaults());

        btnSendAllSubcases = new JButton("Send All Subcases");
        btnSendAllSubcases.setToolTipText("Send messages for ALL subcases under the selected test case");
        btnSendAllSubcases.addActionListener(e -> handleSendAllSubcases());

        panel.add(btnLoadDefaults);
        panel.add(btnSendDefaults);
        panel.add(btnSendAllSubcases);
        return panel;
    }

    // ── Event handlers ────────────────────────────────────────────────────

    private void handleTestCaseChanged() {
        TestCase tc = (TestCase) cboTestCases.getSelectedItem();
        rootNode.removeAllChildren();

        if (tc != null) {
            // Create directory-style nodes for each subcase
            if (tc.getSubcases() != null && !tc.getSubcases().isEmpty()) {
                int idx = 1;
                for (TestSubcase sc : tc.getSubcases()) {
                    // Format: "|------ Message/Subcase y"
                    String label = "|------ Message/Subcase " + idx + " (" + sc.getId() + ")";
                    rootNode.add(new DefaultMutableTreeNode(
                            new SubcaseNode(label, sc, false)));
                    idx++;
                }
            } else {
                // Add a placeholder if no subcases
                rootNode.add(new DefaultMutableTreeNode(
                        new SubcaseNode("|------ No subcases defined", null, true)));
            }
        }

        treeModel.reload();
        
        // Expand all nodes
        for (int i = 0; i < subcaseTree.getRowCount(); i++) {
            subcaseTree.expandRow(i);
        }
        
        // Select first node if available
        if (subcaseTree.getRowCount() > 0) {
            subcaseTree.setSelectionRow(0);
        }
        
        // Display test case description in the ActionLogsPanel
        displayTestCaseDescription(tc);
    }

    private void handleTreeSelectionChanged() {
        SubcaseNode sn = getSelectedNode();
        if (sn == null) {
            return;
        }
        if (sn.isDefault) {
            // Placeholder node - clear description
            return;
        } else if (sn.subcase != null) {
            // Display subcase description with expectations in the ActionLogsPanel
            displaySubcaseDescription(sn.subcase);
            
            if (!sn.subcase.getAmhsDefaults().isEmpty()) {
                defaultsLoadedListeners.values().forEach(Runnable::run);
            }
        }
    }

    private void handleLoadDefaults() {
        SubcaseNode sn = getSelectedNode();
        TestSubcase sc = getSelectedSubcase();
        if (sn != null && sn.isDefault) {
            JOptionPane.showMessageDialog(this,
                "No valid subcase selected.",
                "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (sc != null && !sc.getAmhsDefaults().isEmpty()) {
            defaultsLoadedListeners.values().forEach(Runnable::run);
        } else if (sn == null) {
            JOptionPane.showMessageDialog(this,
                "No message selected.",
                "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No AMHS defaults configured for this subcase.",
                "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleSendDefaults() {
        SubcaseNode sn = getSelectedNode();
        TestSubcase sc = getSelectedSubcase();
        if (sn != null && sn.isDefault) {
            JOptionPane.showMessageDialog(this,
                "No valid subcase selected.",
                "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (sc != null && !sc.getAmhsDefaults().isEmpty()) {
            sendDefaultsListeners.values().forEach(Runnable::run);
        } else if (sn == null) {
            JOptionPane.showMessageDialog(this,
                "No message selected.",
                "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No AMHS defaults configured for this subcase.",
                "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleSendAllSubcases() {
        TestCase tc = getSelectedTestCase();
        if (tc == null) {
            JOptionPane.showMessageDialog(this,
                "No test case selected.",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (tc.getSubcases() == null || tc.getSubcases().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No subcases defined for this test case.",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Trigger the send all subcases listener
        sendAllSubcasesListeners.values().forEach(Runnable::run);
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

    /** Returns null when no valid subcase is selected */
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
    
    /** Set the action logs panel reference for displaying descriptions */
    public void setActionLogsPanel(ActionLogsPanel panel) {
        // Store reference if needed for direct updates
    }

    // Listener registration
    public void addDefaultsLoadedListener(String key, Runnable r) { defaultsLoadedListeners.put(key, r); }
    public void addCopyDefaultsListener(String key,   Runnable r) { copyDefaultsListeners.put(key, r); }
    public void addSendDefaultsListener(String key,   Runnable r) { sendDefaultsListeners.put(key, r); }
    public void addSendAllSubcasesListener(String key, Runnable r) { sendAllSubcasesListeners.put(key, r); }

    // ── Inner classes ─────────────────────────────────────────────────────

    /** Data object held in each JTree node */
    static class SubcaseNode {
        final String       label;
        final TestSubcase  subcase;   // null for placeholder nodes
        final boolean      isDefault;

        SubcaseNode(String label, TestSubcase subcase, boolean isDefault) {
            this.label     = label;
            this.subcase   = subcase;
            this.isDefault = isDefault;
        }

        @Override public String toString() { return label; }
    }

    /** Colour-codes tree nodes by result */
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
    
    // ── Helper methods for displaying descriptions ────────────────────────
    
    /**
     * Display test case description in the parent UI's action logs panel
     */
    private void displayTestCaseDescription(TestCase tc) {
        // This will be called by the parent UI to display in ActionLogsPanel
        // The actual display is handled by AMHSMessageUI
    }
    
    /**
     * Display subcase description in the parent UI's action logs panel
     */
    private void displaySubcaseDescription(TestSubcase sc) {
        // This will be called by the parent UI to display in ActionLogsPanel
        // The actual display is handled by AMHSMessageUI
    }
}
