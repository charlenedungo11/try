package billsplitter.ui;

import billsplitter.model.*;
import billsplitter.service.DataStore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddGroupFrame extends JFrame {

   private JPanel membersContainer;
    private JTextField nameField;
    private JTextField dateField;
    
    private final Color BG = new Color(255, 240, 245);
    private final Color HEADER_COLOR = new Color(255, 20, 147);
    private final Color SECONDARY = new Color(255, 182, 193);
       private final Color LINK_BLUE = new Color(255, 20, 147);

    public AddGroupFrame(Rectangle bounds) {
       setTitle("Add Group");
        setSize(400, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        if (bounds != null) setBounds(bounds);
        else setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG);
        mainPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // --- BACK BUTTON ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topPanel.setBackground(BG);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setForeground(HEADER_COLOR);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            new GroupListFrame(this.getBounds()).setVisible(true);
            dispose();
        });
        topPanel.add(backBtn);

        // --- HEADER ---
        JLabel headerLabel = new JLabel("Add Group");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        headerLabel.setForeground(HEADER_COLOR);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(topPanel);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // --- GROUP NAME ---
        mainPanel.add(createLabel("Group Name"));
        nameField = createStyledField();
        mainPanel.add(nameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- DATE ---
        mainPanel.add(createLabel("Date"));
        dateField = createStyledField();
        dateField.setEditable(false);
        dateField.setBackground(Color.WHITE);
        
        JButton pickDateBtn = createStyledButton("Pick Date");
        pickDateBtn.addActionListener(e -> new DatePickerDialog(this, dateField).setVisible(true));
        
        mainPanel.add(dateField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(pickDateBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- DYNAMIC MEMBERS SECTION ---
        mainPanel.add(createLabel("Participants"));
        
        membersContainer = new JPanel();
        membersContainer.setLayout(new BoxLayout(membersContainer, BoxLayout.Y_AXIS));
        membersContainer.setBackground(Color.WHITE);
        membersContainer.setBorder(BorderFactory.createLineBorder(SECONDARY, 1));

        // Start with just one empty participant row
        addParticipantRow("");

        mainPanel.add(membersContainer);

        // --- ADD ANOTHER PARTICIPANT LINK (Pink Theme) ---
        JButton addAnotherBtn = new JButton("Add Another Participant");
        addAnotherBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        addAnotherBtn.setForeground(HEADER_COLOR); // Changed from Blue to Deep Pink
        addAnotherBtn.setContentAreaFilled(false);
        addAnotherBtn.setBorderPainted(false);
        addAnotherBtn.setFocusPainted(false);
        addAnotherBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addAnotherBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addAnotherBtn.addActionListener(e -> addParticipantRow(""));

        mainPanel.add(addAnotherBtn);

        // --- SAVE BUTTON ---
        mainPanel.add(Box.createVerticalGlue());
        JButton saveBtn = createStyledButton("Save Group");
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        saveBtn.setBackground(HEADER_COLOR); 
        saveBtn.addActionListener(e -> handleSave());
        
        mainPanel.add(saveBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        add(new JScrollPane(mainPanel) {{
            setBorder(null);
            setBackground(BG);
            getViewport().setBackground(BG);
        }});
    }

    private void addParticipantRow(String name) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        JTextField pField = new JTextField(name);
        pField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // Enter key adds new row
        pField.addActionListener(e -> addParticipantRow(""));

        row.add(pField, BorderLayout.CENTER);

        // Delete button for all rows
        JButton delBtn = new JButton("✕");
        delBtn.setForeground(SECONDARY);
        delBtn.setContentAreaFilled(false);
        delBtn.setBorderPainted(false);
        delBtn.setFocusPainted(false);
        delBtn.addActionListener(e -> {
            // Keep at least one row or just allow removal
            membersContainer.remove(row);
            membersContainer.revalidate();
            membersContainer.repaint();
        });
        row.add(delBtn, BorderLayout.EAST);

        membersContainer.add(row);
        membersContainer.revalidate();
        pField.requestFocusInWindow();
    }

    private void handleSave() {
        String gName = nameField.getText().trim();
        String gDate = dateField.getText();

        if (gName.isEmpty() || gDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Group Name and Date!");
            return;
        }

        Group g = new Group(gName, gDate);

        int count = 0;
        for (Component c : membersContainer.getComponents()) {
            if (c instanceof JPanel) {
                JTextField f = (JTextField) ((JPanel) c).getComponent(0);
                String pName = f.getText().trim();
                if (!pName.isEmpty()) {
                    g.addMember(new Member(pName));
                    count++;
                }
            }
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(this, "Please add at least one member!");
            return;
        }

        DataStore.groups.add(g);
        DataStore.saveData();

        new GroupListFrame(this.getBounds()).setVisible(true);
        dispose();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(80, 80, 80));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(5, 0, 5, 0));
        return label;
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(SECONDARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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
     //   try {
         //   for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            //    if ("Nimbus".equals(info.getName())) {
            //        javax.swing.UIManager.setLookAndFeel(info.getClassName());
            //        break;
              //  }
           // }
      //  } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
         //   logger.log(java.util.logging.Level.SEVERE, null, ex);
        //}
        //</editor-fold>

        /* Create and display the form */
       java.awt.EventQueue.invokeLater(() -> new AddGroupFrame (null).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
