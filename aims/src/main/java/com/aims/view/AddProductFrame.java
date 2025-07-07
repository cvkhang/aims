package com.aims.view;

import com.aims.controller.ProductController;
import com.aims.entity.Product;
import com.aims.util.Session;
import com.aims.util.ProductManagerConstraints;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddProductFrame extends JDialog {
    private MainFrame parentFrame;
    private Product product;
    private ProductController productController;
    private JTextField titleField, priceField, valueField, stockField, weightField;
    private JComboBox<String> categoryCombo;
    private JCheckBox rushEligibleCheck;
    // Trường cho Book
    private JTextField authorsField, coverTypeField, publisherField, publicationDateField, numPagesField, bookLanguageField, bookGenreField;
    // Trường cho CD và LP_Record
    private JTextField artistsField, recordLabelField, tracklistField, musicGenreField, releaseDateField;
    // Trường cho DVD
    private JTextField directorField, runtimeField, studioField, dvdLanguageField, subtitlesField, discTypeField;
    private JPanel bookPanel, cdPanel, dvdPanel, dynamicPanel;
    private JLabel constraintInfoLabel;

    public AddProductFrame(MainFrame parentFrame) {
        this(parentFrame, null);
    }

    public AddProductFrame(MainFrame parentFrame, Product product) {
        super(parentFrame, product == null ? "Add Product" : "Edit Product", true);
        this.parentFrame = parentFrame;
        this.product = product;
        this.productController = parentFrame.getProductController();
        initUI();
        if (product != null) {
            populateFields();
        }
    }

    private void initUI() {
        setSize(600, 700);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new BorderLayout());
        JPanel commonPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        commonPanel.setBorder(BorderFactory.createTitledBorder("Common Information"));

        commonPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        commonPanel.add(titleField);

        commonPanel.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{"Book", "CD", "LP_Record", "DVD"});
        categoryCombo.addActionListener(e -> updateSpecificFields());
        commonPanel.add(categoryCombo);

        commonPanel.add(new JLabel("Price (VND):"));
        priceField = new JTextField();
        commonPanel.add(priceField);

        commonPanel.add(new JLabel("Value (VND):"));
        valueField = new JTextField();
        commonPanel.add(valueField);

        commonPanel.add(new JLabel("Stock Quantity:"));
        stockField = new JTextField();
        commonPanel.add(stockField);

        commonPanel.add(new JLabel("Weight (kg):"));
        weightField = new JTextField();
        commonPanel.add(weightField);

        commonPanel.add(new JLabel("Rush Eligible:"));
        rushEligibleCheck = new JCheckBox();
        commonPanel.add(rushEligibleCheck);

        bookPanel = createBookPanel();
        cdPanel = createCDPanel();
        dvdPanel = createDVDPanel();

        dynamicPanel = new JPanel(new BorderLayout());
        dynamicPanel.add(bookPanel, BorderLayout.CENTER);

        formPanel.add(commonPanel, BorderLayout.NORTH);
        formPanel.add(dynamicPanel, BorderLayout.CENTER);

        // Add constraint information for product managers
        if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
            JPanel constraintPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            constraintPanel.setBorder(BorderFactory.createTitledBorder("Daily Limits"));
            
            constraintInfoLabel = new JLabel();
            updateConstraintInfo();
            constraintPanel.add(constraintInfoLabel);
            
            JLabel resetTimeLabel = new JLabel("Reset time: " + ProductManagerConstraints.getTimeUntilReset());
            constraintPanel.add(resetTimeLabel);
            
            formPanel.add(constraintPanel, BorderLayout.SOUTH);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> dispose());

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void updateConstraintInfo() {
        if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
            int remainingQuota = ProductManagerConstraints.getRemainingDailyQuota(Session.getCurrentUser().getUserId());
            constraintInfoLabel.setText("Remaining daily quota: " + remainingQuota + "/30");
        }
    }

    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Book Information"));

        panel.add(new JLabel("Authors:"));
        authorsField = new JTextField();
        panel.add(authorsField);

        panel.add(new JLabel("Cover Type:"));
        coverTypeField = new JTextField();
        panel.add(coverTypeField);

        panel.add(new JLabel("Publisher:"));
        publisherField = new JTextField();
        panel.add(publisherField);

        panel.add(new JLabel("Publication Date (yyyy-MM-dd):"));
        publicationDateField = new JTextField();
        panel.add(publicationDateField);

        panel.add(new JLabel("Number of Pages:"));
        numPagesField = new JTextField();
        panel.add(numPagesField);

        panel.add(new JLabel("Language:"));
        bookLanguageField = new JTextField();
        panel.add(bookLanguageField);

        panel.add(new JLabel("Genre:"));
        bookGenreField = new JTextField();
        panel.add(bookGenreField);

        return panel;
    }

    private JPanel createCDPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("CD / LP_Record Information"));

        panel.add(new JLabel("Artists:"));
        artistsField = new JTextField();
        panel.add(artistsField);

        panel.add(new JLabel("Record Label:"));
        recordLabelField = new JTextField();
        panel.add(recordLabelField);

        panel.add(new JLabel("Tracklist:"));
        tracklistField = new JTextField();
        panel.add(tracklistField);

        panel.add(new JLabel("Music Genre:"));
        musicGenreField = new JTextField();
        panel.add(musicGenreField);

        panel.add(new JLabel("Release Date (yyyy-MM-dd):"));
        releaseDateField = new JTextField();
        panel.add(releaseDateField);

        return panel;
    }

    private JPanel createDVDPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("DVD Information"));

        panel.add(new JLabel("Director:"));
        directorField = new JTextField();
        panel.add(directorField);

        panel.add(new JLabel("Runtime (minutes):"));
        runtimeField = new JTextField();
        panel.add(runtimeField);

        panel.add(new JLabel("Studio:"));
        studioField = new JTextField();
        panel.add(studioField);

        panel.add(new JLabel("Language:"));
        dvdLanguageField = new JTextField();
        panel.add(dvdLanguageField);

        panel.add(new JLabel("Subtitles:"));
        subtitlesField = new JTextField();
        panel.add(subtitlesField);

        panel.add(new JLabel("Disc Type:"));
        discTypeField = new JTextField();
        panel.add(discTypeField);

        return panel;
    }

    private void updateSpecificFields() {
        String category = (String) categoryCombo.getSelectedItem();
        dynamicPanel.removeAll();
        switch (category) {
            case "Book":
                dynamicPanel.add(bookPanel, BorderLayout.CENTER);
                break;
            case "CD":
            case "LP_Record":
                dynamicPanel.add(cdPanel, BorderLayout.CENTER);
                break;
            case "DVD":
                dynamicPanel.add(dvdPanel, BorderLayout.CENTER);
                break;
        }
        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }

    private void populateFields() {
        titleField.setText(product.getTitle());
        categoryCombo.setSelectedItem(product.getCategory());
        priceField.setText(String.valueOf(product.getPrice()));
        valueField.setText(String.valueOf(product.getValue()));
        stockField.setText(String.valueOf(product.getStockQuantity()));
        weightField.setText(String.valueOf(product.getWeight()));
        rushEligibleCheck.setSelected(product.isRushEligible());

        switch (product.getCategory()) {
            case "Book":
                authorsField.setText(product.getAuthors());
                coverTypeField.setText(product.getCoverType());
                publisherField.setText(product.getPublisher());
                if (product.getPublicationDate() != null) {
                    publicationDateField.setText(product.getPublicationDate().toString());
                }
                if (product.getNumPages() != null) {
                    numPagesField.setText(String.valueOf(product.getNumPages()));
                }
                bookLanguageField.setText(product.getBookLanguage());
                bookGenreField.setText(product.getBookGenre());
                break;
            case "CD":
            case "LP_Record":
                artistsField.setText(product.getArtists());
                recordLabelField.setText(product.getRecordLabel());
                tracklistField.setText(product.getTracklist());
                musicGenreField.setText(product.getMusicGenre());
                if (product.getReleaseDate() != null) {
                    releaseDateField.setText(product.getReleaseDate().toString());
                }
                break;
            case "DVD":
                directorField.setText(product.getDirector());
                if (product.getRuntime() != null) {
                    runtimeField.setText(String.valueOf(product.getRuntime()));
                }
                studioField.setText(product.getStudio());
                dvdLanguageField.setText(product.getDvdLanguage());
                subtitlesField.setText(product.getSubtitles());
                discTypeField.setText(product.getDiscType());
                break;
        }
    }

    private void saveProduct() {
        try {
            Product newProduct = (product != null) ? product : new Product();

            // Lấy và gán dữ liệu cơ bản
            String title = titleField.getText().trim();
            if (!title.isEmpty()) newProduct.setTitle(title);
            else if (product != null) newProduct.setTitle(product.getTitle());

            Object selectedCategory = categoryCombo.getSelectedItem();
            if (selectedCategory != null) {
                newProduct.setCategory(selectedCategory.toString().toUpperCase());
            } else if (product != null) {
                newProduct.setCategory(product.getCategory());
            }

            String price = priceField.getText().trim();
            if (!price.isEmpty()) newProduct.setPrice(Double.parseDouble(price));
            else if (product != null) newProduct.setPrice(product.getPrice());

            String value = valueField.getText().trim();
            if (!value.isEmpty()) newProduct.setValue(Double.parseDouble(value));
            else if (product != null) newProduct.setValue(product.getValue());

            String stock = stockField.getText().trim();
            if (!stock.isEmpty()) newProduct.setStockQuantity(Integer.parseInt(stock));
            else if (product != null) newProduct.setStockQuantity(product.getStockQuantity());

            String weight = weightField.getText().trim();
            if (!weight.isEmpty()) newProduct.setWeight(Double.parseDouble(weight));
            else if (product != null) newProduct.setWeight(product.getWeight());

            newProduct.setRushEligible(rushEligibleCheck.isSelected());

            // Gán các trường tùy theo loại sản phẩm
            switch (newProduct.getCategory()) {
                case "BOOK":
                    String authors = authorsField.getText().trim();
                    if (!authors.isEmpty()) newProduct.setAuthors(authors);
                    else if (product != null) newProduct.setAuthors(product.getAuthors());

                    String coverType = coverTypeField.getText().trim();
                    if (!coverType.isEmpty()) newProduct.setCoverType(coverType.toUpperCase());
                    else if (product != null) newProduct.setCoverType(product.getCoverType());

                    String publisher = publisherField.getText().trim();
                    if (!publisher.isEmpty()) newProduct.setPublisher(publisher);
                    else if (product != null) newProduct.setPublisher(product.getPublisher());

                    String pubDate = publicationDateField.getText().trim();
                    if (!pubDate.isEmpty()) {
                        newProduct.setPublicationDate(LocalDate.parse(pubDate, DateTimeFormatter.ISO_LOCAL_DATE));
                    } else if (product != null) {
                        newProduct.setPublicationDate(product.getPublicationDate());
                    }

                    String numPages = numPagesField.getText().trim();
                    if (!numPages.isEmpty()) {
                        newProduct.setNumPages(Integer.parseInt(numPages));
                    } else if (product != null) {
                        newProduct.setNumPages(product.getNumPages());
                    }

                    String bookLang = bookLanguageField.getText().trim();
                    if (!bookLang.isEmpty()) newProduct.setBookLanguage(bookLang);
                    else if (product != null) newProduct.setBookLanguage(product.getBookLanguage());

                    String bookGenre = bookGenreField.getText().trim();
                    if (!bookGenre.isEmpty()) newProduct.setBookGenre(bookGenre);
                    else if (product != null) newProduct.setBookGenre(product.getBookGenre());
                    break;

                case "CD":
                case "LP_RECORD":
                    String artists = artistsField.getText().trim();
                    if (!artists.isEmpty()) newProduct.setArtists(artists);
                    else if (product != null) newProduct.setArtists(product.getArtists());

                    String label = recordLabelField.getText().trim();
                    if (!label.isEmpty()) newProduct.setRecordLabel(label);
                    else if (product != null) newProduct.setRecordLabel(product.getRecordLabel());

                    String tracklist = tracklistField.getText().trim();
                    if (!tracklist.isEmpty()) newProduct.setTracklist(tracklist);
                    else if (product != null) newProduct.setTracklist(product.getTracklist());

                    String genre = musicGenreField.getText().trim();
                    if (!genre.isEmpty()) newProduct.setMusicGenre(genre);
                    else if (product != null) newProduct.setMusicGenre(product.getMusicGenre());

                    String relDate = releaseDateField.getText().trim();
                    if (!relDate.isEmpty()) {
                        newProduct.setReleaseDate(LocalDate.parse(relDate, DateTimeFormatter.ISO_LOCAL_DATE));
                    } else if (product != null) {
                        newProduct.setReleaseDate(product.getReleaseDate());
                    }
                    break;

                case "DVD":
                    String director = directorField.getText().trim();
                    if (!director.isEmpty()) newProduct.setDirector(director);
                    else if (product != null) newProduct.setDirector(product.getDirector());

                    String runtime = runtimeField.getText().trim();
                    if (!runtime.isEmpty()) {
                        newProduct.setRuntime(Integer.parseInt(runtime));
                    } else if (product != null) {
                        newProduct.setRuntime(product.getRuntime());
                    }

                    String studio = studioField.getText().trim();
                    if (!studio.isEmpty()) newProduct.setStudio(studio);
                    else if (product != null) newProduct.setStudio(product.getStudio());

                    String lang = dvdLanguageField.getText().trim();
                    if (!lang.isEmpty()) newProduct.setDvdLanguage(lang);
                    else if (product != null) newProduct.setDvdLanguage(product.getDvdLanguage());

                    String subtitles = subtitlesField.getText().trim();
                    if (!subtitles.isEmpty()) newProduct.setSubtitles(subtitles);
                    else if (product != null) newProduct.setSubtitles(product.getSubtitles());

                    String discType = discTypeField.getText().trim();
                    if (!discType.isEmpty()) newProduct.setDiscType(discType.toUpperCase());
                    else if (product != null) newProduct.setDiscType(product.getDiscType());
                    break;
            }

            if (product == null) {
                newProduct.setCreatedAt(LocalDateTime.now());
                newProduct.setUpdatedAt(LocalDateTime.now());
                productController.addProduct(newProduct, Session.getCurrentUser().getUserId());
                JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                newProduct.setUpdatedAt(LocalDateTime.now());
                productController.updateProduct(newProduct, Session.getCurrentUser().getUserId());
                
                // Show price update information for product managers
                if (Session.getRole() != null && Session.getRole().equals("PRODUCT_MANAGER")) {
                    int remainingUpdates = ProductManagerConstraints.getRemainingPriceUpdates(
                        Session.getCurrentUser().getUserId(), product.getProductId());
                    String message = "Product updated successfully!";
                    if (remainingUpdates < 2) {
                        message += "\nRemaining price updates today: " + remainingUpdates;
                    }
                    JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            parentFrame.loadProductList();
            dispose();

        } catch (NumberFormatException | DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format. Please check numbers and dates.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
