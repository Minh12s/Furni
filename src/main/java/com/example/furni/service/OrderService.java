package com.example.furni.service;

import com.example.furni.entity.*;
import com.example.furni.repository.NotificationRepository;
import com.example.furni.repository.OrderProductRepository;
import com.example.furni.repository.OrdersRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private NotificationRepository notificationRepository;

    // Lấy tất cả đơn hàng
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    // Lấy chi tiết đơn hàng theo ID
    public Orders getOrderById(int id) {
        return ordersRepository.findById(id).orElse(null);
    }
    public Orders getOrderBySecureToken(String secureToken) {
        return ordersRepository.findBySecureToken(secureToken).orElse(null);
    }

    public Page<Orders> getOrdersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ordersRepository.findAll(pageable);
    }
    public Page<Orders> findOderByUserId(int userId, Pageable pageable) {
        return ordersRepository.findOderByUserId(userId, pageable); // Gọi phương thức với phân trang
    }
    // Lấy danh sách sản phẩm theo orderId
    public List<OrderProduct> getOrderProductsByOrderId(int orderId) {
        return orderProductRepository.findByOrderId(orderId);
    }
    // Lưu đơn hàng sau khi cập nhật
    public void saveOrder(Orders order) {
        ordersRepository.save(order);
    }
    public void saveNotification(User user, Orders order,String title,  String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setOrder(order);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
    // lọc đơn hàng theo chỉ tiêu
    public Page<Orders> filterOrders(int page, int size, String ShippingMethod, Double TotalAmount, String PaymentMethod, String IsPaid, String Status, String orderCode) {
        Pageable pageable = PageRequest.of(page, size);

        // Thay thế null cho các tham số không điền
        ShippingMethod = (ShippingMethod == null || ShippingMethod.isEmpty()) ? null : ShippingMethod;
        PaymentMethod = (PaymentMethod == null || PaymentMethod.isEmpty()) ? null : PaymentMethod;
        IsPaid = (IsPaid == null || IsPaid.isEmpty()) ? null : IsPaid;
        Status = (Status == null || Status.isEmpty()) ? null : Status;
        orderCode = (orderCode == null || orderCode.isEmpty()) ? null : orderCode; // Thêm xử lý cho orderCode

        // Gọi đến repository để lọc đơn hàng
        return ordersRepository.filterOrders(ShippingMethod, TotalAmount, PaymentMethod, IsPaid, Status, orderCode, pageable);
    }

    public void saveOrderProduct(OrderProduct orderProduct) {
        orderProductRepository.save(orderProduct);
    }

    public void sendThankYouEmail(String fullName, String email, Orders order, List<Cart> cartItems, double subtotal, double tax, double shippingFee, double totalAmount) {
        String fixedEmail = ""; // Địa chỉ email cố định
        try {
            // Định dạng ngày tháng trước khi truyền vào context
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedOrderDate = order.getOrderDate().format(formatter);

            // Gọi đến mailService để gửi email, truyền thêm ngày tháng đã định dạng
            mailService.sendThankYouEmail(fullName, fixedEmail, email, formattedOrderDate, order, cartItems, tax, subtotal, shippingFee, totalAmount);
        } catch (MessagingException e) {
            e.printStackTrace(); // Xử lý lỗi gửi email
        }
    }
    public Orders getLatestOrder() {
        return ordersRepository.findFirstByOrderByIdDesc();
    }


}
