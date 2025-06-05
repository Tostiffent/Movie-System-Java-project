package org.moviesystem;

import org.moviesystem.dao.UserDAO;
import org.moviesystem.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LoginFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 32);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 14);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private static User currentUser;

    public LoginFrame() {
        setTitle("Comedy Movie System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Initialize the users table
        UserDAO.initializeUsersTable();

        // Create main panel with some padding
        JPanel mainPanel = new JPanel(new BorderLayout(30, 30));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(50, 80, 50, 80));

        // Create and add components
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createLoginPanel(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = new JLabel("Comedy Movie System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        subtitleLabel.setForeground(SECONDARY_COLOR);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createLoginPanel() {
        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBackground(CARD_COLOR);
        
        // Add subtle shadow effect
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 2, true),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 4, 4),  // Shadow offset
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
            )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 12, 0);  // Increased spacing between elements

        // Username field
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(PRIMARY_COLOR);
        loginCard.add(usernameLabel, gbc);

        gbc.gridy = 1;
        usernameField = createStyledTextField("Enter your username");
        loginCard.add(usernameField, gbc);

        // Password field
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passwordLabel.setForeground(PRIMARY_COLOR);
        loginCard.add(passwordLabel, gbc);

        gbc.gridy = 3;
        passwordField = createStyledPasswordField("Enter your password");
        loginCard.add(passwordField, gbc);

        // Login button
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 0, 12, 0);  // Extra top spacing for button
        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> handleLogin());  // Re-adding the action listener
        loginCard.add(loginButton, gbc);

        return loginCard;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextFieldWithHeight field = new JTextFieldWithHeight(25);
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setForeground(Color.DARK_GRAY);
        field.setPreferredHeight(40);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Add placeholder text
        addPlaceholder(field, placeholder);

        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordFieldWithHeight field = new JPasswordFieldWithHeight(25);
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setForeground(Color.DARK_GRAY);
        field.setPreferredHeight(40);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Add placeholder text
        addPlaceholder(field, placeholder);

        return field;
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.DARK_GRAY);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('•');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });

        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(250, 45));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Check if fields contain placeholder text
        if (username.equals("Enter your username") || password.equals("Enter your password")) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser = UserDAO.authenticateUser(username, password);
        if (currentUser != null) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                Main.createAndShowGUI(currentUser);
            });
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}

// Helper classes to add preferred height to text fields
class JTextFieldWithHeight extends JTextField {
    private int preferredHeight;

    public JTextFieldWithHeight(int columns) {
        super(columns);
    }

    public void setPreferredHeight(int preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.height = preferredHeight;
        return size;
    }
}

class JPasswordFieldWithHeight extends JPasswordField {
    private int preferredHeight;

    public JPasswordFieldWithHeight(int columns) {
        super(columns);
    }

    public void setPreferredHeight(int preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.height = preferredHeight;
        return size;
    }
} 