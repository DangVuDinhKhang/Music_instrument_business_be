package com.thesis.business.musicinstrument.import_order_detail;

import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.import_order.ImportOrderService;
import com.thesis.business.musicinstrument.order.CustomerOrder;
import com.thesis.business.musicinstrument.order.CustomerOrderService;
import com.thesis.business.musicinstrument.orderDetail.OrderDetail;
import com.thesis.business.musicinstrument.orderDetail.OrderDetailRepository;
import com.thesis.business.musicinstrument.product.ProductService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class ImportOrderDetailService {

    @Inject
    ImportOrderDetailRepository importOrderDetailRepository;

    @Inject
    ProductService productService;

    @Inject
    ImportOrderService importOrderService;

    @Transactional
    public Long add(ImportOrderDetail importOrderDetail) {

        if(productService.findById(importOrderDetail.getProduct().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
        if(importOrderService.findById(importOrderDetail.getImportOrder().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Import Order does not exist");

        importOrderDetailRepository.persist(importOrderDetail);
        return importOrderDetail.getId();
    }

    public List<ImportOrderDetail> findByImportOrderId(Long importOrderId) {

        List<ImportOrderDetail> importOrderDetails = importOrderDetailRepository.list("importOrder.id", importOrderId);
        if(importOrderDetails.isEmpty())
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Import Order does not exist");
        return importOrderDetails;
    }   

    public ImportOrderDetail findOneByProductId(Long productId) {
        return importOrderDetailRepository.find("product.id = ?1  order by price asc", productId).firstResult();
    }

    public List<ImportOrderDetail> findByProductId(Long productId) {
        return importOrderDetailRepository.find("product.id = ?1  order by price asc", productId).list();
    }

    public void updateSoldQuantity(Long id, Integer hasPurchasedQuantity, Boolean canceled) {

        ImportOrderDetail importOrderDetail = importOrderDetailRepository.findById(id);
        if(canceled)
            importOrderDetail.setSoldQuantity(importOrderDetail.getSoldQuantity() - hasPurchasedQuantity);
        else
            importOrderDetail.setSoldQuantity(importOrderDetail.getSoldQuantity() + hasPurchasedQuantity);

        importOrderDetailRepository.persist(importOrderDetail);
    }

    // public OrderDetail findByProductIdAndListOfOrder(Long productId, List<CustomerOrder> customerOrders){

    //     // List<OrderDetail> orderDetails = orderDetailRepository.list("product.id", productId);
    //     // if(orderDetails.isEmpty())
    //     //     throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
    //     // return orderDetails;

    //     OrderDetail orderDetails;
    //     for(CustomerOrder customerOrder : customerOrders){
    //         orderDetails = orderDetailRepository.find("product.id = ?1 and customerOrder.id = ?2", productId, customerOrder.getId()).firstResult();
    //         if(orderDetails != null)
    //             return orderDetails;
    //     }
    //     return null;

    // }
    
    public ImportOrderDetail findById(Long id){
        return importOrderDetailRepository.findById(id);
    }

    // @Transactional
    // public void updateById(Long id, OrderDetail orderDetail) {

    //     OrderDetail orderDetailInDB = orderDetailRepository.findById(id);
    //     if(orderDetailInDB == null)
    //         throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Detail of order does not exist");

    //     orderDetailInDB.setQuantity(orderDetail.getQuantity());
    //     orderDetailInDB.setTotal(orderDetail.getTotal());
    //     orderDetailRepository.persist(orderDetailInDB);
    // }

    // @Transactional
    // public void deleteById(Long id){
        
    //     if(orderDetailRepository.deleteById(id))
    //         return;
    //     else
    //         throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Detail of order does not exist");
    // }
}
