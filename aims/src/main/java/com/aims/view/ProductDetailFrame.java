package com.aims.view;

import com.aims.controller.CartController;
import com.aims.controller.ProductController;
import com.aims.dao.CartDAO;
import com.aims.dao.ProductDAO;
import com.aims.entity.Product;
import com.aims.util.Session;
import com.aims.util.ProductManagerConstraints;

import javax.swing.*;
import java.awt.*;

public class ProductDetailFrame extends JDialog {
    private Product product;
    private ProductController productController;
    private MainFrame parentFrame;

    public ProductDetailFrame(Product product, MainFrame parentFrame) {
        super(parentFrame, true);
        this.product = product;
        this.parentFrame = parentFrame;
        this.productController = new ProductController(parentFrame, new ProductDAO());
        setTitle("Product Details: " + product.getTitle());
        setSize(400, 500);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thuộc tính chung
        addLabel(mainPanel, "Title:", product.getTitle());
        addLabel(mainPanel, "Category:", product.getCategory());
        addLabel(mainPanel, "Value:", String.format("%.2f VND", product.getValue()));
        addLabel(mainPanel, "Price:", String.format("%.2f VND", product.getPrice()));
        addLabel(mainPanel, "Stock:", String.valueOf(product.getStockQuantity()));
        addLabel(mainPanel, "Rush Eligible:", product.isRushEligible() ? "Yes" : "No");
        addLabel(mainPanel, "Weight:", String.format("%.2f kg", product.getWeight()));

        // Thuộc tính theo loại sản phẩm
        switch (product.getCategory()) {
            case "BOOK":
                addLabel(mainPanel, "Authors:", product.getAuthors());
                addLabel(mainPanel, "Cover Type:", product.getCoverType());
                addLabel(mainPanel, "Publisher:", product.getPublisher());
                addLabel(mainPanel, "Publication Date:", product.getPublicationDate() != null ? product.getPublicationDate().toString() : "N/A");
                addLabel(mainPanel, "Pages:", product.getNumPages() != null ? product.getNumPages().toString() : "N/A");
                addLabel(mainPanel, "Language:", product.getBookLanguage() != null ? product.getBookLanguage() : "N/A");
                addLabel(mainPanel, "Genre:", product.getBookGenre() != null ? product.getBookGenre() : "N/A");
                break;
            case "CD":
            case "LP_RECORD":
                addLabel(mainPanel, "Artists:", product.getArtists());
                addLabel(mainPanel, "Record Label:", product.getRecordLabel());
                addLabel(mainPanel, "Tracklist:", product.getTracklist());
                addLabel(mainPanel, "Genre:", product.getMusicGenre());
                addLabel(mainPanel, "Release Date:", product.getReleaseDate() != null ? product.getReleaseDate().toString() : "N/A");
                break;
            case "DVD":
                addLabel(mainPanel, "Disc Type:", product.getDiscType());
                addLabel(mainPanel, "Director:", product.getDirector());
                addLabel(mainPanel, "Runtime:", product.getRuntime() != null ? product.getRuntime() + " minutes" : "N/A");
                addLabel(mainPanel, "Studio:", product.getStudio());
                addLabel(mainPanel, "Language:", product.getDvdLanguage());
                addLabel(mainPanel, "Subtitles:", product.getSubtitles());
                break;
        }

        // Panel thêm vào giỏ hàng
        JPanel cartPanel = new JPanel(new FlowLayout());
        JLabel quantityLabel = new JLabel("Quantity:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, product.getStockQuantity(), 1));
        JButton addToCartButton = new JButton("Add to Cart");
        cartPanel.add(quantityLabel);
        cartPanel.add(quantitySpinner);
        cartPanel.add(addToCartButton);

        // Panel cho Product Manager
        JPanel managerPanel = new JPanel(new FlowLayout());
        if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
            JButton editButton = new JButton("Edit Product");
            JButton deleteButton = new JButton("Delete Product");
            managerPanel.add(editButton);
            managerPanel.add(deleteButton);

            editButton.addActionListener(e -> {
                new AddProductFrame(parentFrame, product).setVisible(true);
                dispose();
            });
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        productController.deleteProduct(product.getProductId(), Session.getCurrentUser().getUserId());
                        parentFrame.loadProductList();
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            // Add constraint information
            int remainingQuota = ProductManagerConstraints.getRemainingDailyQuota(Session.getCurrentUser().getUserId());
            JLabel quotaLabel = new JLabel("Daily quota remaining: " + remainingQuota + "/30");
            quotaLabel.setForeground(Color.BLUE);
            managerPanel.add(quotaLabel);
        }

        addToCartButton.addActionListener(e -> {
            int quantity = (int) quantitySpinner.getValue();
            CartController cartController = new CartController(null, new CartDAO());
            cartController.addCartItem(Session.getSessionId(), product.getProductId(), quantity);
            JOptionPane.showMessageDialog(this, "Added " + quantity + " of " + product.getTitle() + " to cart.");
        });

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(cartPanel, BorderLayout.SOUTH);
        if (managerPanel.getComponentCount() > 0) {
            contentPanel.add(managerPanel, BorderLayout.NORTH);
        }
        add(contentPanel);
    }

    private void addLabel(JPanel panel, String labelText, String value) {
        panel.add(new JLabel(labelText));
        panel.add(new JLabel(value != null ? value : "N/A"));
    }
}