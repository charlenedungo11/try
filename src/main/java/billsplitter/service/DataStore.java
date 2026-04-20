package billsplitter.service;

import billsplitter.model.Group;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataStore {

    public static List<Group> groups = new ArrayList<>();
    public static List<Group> history = new ArrayList<>();

    public static void saveData() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("data.dat"));
            out.writeObject(groups);
            out.writeObject(history);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadData() {
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream("data.dat"));
            groups = (List<Group>) in.readObject();
            history = (List<Group>) in.readObject();
            in.close();
        } catch (Exception e) {
            // first run → no file yet
        }
    }
}