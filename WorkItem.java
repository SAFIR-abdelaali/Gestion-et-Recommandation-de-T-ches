import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class WorkItem implements Serializable {
    private static final long serialVersionUID = 2L;

    public String title;
    public boolean timeSensitive;
    public boolean highValue;
    public LocalDate targetDate;
    public int duration;

    public WorkItem(boolean urgent, boolean important, LocalDate dueDate, int timeRequired) {
        this.timeSensitive = urgent;
        this.highValue = important;
        this.targetDate = dueDate;
        this.duration = timeRequired;
    }
}

class PriorityCalculator {

    public double calculatePriorityScore(WorkItem item) {
        double priorityValue = 0.0;

        if (item.timeSensitive && item.highValue) priorityValue += 60;
        else if (item.highValue) priorityValue += 30;
        else if (item.timeSensitive) priorityValue += 20;

        if (item.targetDate != null) {
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), item.targetDate);
            if (daysRemaining < 0) priorityValue += 40;
            else if (daysRemaining <= 1) priorityValue += 35;
            else if (daysRemaining <= 3) priorityValue += 15;
            else if (daysRemaining <= 7) priorityValue += 5;
        }

        if (item.duration > 0 && item.duration <= 30) priorityValue += 10;

        return priorityValue;
    }

    public List<WorkItem> prioritizeItems(Collection<WorkItem> items) {
        List<ItemPriority> scoredItems = new ArrayList<>();
        for (WorkItem item : items) {
            double calculatedScore = calculatePriorityScore(item);
            scoredItems.add(new ItemPriority(item, calculatedScore));
        }

        scoredItems.sort((first, second) -> {
            int scoreComparison = Double.compare(second.priorityScore, first.priorityScore);
            if (scoreComparison != 0) return scoreComparison;
            LocalDate firstDate = first.workItem.targetDate, secondDate = second.workItem.targetDate;
            if (firstDate == null && secondDate == null) return 0;
            if (firstDate == null) return 1;
            if (secondDate == null) return -1;
            return Long.compare(ChronoUnit.DAYS.between(LocalDate.now(), firstDate),
                    ChronoUnit.DAYS.between(LocalDate.now(), secondDate));
        });

        List<WorkItem> resultList = new ArrayList<>();
        for (ItemPriority priority : scoredItems) resultList.add(priority.workItem);
        return resultList;
    }

    private static class ItemPriority {
        WorkItem workItem;
        double priorityScore;
        ItemPriority(WorkItem item, double score) { this.workItem = item; this.priorityScore = score; }
    }
}