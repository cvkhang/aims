package com.aims.util;

import com.aims.dao.ProductDAO;
import com.aims.util.DatabaseConnection;
import com.aims.entity.Product;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

public class ProductManagerConstraints {
    
    private static final int MAX_DAILY_DELETE_UPDATE = 30;
    private static final int MAX_BULK_DELETE = 10;
    private static final int MAX_DAILY_PRICE_UPDATES = 2;
    private static final double MIN_PRICE_RATIO = 0.3; 
    private static final double MAX_PRICE_RATIO = 1.5; 
    
   
    public static boolean canAddProduct(int userId) {
        return true;
    }
    

    public static boolean canEditProduct(int userId) {
        return true;
    }
    
    
    public static String canDeleteProducts(int userId, int numberOfProducts) {
        if (numberOfProducts > MAX_BULK_DELETE) {
            return "Cannot delete more than " + MAX_BULK_DELETE + " products at once.";
        }
        
        int dailyDeleteCount = getDailyDeleteCount(userId);
        if (dailyDeleteCount + numberOfProducts > MAX_DAILY_DELETE_UPDATE) {
            return "Cannot delete more than " + MAX_DAILY_DELETE_UPDATE + " products per day. " +
                   "Already deleted " + dailyDeleteCount + " today. " +
                   "Can delete " + (MAX_DAILY_DELETE_UPDATE - dailyDeleteCount) + " more.";
        }
        
        return null; 
    }
    
   
    public static String canUpdateProducts(int userId, int numberOfProducts) {
        int dailyUpdateCount = getDailyUpdateCount(userId);
        if (dailyUpdateCount + numberOfProducts > MAX_DAILY_DELETE_UPDATE) {
            return "Cannot update more than " + MAX_DAILY_DELETE_UPDATE + " products per day. " +
                   "Already updated " + dailyUpdateCount + " today. " +
                   "Can update " + (MAX_DAILY_DELETE_UPDATE - dailyUpdateCount) + " more.";
        }
        
        return null; 
    }
    
  
    public static String canUpdatePrice(int userId, int productId) {
        int dailyPriceUpdateCount = getDailyPriceUpdateCount(userId, productId);
        if (dailyPriceUpdateCount >= MAX_DAILY_PRICE_UPDATES) {
            return "Cannot update price more than " + MAX_DAILY_PRICE_UPDATES + " times per day for this product.";
        }
        
        return null; 
    }
    
  
    public static String validatePrice(double price, double value) {
        if (price < value * MIN_PRICE_RATIO) {
            return "Price cannot be less than " + (MIN_PRICE_RATIO * 100) + "% of product value (" + 
                   String.format("%.2f", value * MIN_PRICE_RATIO) + " VND).";
        }
        
        if (price > value * MAX_PRICE_RATIO) {
            return "Price cannot be more than " + (MAX_PRICE_RATIO * 100) + "% of product value (" + 
                   String.format("%.2f", value * MAX_PRICE_RATIO) + " VND).";
        }
        
        return null; 
    }
    
   
    public static int getRemainingPriceUpdates(int userId, int productId) {
        int usedUpdates = getDailyPriceUpdateCount(userId, productId);
        return Math.max(0, MAX_DAILY_PRICE_UPDATES - usedUpdates);
    }
    

    public static int getRemainingDailyQuota(int userId) {
        int usedQuota = getDailyDeleteCount(userId) + getDailyUpdateCount(userId);
        return Math.max(0, MAX_DAILY_DELETE_UPDATE - usedQuota);
    }
    

    private static int getDailyDeleteCount(int userId) {
        String sql = "SELECT COUNT(*) FROM product_history " +
                    "WHERE user_id = ? AND operation = 'DELETE' " +
                    "AND DATE(created_at) = CURRENT_DATE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
   
    private static int getDailyUpdateCount(int userId) {
        String sql = "SELECT COUNT(*) FROM product_history " +
                    "WHERE user_id = ? AND operation = 'EDIT' " +
                    "AND DATE(created_at) = CURRENT_DATE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
   
    private static int getDailyPriceUpdateCount(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM product_history " +
                    "WHERE user_id = ? AND product_id = ? AND operation = 'EDIT' " +
                    "AND LOWER(description) LIKE '%price%' " +
                    "AND DATE(created_at) = CURRENT_DATE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getTimeUntilReset() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        long secondsUntilReset = java.time.Duration.between(now, tomorrow).getSeconds();
        
        long hours = secondsUntilReset / 3600;
        long minutes = (secondsUntilReset % 3600) / 60;
        
        return String.format("%02d:%02d", hours, minutes);
    }
} 