package com.thesis.business.musicinstrument.order;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.account.AccountService;
import com.thesis.business.musicinstrument.import_order.ImportOrder;
import com.thesis.business.musicinstrument.import_order.ImportOrderService;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetail;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetailService;
import com.thesis.business.musicinstrument.orderDetail.OrderDetail;
import com.thesis.business.musicinstrument.orderDetail.OrderDetailService;
import com.thesis.business.musicinstrument.payment.PaymentService;
import com.thesis.business.musicinstrument.product.Product;
import com.thesis.business.musicinstrument.product.ProductService;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class CustomerOrderService {

    @Inject
    CustomerOrderRepository customerOrderRepository;

    @Inject
    AccountService accountService;

    @Inject
    PaymentService paymentService;

    @Inject
    OrderDetailService orderDetailService;

    @Inject
    ProductService productService;

    @Inject
    ImportOrderDetailService importOrderDetailService;

    @Inject 
    ImportOrderService importOrderService;

    @Transactional
    public Long add(CustomerOrderRequest customerOrderRequest, String username, String role) {

        if(accountService.findById(customerOrderRequest.getAccount().getId(), username, role) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");
        if(paymentService.findById(customerOrderRequest.getPayment().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Payment method does not exist");
        
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setPhone(customerOrderRequest.getPhone());
        customerOrder.setAddress(customerOrderRequest.getAddress());
        customerOrder.setDate(LocalDate.now());
        customerOrder.setTotal(customerOrderRequest.getTotal());
        customerOrder.setNote(customerOrderRequest.getNote());
        customerOrder.setAccount(customerOrderRequest.getAccount());
        customerOrder.setPayment(customerOrderRequest.getPayment());
        customerOrder.setStatus(0);

        customerOrderRepository.persist(customerOrder);

        for(int i = 0; i < customerOrderRequest.getProductsInCartDTO().size(); i++){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setQuantity(customerOrderRequest.getProductsInCartDTO().get(i).getQuantity());
            List<ImportOrderDetail> importOrderDetails = importOrderDetailService.findByProductId(
                customerOrderRequest.getProductsInCartDTO().get(i).getProduct().getId()
            );
            System.out.println(importOrderDetails);
            for(int j = 0; j < importOrderDetails.size(); j++){
                if(importOrderDetails.get(j).getSoldQuantity() + orderDetail.getQuantity() <= importOrderDetails.get(j).getQuantity()){
                    ImportOrder importOrder = importOrderService.findById(importOrderDetails.get(j).getImportOrder().getId());
                    if(importOrder.getStatus() == 1){
                        orderDetail.setImportOrderDetail(importOrderDetails.get(j));
                        break;
                    }
                }
            }
            
            orderDetail.setTotal(
                (long)customerOrderRequest.getProductsInCartDTO().get(i).getQuantity() *
                (long)customerOrderRequest.getProductsInCartDTO().get(i).getProduct().getPrice()
            );
            orderDetail.setCustomerOrder(new CustomerOrder(customerOrder.getId()));
            orderDetailService.add(orderDetail);
            importOrderDetailService.updateSoldQuantity(orderDetail.getImportOrderDetail().getId(), orderDetail.getQuantity(), false);
            productService.updateQuantity(orderDetail.getImportOrderDetail().getProduct().getId(), orderDetail.getQuantity(), false);
        }
        
            
        return customerOrder.getId();
    }

    public List<CustomerOrder> findAll() {

        return customerOrderRepository.listAll();
    }

    public CustomerOrder findById(Long id){

        return customerOrderRepository.findById(id);
    }

    public List<CustomerOrder> findByAccountId(Long accountId){

        return customerOrderRepository.list("account.id = ?1 ORDER BY date", accountId);
    }

    // @Transactional
    // public void updateById(Long id, Integer status) {

    //     CustomerOrder customerOrderInDB = customerOrderRepository.findById(id);
    //     if(customerOrderInDB == null)
    //         throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
    //     if(customerOrderInDB.getStatus() == -1 && status != -1){
    //         List<OrderDetail> orderDetails = orderDetailService.findByOrderId(customerOrderInDB.getId());
    //         for(int i = 0; i < orderDetails.size(); i++)
    //             productService.updateQuantity(orderDetails.get(i).getProduct().getId(), orderDetails.get(i).getQuantity(), false);
    //     }
    //     customerOrderInDB.setStatus(status);
    //     customerOrderRepository.persist(customerOrderInDB);
    //     if(status == -1){
    //         List<OrderDetail> orderDetails = orderDetailService.findByOrderId(customerOrderInDB.getId());
    //         for(int i = 0; i < orderDetails.size(); i++)
    //             productService.updateQuantity(orderDetails.get(i).getProduct().getId(), orderDetails.get(i).getQuantity(), true);
    //     }
    // }

    // @Transactional
    // public void cancelById(Long id, Integer status) {

    //     CustomerOrder customerOrderInDB = customerOrderRepository.findById(id);
    //     if(customerOrderInDB == null)
    //         throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
    //     if(status == -1){
    //         customerOrderInDB.setStatus(status);
    //         customerOrderRepository.persist(customerOrderInDB);

    //         List<OrderDetail> orderDetails = orderDetailService.findByOrderId(customerOrderInDB.getId());
    //         for(int i = 0; i < orderDetails.size(); i++)
    //             productService.updateQuantity(orderDetails.get(i).getProduct().getId(), orderDetails.get(i).getQuantity(), true);
    //     }     
    // }

    @Transactional
    public void deleteById(Long id){
        
        if(customerOrderRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
    }

    public List<CustomerOrder> statistic(String type) {
        if(type.equals("month")){
            return customerOrderRepository.find("SELECT DATE_TRUNC('month', date) AS month, SUM(total) AS total " +
                "FROM CustomerOrder " + "WHERE status = 2" +
                "GROUP BY DATE_TRUNC('month', date)")
                .list();
        }
        else {
            LocalDate currentDate = LocalDate.now();

            // Tính ngày bắt đầu của tuần trước
            LocalDate lastWeekStartDate = currentDate.minusWeeks(1).with(DayOfWeek.MONDAY);

            return customerOrderRepository.find("SELECT date, SUM(total) AS total " +
                "FROM CustomerOrder " +
                "WHERE date >= ?1 AND date <= ?2 and status = ?3" +
                "GROUP BY date", lastWeekStartDate, currentDate, 2)
                .list();
        }
    }

    public Long statisticTotal(){
        return customerOrderRepository.count("status = ?1", 2);
    }

    public Long statisticTotalRevenue(){
        List<CustomerOrder> customerOrders = customerOrderRepository.find("status", 2).list();
        Long totalRevenue  = 0L;
        for(CustomerOrder customerOrder : customerOrders){
            totalRevenue += customerOrder.getTotal();
        }
        return totalRevenue;
    }
}
