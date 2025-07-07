package com.aims.controller;

import com.aims.dao.UserDAO;
import com.aims.entity.User;
import com.aims.view.AdminFrame;

import java.util.List;
import javax.swing.JFrame;

public class UserController {
    private final AdminFrame adminView;
    private final UserDAO userDAO;

    public UserController(JFrame view, UserDAO userDAO) {
        this.adminView = view instanceof AdminFrame ? (AdminFrame) view : null;
        this.userDAO = userDAO;
    }

    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        return userDAO.getUserByUsernameAndPassword(username, password);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void addUser(User user) {
        try {
            userDAO.addUser(user);
            if (adminView != null) {
                adminView.loadUsers();
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to add user: " + e.getMessage(), e);
        }
    }

    public void updateUser(User user) {
        try {
            userDAO.updateUser(user);
            if (adminView != null) {
                adminView.loadUsers();
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    public void deleteUser(int userId) {
        try {
            userDAO.deleteUser(userId);
            if (adminView != null) {
                adminView.loadUsers();
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    public void resetPassword(int userId) {
        try {
            userDAO.resetPassword(userId);
            if (adminView != null) {
                adminView.loadUsers();
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to reset password: " + e.getMessage(), e);
        }
    }

    public void toggleBlockUser(int userId, boolean isBlocked) {
        try {
            userDAO.toggleBlockUser(userId, isBlocked);
            if (adminView != null) {
                adminView.loadUsers();
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to toggle block status: " + e.getMessage(), e);
        }
    }
}