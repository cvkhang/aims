package com.aims.dao;

import com.aims.entity.Product;
import com.aims.util.DatabaseConnection;
import com.aims.util.Session;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class ProductDAO {
	public ResultSet getProducts(String searchText, String category, Boolean isRushEligible, String sortColumn, String sortOrder, int page) {
	    StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
	    List<Object> params = new ArrayList<>();

	    if (searchText != null && !searchText.isEmpty()) {
	        sql.append(" AND LOWER(title) LIKE LOWER(?)");
	        params.add("%" + searchText + "%");
	    }
	    if (category != null && !category.isEmpty()) {
	        sql.append(" AND category = ?");
	        params.add(category);
	    }
	    if (isRushEligible != null) {
	        sql.append(" AND is_rush_eligible = ?");
	        params.add(isRushEligible);
	    }
	    String role = Session.getRole();
	    System.out.printf("role %s",role,"\n");
	    if(!("product_manager".equalsIgnoreCase(role))) {
	    	sql.append(" AND available = 'yes'");
	    }
	    
	    // Chỉ cho phép sort theo các cột hợp lệ
	    List<String> allowedSortColumns = java.util.Arrays.asList("title", "price");
	    if (!allowedSortColumns.contains(sortColumn)) sortColumn = "title";
	    String safeOrder = "ASC";
	    if (sortOrder != null && sortOrder.equalsIgnoreCase("DESC")) safeOrder = "DESC";
	    sql.append(" ORDER BY ").append(sortColumn).append(" ").append(safeOrder);

	    // Phân trang: LIMIT và OFFSET
	    sql.append(" LIMIT 13 OFFSET ?");
	    int offset = (page - 1) * 12;
	    params.add(offset);

	    try {
	        Connection conn = DatabaseConnection.getConnection();
	        PreparedStatement stmt = conn.prepareStatement(sql.toString());

	        for (int i = 0; i < params.size(); i++) {
	            stmt.setObject(i + 1, params.get(i));
	        }

	        return stmt.executeQuery();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}


    public Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
                product.setAuthors(rs.getString("authors"));
                product.setCoverType(rs.getString("cover_type"));
                product.setPublisher(rs.getString("publisher"));
                if (rs.getDate("publication_date") != null) {
                    product.setPublicationDate(rs.getDate("publication_date").toLocalDate());
                }
                product.setNumPages(rs.getObject("num_pages") != null ? rs.getInt("num_pages") : null);
                product.setBookLanguage(rs.getString("book_language"));
                product.setBookGenre(rs.getString("book_genre"));
                product.setArtists(rs.getString("artists"));
                product.setRecordLabel(rs.getString("record_label"));
                product.setTracklist(rs.getString("tracklist"));
                product.setMusicGenre(rs.getString("music_genre"));
                if (rs.getDate("release_date") != null) {
                    product.setReleaseDate(rs.getDate("release_date").toLocalDate());
                }
                product.setDiscType(rs.getString("disc_type"));
                product.setDirector(rs.getString("director"));
                product.setRuntime(rs.getObject("runtime") != null ? rs.getInt("runtime") : null);
                product.setStudio(rs.getString("studio"));
                product.setDvdLanguage(rs.getString("dvd_language"));
                product.setSubtitles(rs.getString("subtitles"));
                product.setAvailable(rs.getString("available"));
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addProduct(Product product) {
        String sql = "INSERT INTO products (title, category, value, price, stock_quantity, is_rush_eligible, weight, " +
                     "authors, cover_type, publisher, publication_date, num_pages, book_language, book_genre, " +
                     "artists, record_label, tracklist, music_genre, release_date, disc_type, director, runtime, " +
                     "studio, dvd_language, subtitles, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getTitle());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getValue());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStockQuantity());
            stmt.setBoolean(6, product.isRushEligible());
            stmt.setDouble(7, product.getWeight());
            stmt.setString(8, product.getAuthors());
            stmt.setString(9, product.getCoverType());
            stmt.setString(10, product.getPublisher());
            stmt.setObject(11, product.getPublicationDate() != null ? java.sql.Date.valueOf(product.getPublicationDate()) : null);
            stmt.setObject(12, product.getNumPages());
            stmt.setString(13, product.getBookLanguage());
            stmt.setString(14, product.getBookGenre());
            stmt.setString(15, product.getArtists());
            stmt.setString(16, product.getRecordLabel());
            stmt.setString(17, product.getTracklist());
            stmt.setString(18, product.getMusicGenre());
            stmt.setObject(19, product.getReleaseDate() != null ? java.sql.Date.valueOf(product.getReleaseDate()) : null);
            stmt.setString(20, product.getDiscType());
            stmt.setString(21, product.getDirector());
            stmt.setObject(22, product.getRuntime());
            stmt.setString(23, product.getStudio());
            stmt.setString(24, product.getDvdLanguage());
            stmt.setString(25, product.getSubtitles());
            stmt.setTimestamp(26, java.sql.Timestamp.valueOf(product.getCreatedAt()));
            stmt.setTimestamp(27, java.sql.Timestamp.valueOf(product.getUpdatedAt()));
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateProduct(Product product) {
        String sql = "UPDATE products SET title = ?, category = ?, value = ?, price = ?, stock_quantity = ?, " +
                     "is_rush_eligible = ?, weight = ?, authors = ?, cover_type = ?, publisher = ?, " +
                     "publication_date = ?, num_pages = ?, book_language = ?, book_genre = ?, " +
                     "artists = ?, record_label = ?, tracklist = ?, music_genre = ?, release_date = ?, " +
                     "disc_type = ?, director = ?, runtime = ?, studio = ?, dvd_language = ?, subtitles = ?, " +
                     "updated_at = ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getTitle());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getValue());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStockQuantity());
            stmt.setBoolean(6, product.isRushEligible());
            stmt.setDouble(7, product.getWeight());
            stmt.setString(8, product.getAuthors());
            stmt.setString(9, product.getCoverType());
            stmt.setString(10, product.getPublisher());
            stmt.setObject(11, product.getPublicationDate() != null ? java.sql.Date.valueOf(product.getPublicationDate()) : null);
            stmt.setObject(12, product.getNumPages());
            stmt.setString(13, product.getBookLanguage());
            stmt.setString(14, product.getBookGenre());
            stmt.setString(15, product.getArtists());
            stmt.setString(16, product.getRecordLabel());
            stmt.setString(17, product.getTracklist());
            stmt.setString(18, product.getMusicGenre());
            stmt.setObject(19, product.getReleaseDate() != null ? java.sql.Date.valueOf(product.getReleaseDate()) : null);
            stmt.setString(20, product.getDiscType());
            stmt.setString(21, product.getDirector());
            stmt.setObject(22, product.getRuntime());
            stmt.setString(23, product.getStudio());
            stmt.setString(24, product.getDvdLanguage());
            stmt.setString(25, product.getSubtitles());
            stmt.setTimestamp(26, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(27, product.getProductId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logProductHistory(int productId, String operation, String description, int userId) {
        String sql = "INSERT INTO product_history (product_id, operation, description, user_id, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setString(2, operation);
            stmt.setString(3, description);
            stmt.setInt(4, userId);
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void changeAvailable(int productId, String status, int userId) {
        String sql = "UPDATE products SET available = ?, updated_at = CURRENT_TIMESTAMP WHERE product_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.toUpperCase());
            stmt.setInt(2, productId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Product availability updated successfully by user ID: " + userId);
            } else {
                System.out.println("No product found with ID: " + productId);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating product availability: " + e.getMessage());
        }
    }

}