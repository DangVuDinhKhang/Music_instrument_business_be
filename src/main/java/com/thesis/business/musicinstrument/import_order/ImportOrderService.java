package com.thesis.business.musicinstrument.import_order;

import java.time.LocalDate;
import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.account.AccountService;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetail;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetailService;
import com.thesis.business.musicinstrument.order.CustomerOrder;
import com.thesis.business.musicinstrument.order.CustomerOrderRepository;
import com.thesis.business.musicinstrument.order.CustomerOrderRequest;
import com.thesis.business.musicinstrument.orderDetail.OrderDetail;
import com.thesis.business.musicinstrument.orderDetail.OrderDetailService;
import com.thesis.business.musicinstrument.payment.PaymentService;
import com.thesis.business.musicinstrument.product.Product;
import com.thesis.business.musicinstrument.product.ProductService;
import com.thesis.business.musicinstrument.supplier.SupplierService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class ImportOrderService {
    
    @Inject
    ImportOrderRepository importOrderRepository;

    @Inject
    AccountService accountService;

    @Inject
    SupplierService supplierService;

    @Inject
    ImportOrderDetailService importOrderDetailService;

    @Inject
    ProductService productService;

    @Transactional
    public Long add(ImportOrderRequest importOrderRequest, String username, String role) {

        if(accountService.findById(importOrderRequest.getAccount().getId(), username, role) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");
        if(supplierService.findById(importOrderRequest.getSupplier().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Supplier does not exist");
        
        ImportOrder importOrder = new ImportOrder();
        importOrder.setDate(LocalDate.now());
        importOrder.setTotal(importOrderRequest.getTotal());
        importOrder.setNote(importOrderRequest.getNote());
        importOrder.setAccount(importOrderRequest.getAccount());
        importOrder.setSupplier(importOrderRequest.getSupplier());
        importOrder.setStatus(0);

        importOrderRepository.persist(importOrder);

        for(int i = 0; i < importOrderRequest.getProductsInCartDTO().size(); i++){
            ImportOrderDetail importOrderDetail = new ImportOrderDetail();
            importOrderDetail.setProduct(new Product(importOrderRequest.getProductsInCartDTO().get(i).getProduct().getId()));
            importOrderDetail.setQuantity(importOrderRequest.getProductsInCartDTO().get(i).getQuantity());
            importOrderDetail.setTotal(
                (long)importOrderRequest.getProductsInCartDTO().get(i).getQuantity() *
                (long)importOrderRequest.getProductsInCartDTO().get(i).getProduct().getPrice()
            );
            importOrderDetail.setImportOrder(new ImportOrder(importOrder.getId()));
            importOrderDetailService.add(importOrderDetail);
            //productService.updateQuantity(importOrderDetail.getProduct().getId(), importOrderDetail.getQuantity(), false);
        }
        
            
        return importOrder.getId();
    }

    public List<ImportOrder> findAll() {

        return importOrderRepository.listAll();
    }

    public ImportOrder findById(Long id){

        return importOrderRepository.findById(id);
    }

    // public List<CustomerOrder> findByAccountId(Long accountId){

    //     return customerOrderRepository.list("account.id", accountId);
    // }

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

    // @Transactional
    // public void deleteById(Long id){
        
    //     if(customerOrderRepository.deleteById(id))
    //         return;
    //     else
    //         throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
    // }
}
