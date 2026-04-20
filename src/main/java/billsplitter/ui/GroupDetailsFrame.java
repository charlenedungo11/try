package billsplitter.ui;

import billsplitter.model.*;
import billsplitter.service.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class GroupDetailsFrame extends JFrame {

    private Group group;
    private JTextArea resultArea;
    private JTextField amountField;
    private JComboBox<Member> paidByBox;
    private JComboBox<String> categoryBox;
    private ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
    
    // Aesthetic Color Palette
    private final Color DEEP_PINK = new Color(255, 105, 180);
    private final Color LIGHT_PINK = new Color(255, 228, 235);
    private final Color TEXT_PINK = new Color(255, 20, 147);
    private final Color WHITE = Color.WHITE;

    public GroupDetailsFrame(Group group, Rectangle bounds) {
        this.group = group;
        setTitle("Group Details");
        setSize(400, 750);
        if (bounds != null) setBounds(bounds); // Keep same window position
        else setLocationRelativeTo(null);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Same window app behavior
        setLayout(new BorderLayout());
        getContentPane().setBackground(WHITE);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DEEP_PINK);
        header.setPreferredSize(new Dimension(400, 60));

        JButton backBtn = new JButton("←");
        backBtn.setForeground(WHITE);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> goBack());
        header.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(group.getName().toUpperCase(), SwingConstants.CENTER);
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(titleLabel, BorderLayout.CENTER);
        
        // Spacer for symmetry
        header.add(Box.createRigidArea(new Dimension(50, 0)), BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Card 1: Amount & Payer
        amountField = new JTextField();
        setupNumericValidation(amountField);
        amountField.setHorizontalAlignment(JTextField.CENTER);
        amountField.setBorder(new LineBorder(DEEP_PINK, 1)); // Added border as requested

        paidByBox = new JComboBox<>(group.getMembers().toArray(new Member[0]));
        ((JLabel)paidByBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        contentPanel.add(createInputCard("Expense Info", 
                createCenteredLabel("Amount:"), amountField,
                createCenteredLabel("Paid By:"), paidByBox
        ));
        contentPanel.add(Box.createVerticalStrut(15));

        // Card 2: Category & Participants
        categoryBox = new JComboBox<>(new String[]{"Food", "Transport", "Accommodation", "Others"});
        ((JLabel)categoryBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        contentPanel.add(createInputCard("Details", 
                createCenteredLabel("Category:"), categoryBox,
                createCenteredLabel("Who will contribute?"), createParticipantsPanel()
        ));
        contentPanel.add(Box.createVerticalStrut(15));

        // --- BUTTON GRID ---
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        btnPanel.setBackground(WHITE);
        btnPanel.setMaximumSize(new Dimension(360, 150));
        
        JButton addBtn = createActionBtn("Add Expense");
        JButton calcBtn = createActionBtn("Calculate");
        JButton suggestBtn = createActionBtn("Suggest Payer");
        JButton statsBtn = createActionBtn("Show Stats");
        JButton chartBtn = createActionBtn("Show Chart");
        JButton settleBtn = createActionBtn("Settle Group");

        btnPanel.add(addBtn); btnPanel.add(calcBtn);
        btnPanel.add(suggestBtn); btnPanel.add(statsBtn);
        btnPanel.add(chartBtn); btnPanel.add(settleBtn);
        contentPanel.add(btnPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Results Area (Improved look)
        resultArea = new JTextArea(6, 20);
        resultArea.setEditable(false);
        resultArea.setBackground(LIGHT_PINK);
        resultArea.setFont(new Font("SansSerif", Font.BOLD, 12));
        resultArea.setMargin(new Insets(10, 15, 10, 15)); // Margin for text
        
        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBorder(new LineBorder(DEEP_PINK, 1));
        contentPanel.add(scroll);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        // Logic
        addBtn.addActionListener(e -> addExpenseLogic());
        calcBtn.addActionListener(e -> calculate());
        suggestBtn.addActionListener(e -> suggestPayer());
        statsBtn.addActionListener(e -> showStats());
        chartBtn.addActionListener(e -> showChart());
        settleBtn.addActionListener(e -> settleLogic());
    }

    // --- LOGIC METHODS ---

    private void addExpenseLogic() {
        try {
            String text = amountField.getText();
            double amount = Double.parseDouble(text);
            if (amount <= 0) throw new Exception();

            Member payer = (Member) paidByBox.getSelectedItem();
            ArrayList<Member> parts = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) parts.add(group.getMembers().get(i));
            }
            
            if (parts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select who will contribute!");
                return;
            }

            group.addExpense(new Expense(amount, payer, parts, (String)categoryBox.getSelectedItem()));
            payer.addPayment(amount); // Logic from version 2
            
            JOptionPane.showMessageDialog(this, "Expense Added!");
            amountField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount greater than 0");
        }
    }

    private void calculate() {
        resultArea.setText("CALCULATED DEBTS:\n\n");
        Map<Member, Double> balances = BillSplitterService.calculateBalances(group);
        for (String s : BillSplitterService.simplifyDebts(balances)) {
            resultArea.append(" • " + s + "\n");
        }
    }

    private void suggestPayer() {
        Member m = BillSplitterService.suggestNextPayer(group);
        JOptionPane.showMessageDialog(this, "Next should pay: " + m.getName());
    }

    private void showStats() {
        Member top = BillSplitterService.getTopSpender(group);
        JOptionPane.showMessageDialog(this, "🤑 Top Spender: " + (top != null ? top.getName() : "None"));
    }

private void showChart() {
    DefaultPieDataset dataset = new DefaultPieDataset();
    Map<String, Double> totals = new HashMap<>();
    for (Expense e : group.getExpenses()) {
        totals.put(e.getCategory(), totals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
    }
    for (String key : totals.keySet()) dataset.setValue(key, totals.get(key));

    // Create the chart without a border/frame
    JFreeChart chart = ChartFactory.createPieChart("Expense Breakdown", dataset, true, true, false);
    chart.setBackgroundPaint(WHITE);
    
    // Wrap the chart in a Panel
    org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart);
    chartPanel.setBackground(WHITE);

    // Create a temporary container to hold the chart and a "Close" button
    JPanel chartView = new JPanel(new BorderLayout());
    chartView.setBackground(WHITE);
    
    JButton closeChartBtn = createActionBtn("CLOSE CHART");
    closeChartBtn.setPreferredSize(new Dimension(400, 50));
    
    // When clicked, it restores the original view
    closeChartBtn.addActionListener(e -> {
        this.getContentPane().removeAll();
        // Re-initialize the frame components (or simply refresh the frame)
        new GroupDetailsFrame(group, this.getBounds()).setVisible(true);
        this.dispose();
    });

    chartView.add(chartPanel, BorderLayout.CENTER);
    chartView.add(closeChartBtn, BorderLayout.SOUTH);

    // SWAP THE CONTENT: This is what makes it feel like an app transition
    this.getContentPane().removeAll();
    this.getContentPane().add(chartView);
    this.revalidate();
    this.repaint();
}
    private void settleLogic() {
        int confirm = JOptionPane.showConfirmDialog(this, "Settle and move to history?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            group.setSettled(true);
            DataStore.history.add(group);
            DataStore.groups.remove(group);
            DataStore.saveData();
            goBack();
        }
    }

    private void goBack() {
        GroupListFrame listFrame = new GroupListFrame();
        listFrame.setBounds(this.getBounds());
        listFrame.setVisible(true);
        this.dispose();
    }

    // --- UI HELPERS ---

    private JPanel createInputCard(String title, Component... components) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(LIGHT_PINK);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 192, 203), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel head = new JLabel(title);
        head.setFont(new Font("SansSerif", Font.BOLD, 14));
        head.setForeground(TEXT_PINK);
        head.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(head);
        card.add(Box.createVerticalStrut(10));

        for (Component c : components) {
            if (c instanceof JComponent) {
                ((JComponent)c).setAlignmentX(Component.CENTER_ALIGNMENT);
                if (c instanceof JTextField || c instanceof JComboBox) {
                    c.setMaximumSize(new Dimension(280, 35));
                }
            }
            card.add(c);
            card.add(Box.createVerticalStrut(5));
        }
        return card;
    }

    private JPanel createParticipantsPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 2)); // Compact grid
        p.setBackground(LIGHT_PINK);
        for (Member m : group.getMembers()) {
            JCheckBox cb = new JCheckBox(m.getName());
            cb.setBackground(LIGHT_PINK);
            cb.setFont(new Font("SansSerif", Font.PLAIN, 11));
            checkBoxes.add(cb);
            p.add(cb); // Aligned to left of their grid cell naturally
        }
        p.setMaximumSize(new Dimension(280, 100));
        return p;
    }

    private JLabel createCenteredLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JButton createActionBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(DEEP_PINK);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(TEXT_PINK); }
            public void mouseExited(MouseEvent e) { b.setBackground(DEEP_PINK); }
        });
        return b;
    }

    private void setupNumericValidation(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nextText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (nextText.matches("^\\d*\\.?\\d*$")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
       // try {
          //  for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
               // if ("Nimbus".equals(info.getName())) {
                 //   javax.swing.UIManager.setLookAndFeel(info.getClassName());
                //    break;
               // }
           // }
       // } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
//            logger.log(java.util.logging.Level.SEVERE, null, ex);
      //  }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new HomeFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
