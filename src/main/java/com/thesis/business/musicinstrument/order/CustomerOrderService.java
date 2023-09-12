package com.thesis.business.musicinstrument.order;

import java.time.LocalDate;
import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.account.AccountService;
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
        customerOrder.setAccount(customerOrderRequest.getAccount());
        customerOrder.setPayment(customerOrderRequest.getPayment());
        customerOrder.setStatus(false);

        customerOrderRepository.persist(customerOrder);

        for(int i = 0; i < customerOrderRequest.getProductsInCartDTO().size(); i++){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(new Product(customerOrderRequest.getProductsInCartDTO().get(i).getProduct().getId()));
            orderDetail.setQuantity(customerOrderRequest.getProductsInCartDTO().get(i).getQuantity());
            orderDetail.setTotal(
                (long)customerOrderRequest.getProductsInCartDTO().get(i).getQuantity() *
                (long)customerOrderRequest.getProductsInCartDTO().get(i).getProduct().getPrice()
            );
            orderDetail.setCustomerOrder(new CustomerOrder(customerOrder.getId()));
            orderDetailService.add(orderDetail);
            productService.updateQuantityAfterPurchase(orderDetail.getProduct().getId(), orderDetail.getQuantity());
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

        return customerOrderRepository.list("account.id", accountId);
    }

    @Transactional
    public void updateById(Long id, Boolean status) {


        CustomerOrder customerOrderInDB = customerOrderRepository.findById(id);
        if(customerOrderInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");

        customerOrderInDB.setStatus(status);
        customerOrderRepository.persist(customerOrderInDB);
    }

    @Transactional
    public void deleteById(Long id){
        
        if(customerOrderRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
    }

}
