package com.aims;

import javax.swing.SwingUtilities;

import com.aims.view.MainFrame;
public class App {
    public static void main(String[] args) {
    	
//    	new Thread(() -> {
//            try {
//            	VNPayReturnServer.startVNPayReturnServer("Order", 20000, () -> {
//                    // Chỉ chạy completePurchase sau khi thanh toán thành công
//                        JOptionPane.showMessageDialog(null, "Order placed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        // Mở Swing UI
//        JFrame frame = new JFrame("VNPay Payment Test");
//        JButton payButton = new JButton("Thanh toán VNPay");
//
//        payButton.addActionListener((ActionEvent e) -> {
//            try {
//                String paymentUrl = VNPayController.createVNPayUrl("ORDER12sssssswwwssddds23", 60000);
//                Desktop.getDesktop().browse(new URI(paymentUrl));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 200);
//        frame.setLayout(new FlowLayout());
//        frame.add(payButton);
//        frame.setVisible(true);
    	
        System.out.println("App started!");
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}