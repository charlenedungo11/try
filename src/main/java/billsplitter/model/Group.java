package billsplitter.model;

import java.io.Serializable;
import java.util.*;

public class Group implements Serializable {

    private String name;
    private String date;
    private boolean settled = false;

    private List<Member> members = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();

    public Group(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public void addMember(Member m) { members.add(m); }
    public void addExpense(Expense e) { expenses.add(e); }

    public String getName() { return name; }
    public String getDate() { return date; }

    public List<Member> getMembers() { return members; }
    public List<Expense> getExpenses() { return expenses; }

    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
}