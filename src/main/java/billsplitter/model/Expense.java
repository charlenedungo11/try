package billsplitter.model;



import java.util.List;

public class Expense {

    private double amount;
    private Member paidBy;
    private List<Member> participants;
    private String category;

    // 🔹 Constructor
    public Expense(double amount, Member paidBy, List<Member> participants, String category) {
        this.amount = amount;
        this.paidBy = paidBy;
        this.participants = participants;
        this.category = category;
    }

    // 🔹 Get amount
    public double getAmount() {
        return amount;
    }

    // 🔹 Get payer
    public Member getPaidBy() {
        return paidBy;
    }

    // 🔹 Get participants
    public List<Member> getParticipants() {
        return participants;
    }

    // 🔹 Get category
    public String getCategory() {
        return category;
    }
}