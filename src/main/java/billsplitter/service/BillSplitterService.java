/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package billsplitter.service;

import billsplitter.model.Group;
import billsplitter.model.Member;
import billsplitter.model.Expense;

import java.util.*;
import java.util.stream.Collectors;

public class BillSplitterService {

    /**
     * Calculates the total amount spent by the entire group.
     */
    public static double getTotalGroupSpent(Group group) {
        return group.getExpenses().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Identifies the "Most Tipid" member (the one who has paid the least amount).
     */
    public static Member getMostTipid(Group group) {
        return group.getMembers().stream()
                .min(Comparator.comparing(Member::getTotalPaid))
                .orElse(null);
    }

    /**
     * Identifies the member who has paid the most out of pocket.
     */
    public static Member getTopSpender(Group group) {
        return group.getMembers().stream()
                .max(Comparator.comparing(Member::getTotalPaid))
                .orElse(null);
    }

    public static Map<String, Double> getCategoryTotals(Group g) {
    return g.getExpenses().stream()
            .collect(Collectors.groupingBy(
                    Expense::getCategory, 
                    Collectors.summingDouble(Expense::getAmount)
            ));
}
    
    public static Map<Member, Double> calculateBalances(Group group) {
        Map<Member, Double> balance = new HashMap<>();

        // Initialize balances
        for (Member m : group.getMembers()) {
            balance.put(m, 0.0);
        }

        // Process expenses
        for (Expense e : group.getExpenses()) {
            double share = e.getAmount() / e.getParticipants().size();

            for (Member m : e.getParticipants()) {
                balance.put(m, balance.get(m) - share);
            }

            Member payer = e.getPaidBy();
            balance.put(payer, balance.get(payer) + e.getAmount());
        }

        return balance;
    }

    public static Member suggestNextPayer(Group group) {
        // Suggests the person who has paid the least to even things out
        return getMostTipid(group);
    }

    public static List<String> simplifyDebts(Map<Member, Double> balance) {
        List<String> result = new ArrayList<>();

        // We create a copy of the balances to avoid mutating the original map
        List<Map.Entry<Member, Double>> debtors = new ArrayList<>();
        List<Map.Entry<Member, Double>> creditors = new ArrayList<>();

        for (Map.Entry<Member, Double> entry : balance.entrySet()) {
            if (entry.getValue() < -0.01) // Small threshold for floating point errors
                debtors.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            else if (entry.getValue() > 0.01)
                creditors.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
        }

        int dIdx = 0;
        int cIdx = 0;

        while (dIdx < debtors.size() && cIdx < creditors.size()) {
            Map.Entry<Member, Double> d = debtors.get(dIdx);
            Map.Entry<Member, Double> c = creditors.get(cIdx);

            double amount = Math.min(-d.getValue(), c.getValue());

            if (amount > 0) {
                result.add(String.format("%s pays %s ₱%.2f", 
                    d.getKey().getName(), c.getKey().getName(), amount));

                d.setValue(d.getValue() + amount);
                c.setValue(c.getValue() - amount);
            }

            if (Math.abs(d.getValue()) < 0.01) dIdx++;
            if (Math.abs(c.getValue()) < 0.01) cIdx++;
        }

        return result;
    }
}