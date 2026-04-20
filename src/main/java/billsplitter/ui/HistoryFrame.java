package billsplitter.ui;

import billsplitter.model.Group;
import billsplitter.model.Member;
import billsplitter.model.Expense;
import billsplitter.service.BillSplitterService;
import billsplitter.service.DataStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryFrame extends JFrame {

    private final Color DEEP_PINK = new Color(255, 105, 180);
    private final Color BG_PINK = new Color(255, 240, 245);
    private final Color HOVER_PINK = new Color(255, 182, 193);
    private final Color DARK_PINK_HOVER = new Color(255, 20, 147); // Darker pink for hover
    private final Color WHITE = Color.WHITE;
    private final Color TEXT_DARK = new Color(50, 50, 50);

    public HistoryFrame() { this(null); }

    public HistoryFrame(Rectangle bounds) {
        setTitle("Transaction History");
        setSize(420, 650); 
        if (bounds != null) setBounds(bounds);
        else setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_PINK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PINK);
        header.setPreferredSize(new Dimension(400, 60));

        JButton backBtn = new JButton("←");
        backBtn.setForeground(DEEP_PINK);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 22));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBack());
        header.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Settled Hangouts", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(titleLabel, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(50, 0)), BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_PINK);
        listPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        if (DataStore.history.isEmpty()) {
            JLabel emptyMsg = new JLabel("No history yet! 🏖️");
            emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalGlue());
            listPanel.add(emptyMsg);
            listPanel.add(Box.createVerticalGlue());
        } else {
            for (Group g : DataStore.history) {
                listPanel.add(createHistoryItem(g));
                listPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHistoryItem(Group g) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(WHITE);
        container.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
        container.setMaximumSize(new Dimension(360, 2000));

        JButton toggleBtn = new JButton("✔ " + g.getName().toUpperCase());
        toggleBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        toggleBtn.setForeground(TEXT_DARK);
        toggleBtn.setBackground(WHITE);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorder(new EmptyBorder(15, 10, 15, 10));
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(new Color(252, 252, 252));
        infoPanel.setVisible(false);
        infoPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 0, 3, 0);

        // Data Prep
        String membersList = g.getMembers().stream().map(Member::getName).collect(Collectors.joining(", "));
        double total = BillSplitterService.getTotalGroupSpent(g);

        // UI Rows
        infoPanel.add(createReceiptLabel("📅 Date: " + g.getDate()), gbc); gbc.gridy++;
        infoPanel.add(createWrappedText("👥 Members: " + membersList), gbc); gbc.gridy++;
        
        // Detailed Categories (Removed "Paid by")
        infoPanel.add(createReceiptLabel("📂 Categories:"), gbc); gbc.gridy++;
        for (Expense e : g.getExpenses()) {
            String line = "     " + e.getCategory() + ": ₱" + String.format("%.2f", e.getAmount());
            infoPanel.add(createReceiptLabel(line), gbc); gbc.gridy++;
        }

        infoPanel.add(new JSeparator(), gbc); gbc.gridy++;
        infoPanel.add(createReceiptLabel("💰 Total Spent: ₱" + String.format("%.2f", total)), gbc); gbc.gridy++;
        infoPanel.add(createReceiptLabel("👑 Top Spender: " + (BillSplitterService.getTopSpender(g) != null ? BillSplitterService.getTopSpender(g).getName() : "N/A")), gbc); gbc.gridy++;
        infoPanel.add(createReceiptLabel("🥗 Most Tipid: " + (BillSplitterService.getMostTipid(g) != null ? BillSplitterService.getMostTipid(g).getName() : "N/A")), gbc); gbc.gridy++;
        infoPanel.add(createReceiptLabel("💸 Next Suggestion: " + (BillSplitterService.suggestNextPayer(g) != null ? BillSplitterService.suggestNextPayer(g).getName() : "N/A")), gbc); gbc.gridy++;
        
        infoPanel.add(new JLabel("-------------------------"), gbc); gbc.gridy++;
        JLabel statusLbl = createReceiptLabel("STATUS: SETTLED");
        statusLbl.setFont(new Font("Monospaced", Font.BOLD, 14));
        infoPanel.add(statusLbl, gbc);

        // Hover Effect Logic
        toggleBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                toggleBtn.setBackground(DARK_PINK_HOVER);
                toggleBtn.setForeground(WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!infoPanel.isVisible()) {
                    toggleBtn.setBackground(WHITE);
                    toggleBtn.setForeground(TEXT_DARK);
                } else {
                    toggleBtn.setBackground(HOVER_PINK);
                    toggleBtn.setForeground(DEEP_PINK);
                }
            }
        });

        toggleBtn.addActionListener(e -> {
            infoPanel.setVisible(!infoPanel.isVisible());
            if (infoPanel.isVisible()) {
                toggleBtn.setForeground(DEEP_PINK);
                toggleBtn.setBackground(HOVER_PINK);
            } else {
                toggleBtn.setForeground(TEXT_DARK);
                toggleBtn.setBackground(WHITE);
            }
            revalidate(); repaint();
        });

        container.add(toggleBtn, BorderLayout.NORTH);
        container.add(infoPanel, BorderLayout.CENTER);
        return container;
    }

    private JLabel createReceiptLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Monospaced", Font.PLAIN, 13));
        return label;
    }

    private JTextArea createWrappedText(String text) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBackground(new Color(252, 252, 252));
        area.setFocusable(false);
        return area;
    }

    private void goBack() {
        new GroupListFrame().setVisible(true);
        this.dispose();
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
        //try {
          //  for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
              //  if ("Nimbus".equals(info.getName())) {
                //    javax.swing.UIManager.setLookAndFeel(info.getClassName());
               //     break;
               // }
          //  }
      //  } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
       //     logger.log(java.util.logging.Level.SEVERE, null, ex);
      //  }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new HistoryFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
