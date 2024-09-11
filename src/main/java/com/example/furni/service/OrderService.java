package com.example.furni.service;

import com.example.furni.entity.Material;
import com.example.furni.entity.OrderProduct;
import com.example.furni.entity.Orders;
import com.example.furni.repository.OrderProductRepository;
import com.example.furni.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    // Lấy tất cả đơn hàng
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    // Lấy chi tiết đơn hàng theo ID
    public Orders getOrderById(int id) {
        return ordersRepository.findById(id).orElse(null);
    }
    public Page<Orders> getOrdersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ordersRepository.findAll(pageable);
    }

    // Lấy danh sách sản phẩm theo orderId
    public List<OrderProduct> getOrderProductsByOrderId(int orderId) {
        return orderProductRepository.findByOrderId(orderId);
    }
    // Lưu đơn hàng sau khi cập nhật
    public void saveOrder(Orders order) {
        ordersRepository.save(order);
    }

    // lọc đơn hàng theo chỉ tiêu
    public Page<Orders> filterOrders(int page, int size, String ShippingMethod, Double TotalAmount, String PaymentMethod, String IsPaid, String Status) {
        Pageable pageable = PageRequest.of(page, size);

        // Thay thế null cho các tham số không điền
        ShippingMethod = (ShippingMethod == null || ShippingMethod.isEmpty()) ? null : ShippingMethod;
        PaymentMethod = (PaymentMethod == null || PaymentMethod.isEmpty()) ? null : PaymentMethod;
        IsPaid = (IsPaid == null || IsPaid.isEmpty()) ? null : IsPaid;
        Status = (Status == null || Status.isEmpty()) ? null : Status;

        // Gọi đến repository để lọc đơn hàng
        return ordersRepository.filterOrders(ShippingMethod, TotalAmount, PaymentMethod, IsPaid, Status, pageable);
    }
}
