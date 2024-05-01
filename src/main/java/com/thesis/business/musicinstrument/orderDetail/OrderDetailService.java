package com.thesis.business.musicinstrument.orderDetail;

import java.util.ArrayList;
import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetailService;
import com.thesis.business.musicinstrument.order.CustomerOrder;
import com.thesis.business.musicinstrument.order.CustomerOrderService;
import com.thesis.business.musicinstrument.order_receipt_link.OrderReceiptLink;
import com.thesis.business.musicinstrument.order_receipt_link.OrderReceiptLinkService;
import com.thesis.business.musicinstrument.product.Product;
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
    ImportOrderDetailService importOrderDetailService;

    @Inject
    CustomerOrderService customerOrderService;

    @Inject
    OrderReceiptLinkService orderReceiptLinkService;

    @Transactional
    public Long add(OrderDetail orderDetail) {

        // if(importOrderDetailService.findById(orderDetail.getImportOrderDetail().getId()) == null)
        //     throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Import order detail does not exist");
        if(customerOrderService.findById(orderDetail.getCustomerOrder().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");

        orderDetailRepository.persist(orderDetail);
        return orderDetail.getId();
    }

    public List<OrderDetailDto> findByOrderIdConvertDto(Long orderId) {

        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();

        List<OrderDetail> orderDetails = orderDetailRepository.list("customerOrder.id", orderId);
        if(orderDetails.isEmpty())
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
        for(OrderDetail orderDetail : orderDetails) {
            OrderReceiptLink orderReceiptLink = orderReceiptLinkService.findOneByOrderDetailId(orderDetail.getId());
            Product product = productService.findById(orderReceiptLink.getImportOrderDetail().getProduct().getId());
            orderDetailDtos.add(this.convertEntityIntoDto(orderDetail, product));
        }
        return orderDetailDtos;
    }   

    public List<OrderDetail> findByOrderId(Long orderId) {

        List<OrderDetail> orderDetails = orderDetailRepository.list("customerOrder.id", orderId);
        if(orderDetails.isEmpty())
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
        return orderDetails;
    }   

    public List<OrderDetail> findByProductIdAndListOfOrder(Long productId, List<CustomerOrder> customerOrders){


        List<OrderDetail> orderDetails;
        for(CustomerOrder customerOrder : customerOrders){
            orderDetails = orderDetailRepository.find("product.id = ?1 and customerOrder.id = ?2", productId, customerOrder.getId()).list();
            if(orderDetails != null)
                return orderDetails;
        }
        return null;

    }
    
    public OrderDetail findById(Long id){
        return orderDetailRepository.findById(id);
    }

    public List<OrderDetail> findTopThreeProducts(){
        return orderDetailRepository.find("SELECT product.id, COUNT(*) as count " + 
        "FROM OrderDetail " + "GROUP BY product.id " + "ORDER BY count DESC").list();
    }

    @Transactional
    public void updateById(Long id, OrderDetail orderDetail) {

        OrderDetail orderDetailInDB = orderDetailRepository.findById(id);
        if(orderDetailInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Detail of order does not exist");

        orderDetailInDB.setQuantity(orderDetail.getQuantity());
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

    private OrderDetailDto convertEntityIntoDto(OrderDetail orderDetail, Product product) {
        
        OrderDetailDto orderDetailDto = new OrderDetailDto();
        orderDetailDto.setId(orderDetail.getId());
        orderDetailDto.setCustomerOrder(orderDetail.getCustomerOrder());
        orderDetailDto.setProduct(product);
        orderDetailDto.setQuantity(orderDetail.getQuantity());
        orderDetailDto.setTotal(orderDetail.getTotal());

        return orderDetailDto;
    }

}
