package com.aims.view;

import com.aims.controller.UserController;
import com.aims.dao.UserDAO;
import com.aims.entity.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class AddUserFrame extends JDialog {
    private UserController userController;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JCheckBox blockedCheck;

    public AddUserFrame(AdminFrame parentFrame) {
        super(parentFrame, true);
        this.userController = new UserController(parentFrame, new UserDAO());

        setTitle("Add User");
        setSize(400, 350);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        roleCombo = new JComboBox<>(new String[]{"CUSTOMER", "PRODUCT_MANAGER", "ADMIN"});
        blockedCheck = new JCheckBox("Blocked");

        mainPanel.add(new JLabel("Username:"));
        mainPanel.add(usernameField);
        mainPanel.add(new JLabel("Email:"));
        mainPanel.add(emailField);
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(passwordField);
        mainPanel.add(new JLabel("Role:"));
        mainPanel.add(roleCombo);
        mainPanel.add(new JLabel("Blocked:"));
        mainPanel.add(blockedCheck);

        JButton saveButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveUser());
        cancelButton.addActionListener(e -> dispose());

        mainPanel.add(saveButton);
        mainPanel.add(cancelButton);

        add(mainPanel);
    }

    private void saveUser() {
        try {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Username and email required
            if (username.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Email are required.");
                return;
            }
            // Username length
            if (username.length() < 3 || username.length() > 50) {
                JOptionPane.showMessageDialog(this, "Username must be between 3 and 50 characters.");
                return;
            }
            // Email format
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!email.matches(emailRegex)) {
                JOptionPane.showMessageDialog(this, "Invalid email format.");
                return;
            }
            // Password length
            if (password.length() < 6 || password.length() > 100) {
                JOptionPane.showMessageDialog(this, "Password must be between 6 and 100 characters.");
                return;
            }

            // Add user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole((String) roleCombo.getSelectedItem());
            newUser.setBlocked(blockedCheck.isSelected());
            newUser.setCreatedAt(LocalDateTime.now());

            userController.addUser(newUser);

            ((AdminFrame) getOwner()).loadUsers();
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save user: " + e.getMessage());
        }
    }
}
