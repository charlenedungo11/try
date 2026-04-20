package billsplitter.ui;

import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {
        

    Color BG = new Color(255, 240, 245);
    Color PRIMARY = new Color(255, 105, 180);
    Color SECONDARY = new Color(255, 182, 193);
    Color CARD = new Color(255, 228, 235);
    public HomeFrame() {
        
      setTitle("Expense Tracker");
        setSize(400, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel();
        root.setBackground(BG);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        // ================= HEADER =================
        JPanel headerCard = createCardPanel();
        JLabel title = new JLabel("Group Expense Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

     
        JLabel subtitle = new JLabel("Split bills. Track trips. Stay transparent 💸");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(150, 80, 120));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerCard.add(title);
        headerCard.add(Box.createVerticalStrut(5));
        headerCard.add(subtitle);
        
         // ================= FEATURE CARDS =================
        JPanel feature1 = createFeatureCard("💸", "Group Bill Splitting",
                "Automatically calculates who owes what");

        JPanel feature2 = createFeatureCard("📊", "Real-time Status",
                "See who is Paid or Pending instantly");

        JPanel feature3 = createFeatureCard("🗓️", "Adventure Logging",
                "Track trip start dates");

        JPanel feature4 = createFeatureCard("🔒", "Transparency",
                "View full history of transactions");

        // ================= START BUTTON =================
        JButton start = new JButton("Start");
        start.setFont(new Font("Segoe UI", Font.BOLD, 14));
        start.setBackground(SECONDARY);
        start.setForeground(Color.WHITE);
        start.setFocusPainted(false);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        start.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        start.addActionListener(e -> {
            new GroupListFrame().setVisible(true);
            dispose();
     });
        
        
         // ================= LAYOUT =================
        root.add(Box.createVerticalStrut(20));
        root.add(headerCard);
        root.add(Box.createVerticalStrut(15));

        root.add(feature1);
        root.add(Box.createVerticalStrut(10));

        root.add(feature2);
        root.add(Box.createVerticalStrut(10));

        root.add(feature3);
        root.add(Box.createVerticalStrut(10));
        
        root.add(feature4);
        root.add(Box.createVerticalStrut(25));

        root.add(start);
        root.add(Box.createVerticalStrut(30));

        add(root);
    }
    
    // ================= CARD STYLE =================
    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 220, 235));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                BorderFactory.createLineBorder(new Color(255, 182, 193))
        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }
    
    // ================= FEATURE CARD =================
    private JPanel createFeatureCard(String icon, String title, String desc) {

        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createLineBorder(new Color(255, 182, 193))
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        
         JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><p style='width:250px'>" + desc + "</p></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(120, 80, 100));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

          card.add(iconLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);
        card.add(descLabel);

        return card;
        
    }
    
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("EXPENSES TRACKER ");

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 194, Short.MAX_VALUE)
        );

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton1.setText("OPEN");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 164, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(161, 161, 161))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addComponent(jButton1)
                .addContainerGap(132, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        SwingUtilities.invokeLater(() -> new HomeFrame().setVisible(true));
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
     //   try {
          //  for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
              //  if ("Nimbus".equals(info.getName())) {
                //    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                 //   break;
               // }
          //  }
       //} catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
         //   logger.log(java.util.logging.Level.SEVERE, null, ex);
       // }
        //</editor-fold>

        /* Create and display the form */
        //java.awt.EventQueue.invokeLater(() -> new HomeFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

  
}
