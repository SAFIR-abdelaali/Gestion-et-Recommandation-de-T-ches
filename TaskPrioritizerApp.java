import java.time.LocalDate;
import java.util.*;

public class TaskPrioritizerApp {
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        PriorityCalculator calculator = new PriorityCalculator();
        TaskFileManager fileManager = new TaskFileManager();

        List<WorkItem> loadedItems = fileManager.loadWorkItems();
        Map<String, WorkItem> itemRegistry = new HashMap<>();
        for (WorkItem item : loadedItems) {
            itemRegistry.put(item.title.toLowerCase(), item);
        }

        boolean applicationRunning = true;

        while (applicationRunning) {
            System.out.println("\n=== Work Item Manager ===");
            System.out.println("1. Create New Work Item");
            System.out.println("2. Display Prioritized Items");
            System.out.println("3. Update Existing Item");
            System.out.println("4. Remove Work Item");
            System.out.println("5. Exit Application");
            System.out.print("Select option: ");
            String userSelection = inputScanner.nextLine();

            switch (userSelection) {
                case "1":
                    System.out.print("Work item title: ");
                    String itemTitle = inputScanner.nextLine();

                    System.out.print("Time-sensitive (true/false): ");
                    boolean timeSensitive = Boolean.parseBoolean(inputScanner.nextLine());

                    System.out.print("High-value (true/false): ");
                    boolean highValue = Boolean.parseBoolean(inputScanner.nextLine());

                    System.out.print("Target date (yyyy-mm-dd or leave empty): ");
                    String dateInput = inputScanner.nextLine();
                    LocalDate targetDate = null;
                    if (!dateInput.isEmpty()) targetDate = LocalDate.parse(dateInput);

                    System.out.print("Estimated duration (minutes): ");
                    int duration = Integer.parseInt(inputScanner.nextLine());

                    boolean duplicateFound = false;
                    for (WorkItem existing : itemRegistry.values()) {
                        if (existing.timeSensitive == timeSensitive &&
                                existing.highValue == highValue &&
                                Objects.equals(existing.targetDate, targetDate) &&
                                existing.duration == duration) {
                            duplicateFound = true;
                            break;
                        }
                    }

                    if (duplicateFound) {
                        System.out.println("Similar work item already exists!");
                        break;
                    }

                    WorkItem newItem = new WorkItem(timeSensitive, highValue, targetDate, duration);
                    newItem.title = itemTitle;
                    itemRegistry.put(itemTitle.toLowerCase(), newItem);
                    fileManager.saveWorkItems(itemRegistry.values());
                    System.out.println("Work item successfully created!");
                    break;

                case "2":
                    if (itemRegistry.isEmpty()) {
                        System.out.println("No work items available.");
                        break;
                    }
                    List<WorkItem> prioritized = calculator.prioritizeItems(itemRegistry.values());
                    System.out.println("\n=== Prioritized Work Items ===");
                    for (WorkItem item : prioritized) {
                        double priorityScore = calculator.calculatePriorityScore(item);
                        String dateDisplay = (item.targetDate != null) ? item.targetDate.toString() : "No date";
                        System.out.printf(
                                "Title: %-25s | Urgent: %-5b | Important: %-5b | Due: %-12s | Duration: %-3d min | Priority: %.2f%n",
                                item.title, item.timeSensitive, item.highValue, dateDisplay, item.duration, priorityScore);
                    }
                    break;

                case "3":
                    System.out.print("Enter item title to modify: ");
                    String modifyTitle = inputScanner.nextLine().toLowerCase();
                    if (!itemRegistry.containsKey(modifyTitle)) {
                        System.out.println("Item not found!");
                        break;
                    }
                    WorkItem itemToUpdate = itemRegistry.get(modifyTitle);

                    System.out.print("New time-sensitive status (true/false, current: " + itemToUpdate.timeSensitive + "): ");
                    itemToUpdate.timeSensitive = Boolean.parseBoolean(inputScanner.nextLine());

                    System.out.print("New high-value status (true/false, current: " + itemToUpdate.highValue + "): ");
                    itemToUpdate.highValue = Boolean.parseBoolean(inputScanner.nextLine());

                    System.out.print("New target date (yyyy-mm-dd or empty, current: " +
                            (itemToUpdate.targetDate != null ? itemToUpdate.targetDate : "None") + "): ");
                    String updatedDeadline = inputScanner.nextLine();
                    itemToUpdate.targetDate = updatedDeadline.isEmpty() ? null : LocalDate.parse(updatedDeadline);

                    System.out.print("New duration (minutes, current: " + itemToUpdate.duration + "): ");
                    itemToUpdate.duration = Integer.parseInt(inputScanner.nextLine());

                    fileManager.saveWorkItems(itemRegistry.values());
                    System.out.println("Item updated successfully!");
                    break;

                case "4":
                    System.out.print("Enter item title to remove: ");
                    String removeTitle = inputScanner.nextLine().toLowerCase();
                    if (itemRegistry.remove(removeTitle) != null) {
                        fileManager.saveWorkItems(itemRegistry.values());
                        System.out.println("Item removed successfully!");
                    } else {
                        System.out.println("Item not found!");
                    }
                    break;

                case "5":
                    applicationRunning = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        }

        inputScanner.close();
    }
}