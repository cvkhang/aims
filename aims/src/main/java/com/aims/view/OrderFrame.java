package com.aims.view;

import com.aims.controller.OrderController;
import com.aims.dao.OrderDAO;
import com.aims.entity.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderFrame extends JFrame {
    private JTable orderTable;
    private OrderController orderController;

    public OrderFrame() {
        setTitle("AIMS - Order Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        orderController = new OrderController(this, new OrderDAO());

        // Table
        String[] columns = {"ID", "Recipient", "Email", "Address", "Status", "Total"};
        orderTable = new JTable(new DefaultTableModel(new Object[][]{}, columns));
        JScrollPane scrollPane = new JScrollPane(orderTable);

        // Bottom panel with edit button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Change status");
        editButton.addActionListener(e -> openEditDialog());
        bottomPanel.add(editButton);
        JButton viewDetailButton = new JButton("View Detail");
        viewDetailButton.addActionListener(e -> openOrderDetailDialog());
        bottomPanel.add(viewDetailButton);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadOrders();
    }

    public void loadOrders() {
        List<Order> orders = orderController.getOrders();
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        model.setRowCount(0);
        for (Order order : orders) {
            model.addRow(new Object[]{
                order.getOrderId(),
                order.getRecipientName(),
                order.getEmail(),
                order.getDeliveryAddress(),
                order.getStatus(),
                order.getTotalAmount()
            });
        }
    }

    private void openEditDialog() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to edit.");
            return;
        }

        int orderId = (int) orderTable.getValueAt(selectedRow, 0);
        String currentStatus = (String) orderTable.getValueAt(selectedRow, 4);

        String[] statuses = {"PENDING", "APPROVED", "REJECTED", "CANCELED"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(currentStatus);

        int result = JOptionPane.showConfirmDialog(this, statusCombo, "Edit Order Status", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedStatus = (String) statusCombo.getSelectedItem();
            orderController.updateOrderStatus(orderId, selectedStatus);
            loadOrders();
        }
    }
    
    private void openOrderDetailDialog() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to view.");
            return;
        }

        int orderId = (int) orderTable.getValueAt(selectedRow, 0);
        Order order = orderController.getOrderById(orderId);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Order not found.");
            return;
        }

        // Panel chính
        JPanel panel = new JPanel(new BorderLayout());

        // Thông tin order chung
        JTextArea detailArea = new JTextArea(15, 60);
        detailArea.setEditable(false);
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(order.getOrderId()).append("\n");
        sb.append("User ID: ").append(order.getUserId()).append("\n");
        sb.append("Recipient: ").append(order.getRecipientName()).append("\n");
        sb.append("Email: ").append(order.getEmail()).append("\n");
        sb.append("Phone Number: ").append(order.getPhoneNumber()).append("\n");
        sb.append("Delivery Address: ").append(order.getDeliveryAddress()).append("\n");
        sb.append("Province/City: ").append(order.getProvinceCity()).append("\n");
        sb.append("Rush Delivery: ").append(order.isRushDelivery() ? "Yes" : "No").append("\n");
        sb.append("Rush Delivery Time: ").append(order.getRushDeliveryTime() != null ? order.getRushDeliveryTime() : "N/A").append("\n");
        sb.append("Delivery Fee: ").append(order.getDeliveryFee()).append("\n");
        sb.append("Total Amount: ").append(order.getTotalAmount()).append("\n");
        sb.append("Status: ").append(order.getStatus()).append("\n");
        sb.append("Created At: ").append(order.getCreatedAt()).append("\n");

        detailArea.setText(sb.toString());
        panel.add(new JScrollPane(detailArea), BorderLayout.NORTH);

        // Bảng sản phẩm
        String[] columns = {"Product", "Quantity", "Price", "Rush"};
        Object[][] data = new Object[order.getOrderItems().size()][4];

        for (int i = 0; i < order.getOrderItems().size(); i++) {
            var item = order.getOrderItems().get(i);
            data[i][0] = item.getProduct().getTitle();
            data[i][1] = item.getQuantity();
            data[i][2] = item.getPrice();
            data[i][3] = item.isRush() ? "Yes" : "No";
        }

        JTable itemTable = new JTable(data, columns);
        itemTable.setEnabled(false);  // chỉ xem
        JScrollPane tableScrollPane = new JScrollPane(itemTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 200));

        panel.add(tableScrollPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }


    public JTable getOrderTable() {
        return orderTable;
    }
}
