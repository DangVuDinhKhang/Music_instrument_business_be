package com.thesis.business.musicinstrument.order;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.account.AccountService;
import com.thesis.business.musicinstrument.import_order.ImportOrder;
import com.thesis.business.musicinstrument.import_order.ImportOrderService;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetail;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetailService;
import com.thesis.business.musicinstrument.orderDetail.OrderDetail;
import com.thesis.business.musicinstrument.orderDetail.OrderDetailService;
import com.thesis.business.musicinstrument.order_receipt_link.OrderReceiptLink;
import com.thesis.business.musicinstrument.order_receipt_link.OrderReceiptLinkService;
import com.thesis.business.musicinstrument.payment.Payment;
import com.thesis.business.musicinstrument.payment.PaymentService;
import com.thesis.business.musicinstrument.product.Product;
import com.thesis.business.musicinstrument.product.ProductService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;


import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import jakarta.servlet.ServletException;

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

    @Inject
    OrderReceiptLinkService orderReceiptLinkService;

    public String paymentVNPay(Long customerOrderId, Long total) throws ServletException, IOException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        //long amount = Integer.parseInt(req.getParameter("amount"))*100;
        long amount = total * 100;
        //String bankCode = req.getParameter("bankCode");
        String bankCode = "";
        
        // String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_TxnRef = String.valueOf(customerOrderId);
        //String vnp_IpAddr = Config.getIpAddress(req);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = Config.vnp_TmnCode;
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        
        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");

        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl + String.valueOf(customerOrderId));
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);   
        
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }

    @Transactional
    public String add(CustomerOrderRequest customerOrderRequest, String username, String role) {

        String result = null;
        Payment payment = paymentService.findById(customerOrderRequest.getPayment().getId());
        
        
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
        if(payment.getName().toLowerCase().contains("vn"))
            customerOrder.setStatus(3);
        else
            customerOrder.setStatus(0);

        customerOrderRepository.persist(customerOrder);

        Integer missing = 0;

        for(int i = 0; i < customerOrderRequest.getProductsInCartDTO().size(); i++){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setQuantity(customerOrderRequest.getProductsInCartDTO().get(i).getQuantity());
            List<ImportOrderDetail> importOrderDetails = importOrderDetailService.findByProductId(
                customerOrderRequest.getProductsInCartDTO().get(i).getProduct().getId()
            );
            do {
                for(int j = 0; j < importOrderDetails.size(); j++){
                    int addQuantity = orderDetail.getQuantity();
                    if(missing != 0){
                        addQuantity = missing;
                    }
                    if(importOrderDetails.get(j).getSoldQuantity() + addQuantity <= importOrderDetails.get(j).getQuantity()){
                        ImportOrder importOrder = importOrderService.findById(importOrderDetails.get(j).getImportOrder().getId());
                        if(importOrder.getStatus() == 1){
                            OrderReceiptLink orderReceiptLink = new OrderReceiptLink(orderDetail, importOrderDetails.get(j), addQuantity);
                            orderReceiptLinkService.add(orderReceiptLink);
                            importOrderDetailService.updateSoldQuantity(orderReceiptLink.getImportOrderDetail().getId(), addQuantity, false);
                            productService.updateQuantity(orderReceiptLink.getImportOrderDetail().getProduct().getId(), addQuantity, false);
                            missing = 0;
                            break;
                        }
                    }
                    else{
                        System.out.println("Khong du");
                        Integer tempQuantity = 0;
                        for(int k = 0; k < orderDetail.getQuantity(); k++){
                            tempQuantity = orderDetail.getQuantity() - k;
                            if(importOrderDetails.get(j).getSoldQuantity() + tempQuantity <= importOrderDetails.get(j).getQuantity()){
                                ImportOrder importOrder = importOrderService.findById(importOrderDetails.get(j).getImportOrder().getId());
                                if(importOrder.getStatus() == 1){
                                    OrderReceiptLink orderReceiptLink = new OrderReceiptLink(orderDetail, importOrderDetails.get(j), tempQuantity);
                                    orderReceiptLinkService.add(orderReceiptLink);
                                    importOrderDetailService.updateSoldQuantity(orderReceiptLink.getImportOrderDetail().getId(), orderReceiptLink.getQuantity(), false);
                                    productService.updateQuantity(orderReceiptLink.getImportOrderDetail().getProduct().getId(), orderReceiptLink.getQuantity(), false);
                                    missing = k;
                                    break;
                                }
                            }
                        }
                        
                    }
                } 
                
            } while(missing != 0);
            
            
            orderDetail.setTotal(
                (long)customerOrderRequest.getProductsInCartDTO().get(i).getQuantity() *
                (long)customerOrderRequest.getProductsInCartDTO().get(i).getProduct().getPrice()
            );
            orderDetail.setCustomerOrder(new CustomerOrder(customerOrder.getId()));
            orderDetailService.add(orderDetail);
            
            
        }
        if(payment.getName().toLowerCase().contains("vn")){
            try {
                result = paymentVNPay(customerOrder.getId(), customerOrder.getTotal());
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
        }
        return result;


    }

    public List<CustomerOrder> findAll() {

        return customerOrderRepository.listAll();
    }
    

    public CustomerOrder findById(Long id){

        return customerOrderRepository.findById(id);
    }

    // public CustomerOrder findByIdAndAccount(Long id, String username, String role){

    //     return customerOrderRepository.find("id = ?1 and customerOrder.account", id, );
    // }

    public List<CustomerOrder> findByAccountId(Long accountId){

        return customerOrderRepository.list("account.id = ?1 ORDER BY date", accountId);
    }

    @Transactional
    public void updateById(Long id, Integer status) {

        CustomerOrder customerOrderInDB = customerOrderRepository.findById(id);
        if(customerOrderInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
        if(customerOrderInDB.getStatus() == -1 && status != -1){
            List<OrderDetail> orderDetails = orderDetailService.findByOrderId(customerOrderInDB.getId());
            for(int i = 0; i < orderDetails.size(); i++){
                List<OrderReceiptLink> orderReceiptLinks = orderReceiptLinkService.findAllByOrderDetailId(orderDetails.get(i).getId());
                Product product = productService.findById(orderReceiptLinks.get(0).getImportOrderDetail().getProduct().getId());
                productService.updateQuantity(product.getId(), orderDetails.get(i).getQuantity(), false);
                for(int j = 0; j < orderReceiptLinks.size(); j++)
                    importOrderDetailService.updateSoldQuantity(orderReceiptLinks.get(j).getImportOrderDetail().getId(), orderReceiptLinks.get(j).getQuantity() , false);
        
            }  
        }
        customerOrderInDB.setStatus(status);
        customerOrderRepository.persist(customerOrderInDB);
        if(status == -1){
            List<OrderDetail> orderDetails = orderDetailService.findByOrderId(customerOrderInDB.getId());
            for(int i = 0; i < orderDetails.size(); i++){
                List<OrderReceiptLink> orderReceiptLinks = orderReceiptLinkService.findAllByOrderDetailId(orderDetails.get(i).getId());
                Product product = productService.findById(orderReceiptLinks.get(0).getImportOrderDetail().getProduct().getId());
                productService.updateQuantity(product.getId(), orderDetails.get(i).getQuantity(), true);
                for(int j = 0; j < orderReceiptLinks.size(); j++)
                    importOrderDetailService.updateSoldQuantity(orderReceiptLinks.get(j).getImportOrderDetail().getId(), orderReceiptLinks.get(j).getQuantity() , true);
        
            }  
        }
    }

    @Transactional
    public void cancelById(Long id, Integer status) {

        CustomerOrder customerOrderInDB = customerOrderRepository.findById(id);
        if(customerOrderInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
        if(status == -1){
            customerOrderInDB.setStatus(status);
            customerOrderRepository.persist(customerOrderInDB);

            List<OrderDetail> orderDetails = orderDetailService.findByOrderId(customerOrderInDB.getId());
            for(int i = 0; i < orderDetails.size(); i++){
                List<OrderReceiptLink> orderReceiptLinks = orderReceiptLinkService.findAllByOrderDetailId(orderDetails.get(i).getId());
                Product product = productService.findById(orderReceiptLinks.get(0).getImportOrderDetail().getProduct().getId());
                productService.updateQuantity(product.getId(), orderDetails.get(i).getQuantity(), true);
                for(int j = 0; j < orderReceiptLinks.size(); j++)
                    importOrderDetailService.updateSoldQuantity(orderReceiptLinks.get(j).getImportOrderDetail().getId(), orderReceiptLinks.get(j).getQuantity() , true);
        
            }  
        }  
    }

    @Transactional
    public void updateVNPayStatus(Long customerOrderId, Integer status) {
        
        CustomerOrder customerOrderInDB = customerOrderRepository.findById(customerOrderId);
        if(customerOrderId != null) {
            if(customerOrderInDB.getStatus() == 3 && status == 1){
                customerOrderInDB.setStatus(0);
                customerOrderRepository.persist(customerOrderInDB);
            }
            else if(customerOrderInDB.getStatus() == 3 && status == 0){
                customerOrderInDB.setStatus(-1);
                customerOrderRepository.persist(customerOrderInDB);
            }
        }
        else{
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
        }
        
    }

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

    public Long statisticProfit(Long id) {
        CustomerOrder customerOrder = customerOrderRepository.findById(id);

        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(id);

        Integer importPrice = 0;

        for(OrderDetail orderDetail : orderDetails){
            List<OrderReceiptLink> orderReceiptLinks = orderReceiptLinkService.findAllByOrderDetailId(orderDetail.getId());
            for(OrderReceiptLink orderReceiptLink : orderReceiptLinks){
                ImportOrderDetail importOrderDetail = importOrderDetailService.findById(orderReceiptLink.getImportOrderDetail().getId());
                importPrice += importOrderDetail.getPrice() * orderReceiptLink.getQuantity();
            }
        }

        return customerOrder.getTotal() - importPrice;
    }
}
