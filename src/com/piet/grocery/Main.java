package com.piet.grocery;

import com.piet.grocery.data.DummyStore;
import com.piet.grocery.ui.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DummyStore.initialize();
        } catch (SQLException exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Could not connect to MySQL. Check that MySQL is running and database.properties is correct.\n\n"
                            + exception.getMessage(),
                    "Database Connection Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Keep the default Swing look if system look is not available.
            }
            new LoginFrame().setVisible(true);
        });
    }
}
