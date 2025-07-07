package com.aims.view;

import com.aims.controller.ProductController;
import com.aims.dao.ProductDAO;
import com.aims.util.Session;
import com.aims.util.DatabaseConnection;
import com.aims.util.ProductManagerConstraints;


import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private List<ProductPanel> productList = new ArrayList<>();
    private JPanel productPanel;
    private ProductController productController;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> rushEligibleCombo;
    private JComboBox<String> sortTypeCombo;
    private JComboBox<String> sortOrderCombo;
    private JButton loginButton, logoutButton, signupButton, profileButton;
    private int page = 1;
    private final int PAGE_SIZE = 12; // 4x3 grid
    private JLabel pageLabel;
    private JPanel toolBar; // Lưu tham chiếu để refresh
    private JLabel sessionLabel; // Thêm nhãn cho session_id

    public MainFrame() {
        setTitle("AIMS - Internet Media Store");
        setSize(1500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Thanh điều hướng
        toolBar = createToolBar();
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolBar, BorderLayout.NORTH);

        // Thanh header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel storeName = new JLabel("AIMS Media Store");
        storeName.setFont(new Font("Arial", Font.BOLD, 24));
        leftPanel.add(storeName);

        searchField = new JTextField(20);
        leftPanel.add(new JLabel("Search Title: "));
        leftPanel.add(searchField);

        // Bộ lọc category
        categoryCombo = new JComboBox<>(getCategories());
        categoryCombo.insertItemAt("All", 0);
        categoryCombo.setSelectedIndex(0);
        leftPanel.add(new JLabel("Category: "));
        leftPanel.add(categoryCombo);

        // Bộ lọc is_rush_eligible
        rushEligibleCombo = new JComboBox<>(new String[]{"All", "Rush Eligible", "Non-Rush"});
        leftPanel.add(new JLabel("Rush Delivery: "));
        leftPanel.add(rushEligibleCombo);

        // Sắp xếp
        sortTypeCombo = new JComboBox<>(new String[]{"Sort by Title", "Sort by Price"});
        sortOrderCombo = new JComboBox<>(new String[]{"Asc", "Desc"});
        leftPanel.add(sortTypeCombo);
        leftPanel.add(sortOrderCombo);

        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");
        leftPanel.add(searchButton);
        leftPanel.add(resetButton);

        // Nút Login/Signup hoặc Logout/Profile
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");
        profileButton = new JButton("Profile");
        logoutButton = new JButton("Logout");
        updateLoginUI(rightPanel);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        topPanel.add(headerPanel, BorderLayout.CENTER);

        // Panel sản phẩm
        productPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        productPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(productPanel);

        // Panel phân trang
        JPanel paginationPanel = new JPanel(new FlowLayout());
        JButton prevButton = new JButton("Previous");
        pageLabel = new JLabel("Page 1");
        JButton nextButton = new JButton("Next");
        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        // Khởi tạo controller
        productController = new ProductController(this, new ProductDAO());

        // Sự kiện
        searchButton.addActionListener(e -> {
            page = 1;
            loadProductList();
        });
        resetButton.addActionListener(e -> {
            page = 1;
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            rushEligibleCombo.setSelectedIndex(0);
            sortTypeCombo.setSelectedIndex(0);
            sortOrderCombo.setSelectedIndex(0);
            loadProductList();
        });
        prevButton.addActionListener(e -> {
            if (page > 1) {
                page--;
                loadProductList();
            }
        });
        nextButton.addActionListener(e -> {
        	if(productList.size() > 12) {
        		page++;
                loadProductList();
            }            
        });
        loginButton.addActionListener(e -> {
        	new LoginFrame(this).setVisible(true);
        	dispose();
        });
        signupButton.addActionListener(e -> {
        	new SignupFrame(this).setVisible(true);
        	dispose();
        });
        profileButton.addActionListener(e -> new ProfileFrame().setVisible(true));
        logoutButton.addActionListener(e -> {
            Session.logout();
            updateLoginUI(rightPanel);
            refreshToolBar();
        });
        page=1;
        loadProductList();
    }

    private JPanel createToolBar() {
    	JPanel toolBar = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton mainButton = new JButton("Home");
        JButton cartButton = new JButton("Cart");
        JButton ordersButton = new JButton("Orders");
        JButton adminButton = new JButton("Admin");

        mainButton.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
            
        });
        cartButton.addActionListener(e -> {
            dispose();
            new CartFrame().setVisible(true);
        });
        ordersButton.addActionListener(e -> {
            if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
                new OrderFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Only Product Managers can view orders.");
            }
        });
        adminButton.addActionListener(e -> {
            if (Session.getRole() != null && Session.getRole().equals("ADMIN")) {
                new AdminFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Only Admins can access this panel.");
            }
        });

        leftPanel.add(mainButton);
        leftPanel.add(cartButton);
        if (Session.isLoggedIn() && Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
        	leftPanel.add(ordersButton);
            JButton addProductButton = new JButton("Add Product");
            addProductButton.addActionListener(e -> new AddProductFrame(this).setVisible(true));
            leftPanel.add(addProductButton);
            
            JButton bulkDeleteButton = new JButton("Bulk Delete");
            bulkDeleteButton.addActionListener(e -> showBulkDeleteDialog());
            leftPanel.add(bulkDeleteButton);
        }
        if (Session.isLoggedIn() && Session.getRole() != null && Session.getRole().equals("ADMIN")) {
            leftPanel.add(adminButton);
        }
        sessionLabel = new JLabel("Session: " + (Session.getSessionId() != null ? Session.getSessionId() : "Guest"));
        rightPanel.add(sessionLabel);
        
        toolBar.add(leftPanel, BorderLayout.WEST);
        toolBar.add(rightPanel, BorderLayout.EAST);
        return toolBar;
    }

    private void updateLoginUI(JPanel rightPanel) {
        rightPanel.removeAll();
        if (Session.isLoggedIn()) {
            rightPanel.add(profileButton);
            rightPanel.add(logoutButton);
        } else {
            rightPanel.add(loginButton);
            rightPanel.add(signupButton);
        }
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public void refreshToolBar() {
        JPanel topPanel = (JPanel) getContentPane().getComponent(0);
        topPanel.remove(0); // Xóa toolBar cũ
        toolBar = createToolBar();
        topPanel.add(toolBar, BorderLayout.NORTH);
        topPanel.revalidate();
        topPanel.repaint();
        loadProductList();
    }

    private String[] getCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM products ORDER BY category";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories.toArray(new String[0]);
    }

    public void loadProductList() {
        productList.clear();
        String searchText = searchField.getText().trim();
        String category = categoryCombo.getSelectedItem().equals("All") ? null : (String) categoryCombo.getSelectedItem();
        String rushEligible = (String) rushEligibleCombo.getSelectedItem();
        Boolean isRushEligible = null;
        if (rushEligible.equals("Rush Eligible")) {
            isRushEligible = true;
        } else if (rushEligible.equals("Non-Rush")) {
            isRushEligible = false;
        }
        String sortBy = (String) sortTypeCombo.getSelectedItem();
        String sortOrder = (String) sortOrderCombo.getSelectedItem();

        String sortColumn = sortBy.equals("Sort by Title") ? "title" : "price";
        String order = sortOrder.equals("Asc") ? "ASC" : "DESC";

        ResultSet rs = productController.getProducts(searchText, category, isRushEligible, sortColumn, order, page);
        try {
            while (rs.next()) {
                productList.add(ProductPanel.createFromResultSet(rs, this));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        displayProducts();
    }

    public void displayProducts() {
        productPanel.removeAll();
        int start = 0;
        int end = Math.min(PAGE_SIZE, productList.size());
        for (int i = start; i < end; i++) {
            productPanel.add(productList.get(i));
        }
        pageLabel.setText("Page " + page);
        productPanel.revalidate();
        productPanel.repaint();
    }

    public ProductController getProductController() {
        return productController;
    }
    
    private void showBulkDeleteDialog() {
        if (!Session.isLoggedIn() || !Session.getRole().equals("PRODUCT_MANAGER")) {
            JOptionPane.showMessageDialog(this, "Only Product Managers can perform bulk delete operations.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show constraint information
        int remainingQuota = ProductManagerConstraints.getRemainingDailyQuota(Session.getCurrentUser().getUserId());
        String message = "Daily delete/update quota remaining: " + remainingQuota + "/30\n\n";
        message += "You can delete up to 10 products at once.\n";
        message += "Enter product IDs separated by commas:";
        
        String input = JOptionPane.showInputDialog(this, message, "Bulk Delete Products", JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                String[] productIdStrings = input.split(",");
                List<Integer> productIds = new ArrayList<>();
                
                for (String idStr : productIdStrings) {
                    int productId = Integer.parseInt(idStr.trim());
                    productIds.add(productId);
                }
                
                if (productIds.size() > 10) {
                    JOptionPane.showMessageDialog(this, "Cannot delete more than 10 products at once.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete " + productIds.size() + " products?\nThis action cannot be undone.", 
                    "Confirm Bulk Delete", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    productController.deleteMultipleProducts(productIds, Session.getCurrentUser().getUserId());
                    loadProductList();
                    JOptionPane.showMessageDialog(this, "Successfully deleted " + productIds.size() + " products.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid product ID format. Please enter valid numbers separated by commas.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}