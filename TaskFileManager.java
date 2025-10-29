import java.io.*;
import java.util.*;

public class TaskFileManager {

    private String storageFile;

    public TaskFileManager() {
        this.storageFile = "work_items.dat";
    }

    public void saveWorkItems(Collection<WorkItem> items) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(storageFile))) {
            outputStream.writeObject(new ArrayList<>(items));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<WorkItem> loadWorkItems() {
        File dataFile = new File(storageFile);
        if (!dataFile.exists()) return new ArrayList<>();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(storageFile))) {
            return (List<WorkItem>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}