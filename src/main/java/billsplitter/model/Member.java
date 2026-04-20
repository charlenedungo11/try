package billsplitter.model;

public class Member {

    private String name;
    private double totalPaid;

    // 🔹 Constructor
    public Member(String name) {
        this.name = name;
        this.totalPaid = 0.0;
    }

    // 🔹 Get name
    public String getName() {
        return name;
    }

    // 🔹 Get total paid
    public double getTotalPaid() {
        return totalPaid;
    }

    // 🔹 Add payment
    public void addPayment(double amount) {
        totalPaid += amount;
    }

    // 🔹 For JComboBox display (VERY IMPORTANT)
    @Override
    public String toString() {
        return name;
    }
}