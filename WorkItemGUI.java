import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;

public class WorkItemGUI extends JFrame {
    private TaskFileManager fileManager = new TaskFileManager();
    private PriorityCalculator calculator = new PriorityCalculator();
    private Map<String, WorkItem> itemRegistry = new HashMap<>();
    private DefaultTableModel tableModel = new DefaultTableModel();

    public WorkItemGUI() {
        setTitle("Work Item Prioritization System");
        setSize(900, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        java.util.List<WorkItem> items = fileManager.loadWorkItems();
        for (WorkItem item : items) itemRegistry.put(item.title.toLowerCase(), item);

        String[] columnHeaders = {"Item Title", "Time-Sensitive", "High-Value", "Target Date", "Duration", "Priority Score"};
        tableModel.setColumnIdentifiers(columnHeaders);
        JTable dataTable = new JTable(tableModel);
        JScrollPane scrollPanel = new JScrollPane(dataTable);
        add(scrollPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton createButton = new JButton("Create New");
        JButton refreshButton = new JButton("Refresh View");
        controlPanel.add(createButton);
        controlPanel.add(refreshButton);
        add(controlPanel, BorderLayout.SOUTH);

        createButton.addActionListener(e -> createNewItem());

        refreshButton.addActionListener(e -> refreshTableDisplay());

        refreshTableDisplay();
    }

    private void createNewItem() {
        JTextField titleField = new JTextField();
        JCheckBox urgentCheck = new JCheckBox();
        JCheckBox importantCheck = new JCheckBox();
        JTextField dateField = new JTextField();
        JTextField timeField = new JTextField();

        Object[] inputFields = {
                "Item Title:", titleField,
                "Time-Sensitive:", urgentCheck,
                "High-Value:", importantCheck,
                "Target Date (yyyy-mm-dd):", dateField,
                "Duration (minutes):", timeField
        };

        int userChoice = JOptionPane.showConfirmDialog(this, inputFields, "Create Work Item", JOptionPane.OK_CANCEL_OPTION);
        if (userChoice == JOptionPane.OK_OPTION) {
            String itemTitle = titleField.getText().trim();
            if (itemTitle.isEmpty()) return;
            if (itemRegistry.containsKey(itemTitle.toLowerCase())) {
                JOptionPane.showMessageDialog(this, "Item already exists!");
                return;
            }

            boolean isUrgent = urgentCheck.isSelected();
            boolean isImportant = importantCheck.isSelected();
            LocalDate dueDate = null;
            if (!dateField.getText().trim().isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dateField.getText().trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format!");
                    return;
                }
            }
            int itemDuration;
            try {
                itemDuration = Integer.parseInt(timeField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid duration value!");
                return;
            }

            WorkItem newItem = new WorkItem(isUrgent, isImportant, dueDate, itemDuration);
            newItem.title = itemTitle;
            itemRegistry.put(itemTitle.toLowerCase(), newItem);
            fileManager.saveWorkItems(itemRegistry.values());
            refreshTableDisplay();
        }
    }

    private void refreshTableDisplay() {
        tableModel.setRowCount(0);
        java.util.List<WorkItem> prioritized = calculator.prioritizeItems(itemRegistry.values());
        for (WorkItem item : prioritized) {
            double score = calculator.calculatePriorityScore(item);
            String dateText = (item.targetDate != null) ? item.targetDate.toString() : "Not set";
            tableModel.addRow(new Object[]{item.title, item.timeSensitive, item.highValue, dateText, item.duration, score});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WorkItemGUI().setVisible(true));
    }
}