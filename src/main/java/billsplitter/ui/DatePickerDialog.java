package billsplitter.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class DatePickerDialog extends JDialog {

    private YearMonth currentMonth;
    private JPanel calendarPanel;
    private JTextField targetField;

    Color PINK = new Color(255, 182, 193);

    public DatePickerDialog(JFrame parent, JTextField targetField) {
        super(parent, "Select Date", true);
        this.targetField = targetField;

        // Increased height slightly to accommodate the grid without squeezing
        setSize(350, 350); 
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        currentMonth = YearMonth.now();

        // 🔥 TOP NAV
        JPanel top = new JPanel();
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        JLabel monthLabel = new JLabel();
        monthLabel.setPreferredSize(new Dimension(120, 30));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        styleBtn(prev);
        styleBtn(next);

        top.add(prev);
        top.add(monthLabel);
        top.add(next);

        add(top, BorderLayout.NORTH);

        // 🔥 CALENDAR GRID (7 columns for days of the week)
        calendarPanel = new JPanel(new GridLayout(0, 7));
        add(calendarPanel, BorderLayout.CENTER);

        // 🔁 ACTIONS
        prev.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar(monthLabel);
        });

        next.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar(monthLabel);
        });

        updateCalendar(monthLabel);
    }

    private void updateCalendar(JLabel monthLabel) {
        calendarPanel.removeAll();

        monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());

        // Days header
        String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            calendarPanel.add(lbl);
        }

        LocalDate firstDay = currentMonth.atDay(1);
        int startDay = firstDay.getDayOfWeek().getValue() % 7;

        // Empty slots at start
        for (int i = 0; i < startDay; i++) {
            calendarPanel.add(new JLabel(""));
        }

        int daysInMonth = currentMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            JButton btn = new JButton(String.valueOf(day));
            styleDayButton(btn);

            final int selectedDay = day;
            btn.addActionListener(e -> {
                String date = currentMonth.getYear() + "-" +
                        String.format("%02d", currentMonth.getMonthValue()) + "-" +
                        String.format("%02d", selectedDay);

                targetField.setText(date);
                dispose();
            });

            calendarPanel.add(btn);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void styleBtn(JButton btn) {
        btn.setBackground(PINK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void styleDayButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        // CRITICAL: Remove margins so the text doesn't truncate to "..."
        btn.setMargin(new Insets(0, 0, 0, 0)); 
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PINK);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
        });
    }
}