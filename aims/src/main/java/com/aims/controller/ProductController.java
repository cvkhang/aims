package com.aims.controller;

import com.aims.dao.ProductDAO;
import com.aims.entity.Product;
import com.aims.view.MainFrame;
import com.aims.util.ProductManagerConstraints;
import com.aims.util.Session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductController {
    private MainFrame view;
    private ProductDAO productDAO;

    public ProductController(MainFrame view, ProductDAO productDAO) {
        this.view = view;
        this.productDAO = productDAO;
    }

    public ResultSet getProducts(String searchText, String category, Boolean isRushEligible, String sortColumn, String sortOrder, int page) {
        return productDAO.getProducts(searchText, category, isRushEligible, sortColumn, sortOrder, page);
    }

    public Product getProductById(int productId) {
        return productDAO.getProductById(productId);
    }

    public void addProduct(Product product, int userId) {
        // Check if user is product manager and can add products
        if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
            if (!ProductManagerConstraints.canAddProduct(userId)) {
                throw new RuntimeException("Cannot add product: Daily limit exceeded.");
            }
        }
        
        int productID = productDAO.addProduct(product);
        productDAO.logProductHistory(productID, "ADD", "Added new product: " + product.getTitle(), userId);
    }

    public void updateProduct(Product product, int userId) {
        // Check if user is product manager and can update products
        if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
            String error = ProductManagerConstraints.canUpdateProducts(userId, 1);
            if (error != null) {
                throw new RuntimeException(error);
            }
        }
        
        Product oldProduct = getProductById(product.getProductId());
        boolean priceUpdated = oldProduct != null && oldProduct.getPrice() != product.getPrice();

        if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER") && priceUpdated) {
            String priceError = ProductManagerConstraints.canUpdatePrice(userId, product.getProductId());
            if (priceError != null) {
                throw new RuntimeException(priceError);
            }
            // Validate price range
            String priceValidationError = ProductManagerConstraints.validatePrice(product.getPrice(), product.getValue());
            if (priceValidationError != null) {
                throw new RuntimeException(priceValidationError);
            }
        }
        
        productDAO.updateProduct(product);
        
        String description = "Updated product: " + product.getTitle();
        if (priceUpdated) {
            description += " (price updated from " + oldProduct.getPrice() + " to " + product.getPrice() + ")";
        }
        
        productDAO.logProductHistory(product.getProductId(), "EDIT", description, userId);
    }

    public void deleteProduct(int productId, int userId) {
        // Check if user is product manager and can delete products
        if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
            String error = ProductManagerConstraints.canDeleteProducts(userId, 1);
            if (error != null) {
                throw new RuntimeException(error);
            }
        }
        
        Product product = getProductById(productId);
        productDAO.deleteProduct(productId);
        productDAO.logProductHistory(productId, "DELETE", "Deleted product: " + product.getTitle(), userId);
    }
    
    public void deleteMultipleProducts(List<Integer> productIds, int userId) {
        // Check if user is product manager and can delete multiple products
        if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
            String error = ProductManagerConstraints.canDeleteProducts(userId, productIds.size());
            if (error != null) {
                throw new RuntimeException(error);
            }
        }
        
        for (Integer productId : productIds) {
            Product product = getProductById(productId);
            productDAO.deleteProduct(productId);
            productDAO.logProductHistory(productId, "DELETE", "Deleted product: " + product.getTitle(), userId);
        }
    }
    
    public void changeAvailable(Product product, String status, int userId) {
    	productDAO.changeAvailable(product.getProductId(), status, userId);
    	if(status.equalsIgnoreCase("YES"))
    		productDAO.logProductHistory(product.getProductId(), "SHOW", "Deleted product: " + product.getTitle(), userId);
    	else
    		productDAO.logProductHistory(product.getProductId(), "HIDE", "Deleted product: " + product.getTitle(), userId);
    }
}