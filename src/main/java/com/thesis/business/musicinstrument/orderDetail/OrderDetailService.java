package com.thesis.business.musicinstrument.orderDetail;

import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.order.CustomerOrder;
import com.thesis.business.musicinstrument.order.CustomerOrderService;
import com.thesis.business.musicinstrument.product.ProductService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class OrderDetailService {

    @Inject
    OrderDetailRepository orderDetailRepository;

    @Inject
    ProductService productService;

    @Inject
    CustomerOrderService customerOrderService;

    @Transactional
    public Long add(OrderDetail orderDetail) {

        if(productService.findById(orderDetail.getProduct().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
        if(customerOrderService.findById(orderDetail.getCustomerOrder().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");

        orderDetailRepository.persist(orderDetail);
        return orderDetail.getId();
    }

    public List<OrderDetail> findByOrderId(Long orderId) {

        List<OrderDetail> orderDetails = orderDetailRepository.list("customerOrder.id", orderId);
        if(orderDetails.isEmpty())
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
        return orderDetails;
    }   

    public OrderDetail findByProductIdAndListOfOrder(Long productId, List<CustomerOrder> customerOrders){

        // List<OrderDetail> orderDetails = orderDetailRepository.list("product.id", productId);
        // if(orderDetails.isEmpty())
        //     throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
        // return orderDetails;

        OrderDetail orderDetails;
        for(CustomerOrder customerOrder : customerOrders){
            orderDetails = orderDetailRepository.find("product.id = ?1 and customerOrder.id = ?2", productId, customerOrder.getId()).firstResult();
            if(orderDetails != null)
                return orderDetails;
        }
        return null;

    }
    
    public OrderDetail findById(Long id){
        return orderDetailRepository.findById(id);
    }

    @Transactional
    public void updateById(Long id, OrderDetail orderDetail) {

        OrderDetail orderDetailInDB = orderDetailRepository.findById(id);
        if(orderDetailInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Detail of order does not exist");

        orderDetailInDB.setAmount(orderDetail.getAmount());
        orderDetailInDB.setTotal(orderDetail.getTotal());
        orderDetailRepository.persist(orderDetailInDB);
    }

    @Transactional
    public void deleteById(Long id){
        
        if(orderDetailRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Detail of order does not exist");
    }

}
