package com.aims.view;

import com.aims.controller.CartController;
import com.aims.dao.CartDAO;
import com.aims.entity.CartItem;
import com.aims.entity.Product;
import com.aims.util.Session;
import com.aims.util.DatabaseConnection;
import com.aims.util.ProductManagerConstraints;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class ProductPanel extends JPanel {
    private Product product;
    private int quantity;
    private MainFrame mainFrame;
    private CartFrame cartFrame;
    private CartController cartController;

    private ProductPanel(Product product, int quantity, MainFrame mainFrame, CartFrame cartFrame) {
        this.product = product;
        this.quantity = quantity;
        this.mainFrame = mainFrame;
        this.cartFrame = cartFrame;
        this.cartController = new CartController(cartFrame, new CartDAO());
        initUI();
    }

    public static ProductPanel createFromResultSet(ResultSet rs, MainFrame mainFrame) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setTitle(rs.getString("title"));
        product.setCategory(rs.getString("category"));
        product.setValue(rs.getDouble("value"));
        product.setPrice(rs.getDouble("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setRushEligible(rs.getBoolean("is_rush_eligible"));
        product.setWeight(rs.getDouble("weight"));
        product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        product.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        // --- Bổ sung ---
        product.setAuthors(rs.getString("authors"));
        product.setCoverType(rs.getString("cover_type"));
        product.setPublisher(rs.getString("publisher"));
        product.setPublicationDate(rs.getDate("publication_date") != null ? rs.getDate("publication_date").toLocalDate() : null);
        product.setNumPages(rs.getObject("num_pages", Integer.class));
        product.setBookLanguage(rs.getString("book_language"));
        product.setBookGenre(rs.getString("book_genre"));

        product.setArtists(rs.getString("artists"));
        product.setRecordLabel(rs.getString("record_label"));
        product.setTracklist(rs.getString("tracklist"));
        product.setMusicGenre(rs.getString("music_genre"));
        product.setReleaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null);

        product.setDiscType(rs.getString("disc_type"));
        product.setDirector(rs.getString("director"));
        product.setRuntime(rs.getObject("runtime", Integer.class));
        product.setStudio(rs.getString("studio"));
        product.setDvdLanguage(rs.getString("dvd_language"));
        product.setSubtitles(rs.getString("subtitles"));
        product.setAvailable(rs.getString("available"));
        
        // -------------
        return new ProductPanel(product, 0, mainFrame, null);
    }


    public static ProductPanel createFromCartItem(CartItem item, CartFrame cartFrame) {
        return new ProductPanel(item.getProduct(), item.getQuantity(), null, cartFrame);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(250, 190));

        JLabel titleLabel = new JLabel(product.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel categoryLabel = new JLabel("Category: " + product.getCategory(), SwingConstants.CENTER);
        JLabel priceLabel = new JLabel(String.format("Price: %.2f VND", product.getPrice()), SwingConstants.CENTER);
        JLabel stockLabel = new JLabel("Stock: " + product.getStockQuantity(), SwingConstants.CENTER);
        JLabel rushLabel = new JLabel(product.isRushEligible() ? "Rush Eligible" : "Standard Delivery", SwingConstants.CENTER);
        JLabel quantityLabel = quantity > 0 ? new JLabel("Quantity: " + quantity, SwingConstants.CENTER) : new JLabel("");

        JPanel infoPanel = new JPanel(new GridLayout(7, 1)); // Tăng số hàng lên 7 để thêm dòng mới
        infoPanel.add(titleLabel);
        infoPanel.add(categoryLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(stockLabel);
        infoPanel.add(rushLabel);
        infoPanel.add(quantityLabel);

        // Hiển thị dòng "Available" nếu là product manager
        if (Session.isLoggedIn() && "PRODUCT_MANAGER".equals(Session.getRole())) {
            JLabel availableLabel = new JLabel("Available: " + (product.getAvailable().equalsIgnoreCase("YES") ? "YES" : "NO"), SwingConstants.CENTER);
            infoPanel.add(availableLabel);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton detailButton = new JButton("Detail");
        detailButton.addActionListener(e -> showProductDetail());
        buttonPanel.add(detailButton);

        if (mainFrame != null) {
            JButton addToCartButton = new JButton("Add to Cart");
            addToCartButton.addActionListener(e -> addToCart());
            buttonPanel.add(addToCartButton);

            if (Session.isLoggedIn() && "PRODUCT_MANAGER".equals(Session.getRole())) {
                JButton editButton = new JButton("Edit");
                
                String toggleLabel = product.getAvailable().equalsIgnoreCase("yes") ? "Hide" : "Show";
                JButton toggleButton = new JButton(toggleLabel);

                editButton.addActionListener(e -> {
                    try {
                        new AddProductFrame(mainFrame, product).setVisible(true);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error opening edit form: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                toggleButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        (product.getAvailable().equalsIgnoreCase("yes") ? "Hide" : "Show") + " this product?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        product.setAvailable(product.getAvailable().equalsIgnoreCase("yes") ? "no": "yes");
                        product.setUpdatedAt(LocalDateTime.now());

                        try {
                            mainFrame.getProductController().changeAvailable(product, product.getAvailable(), Session.getCurrentUser().getUserId());
                            mainFrame.loadProductList();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Error updating availability: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                buttonPanel.add(editButton);
                buttonPanel.add(toggleButton);
            }
        } else if (cartFrame != null) {
            JButton updateButton = new JButton("Update");
            JButton deleteButton = new JButton("Delete");
            updateButton.addActionListener(e -> updateCartItem());
            deleteButton.addActionListener(e -> deleteCartItem());
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);
        }

        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void showProductDetail() {
        StringBuilder detail = new StringBuilder();

        // General information
        detail.append("=== General Information ===\n");
        detail.append("Title: ").append(product.getTitle()).append("\n");
        detail.append("Category: ").append(product.getCategory()).append("\n");
        detail.append("Current Price (excl. VAT): ").append(product.getPrice()).append(" VND\n");
        detail.append("Stock Quantity: ").append(product.getStockQuantity()).append("\n");
        detail.append("Rush Eligible: ").append(product.isRushEligible() ? "Yes" : "No").append("\n");
        detail.append("Weight: ").append(product.getWeight()).append(" kg\n");

        // Category-specific information
        String category = product.getCategory().toUpperCase();
        System.out.println("Category = " + product.getCategory());

        switch (category) {
            case "BOOK":
                detail.append("\n=== Book Information ===\n");
                if (product.getAuthors() != null)
                    detail.append("Authors: ").append(product.getAuthors()).append("\n");
                if (product.getCoverType() != null)
                    detail.append("Cover Type: ").append(product.getCoverType()).append("\n");
                System.out.println("getCoverType = " + product.getCoverType());
                if (product.getPublisher() != null)
                    detail.append("Publisher: ").append(product.getPublisher()).append("\n");
                if (product.getPublicationDate() != null)
                    detail.append("Publication Date: ").append(product.getPublicationDate()).append("\n");
                if (product.getNumPages() != null)
                    detail.append("Number of Pages: ").append(product.getNumPages()).append("\n");
                if (product.getBookLanguage() != null)
                    detail.append("Language: ").append(product.getBookLanguage()).append("\n");
                if (product.getBookGenre() != null)
                    detail.append("Genre: ").append(product.getBookGenre()).append("\n");
                break;

            case "CD":
            case "LP_RECORD":
                detail.append("\n=== CD/LP Information ===\n");
                if (product.getArtists() != null)
                    detail.append("Artists: ").append(product.getArtists()).append("\n");
                if (product.getRecordLabel() != null)
                    detail.append("Record Label: ").append(product.getRecordLabel()).append("\n");
                if (product.getTracklist() != null)
                    detail.append("Tracklist: ").append(product.getTracklist()).append("\n");
                if (product.getMusicGenre() != null)
                    detail.append("Genre: ").append(product.getMusicGenre()).append("\n");
                if (product.getReleaseDate() != null)
                    detail.append("Release Date: ").append(product.getReleaseDate()).append("\n");
                break;

            case "DVD":
                detail.append("\n=== DVD Information ===\n");
                if (product.getDiscType() != null)
                    detail.append("Disc Type: ").append(product.getDiscType()).append("\n");
                if (product.getDirector() != null)
                    detail.append("Director: ").append(product.getDirector()).append("\n");
                if (product.getRuntime() != null)
                    detail.append("Runtime: ").append(product.getRuntime()).append(" minutes\n");
                if (product.getStudio() != null)
                    detail.append("Studio: ").append(product.getStudio()).append("\n");
                if (product.getDvdLanguage() != null)
                    detail.append("Language: ").append(product.getDvdLanguage()).append("\n");
                if (product.getSubtitles() != null)
                    detail.append("Subtitles: ").append(product.getSubtitles()).append("\n");
                if (product.getReleaseDate() != null)
                    detail.append("Release Date: ").append(product.getReleaseDate()).append("\n");
                if (product.getBookGenre() != null)
                    detail.append("Genre: ").append(product.getBookGenre()).append("\n");
                break;

            default:
                detail.append("\n(No additional details for this product category)\n");
                break;
        }

        JOptionPane.showMessageDialog(null, detail.toString(), "Product Details", JOptionPane.INFORMATION_MESSAGE);
    }


   
    private void addToCart() {
        if (product.getStockQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Product is out of stock.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cartController.addCartItem(Session.getSessionId(), product.getProductId(), 1);
        JOptionPane.showMessageDialog(this, "Added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateCartItem() {
        String input = JOptionPane.showInputDialog(this, "Enter new quantity:", quantity);
        if (input != null) {
            try {
                int newQuantity = Integer.parseInt(input);
                if (newQuantity > 0) {
                    cartController.updateCartItem(getCartItemId(), newQuantity);
                } else {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCartItem() {
        int confirm = JOptionPane.showConfirmDialog(this, "Remove this item from cart?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cartController.deleteCartItem(getCartItemId());
        }
    }

    private int getCartItemId() {
        String sql = "SELECT ci.cart_item_id FROM cart_items ci " +
                     "JOIN cart c ON ci.cart_id = c.cart_id " +
                     "WHERE c.session_id = ? AND ci.product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, Session.getSessionId());
            stmt.setInt(2, product.getProductId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cart_item_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();	
        }
        return -1;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}