package com.thesis.business.musicinstrument.import_order;

import java.time.DayOfWeek;
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
        importOrder.setAccount(importOrderRequest.getAccount());
        importOrder.setSupplier(importOrderRequest.getSupplier());
        importOrder.setStatus(0);

        importOrderRepository.persist(importOrder);

        for(int i = 0; i < importOrderRequest.getProducts().size(); i++){
            ImportOrderDetail importOrderDetail = new ImportOrderDetail();
            importOrderDetail.setProduct(new Product(importOrderRequest.getProducts().get(i).getId()));
            importOrderDetail.setQuantity(importOrderRequest.getProducts().get(i).getQuantity());
            importOrderDetail.setPrice(importOrderRequest.getProducts().get(i).getPrice());
            importOrderDetail.setTotal(
                (long)importOrderRequest.getProducts().get(i).getQuantity() *
                (long)importOrderRequest.getProducts().get(i).getPrice()
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

    @Transactional
    public void updateById(Long id, Integer status) {

        ImportOrder importOrderInDB = importOrderRepository.findById(id);
        if(importOrderInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Import Order does not exist");
        if(status == 1){
            List<ImportOrderDetail> importOrderDetails = importOrderDetailService.findByImportOrderId(importOrderInDB.getId());
            for(int i = 0; i < importOrderDetails.size(); i++)
                productService.updateQuantity(importOrderDetails.get(i).getProduct().getId(), importOrderDetails.get(i).getQuantity(), true);
        }
        
        else if(status == 0){
            List<ImportOrderDetail> importOrderDetails = importOrderDetailService.findByImportOrderId(importOrderInDB.getId());
            for(int i = 0; i < importOrderDetails.size(); i++)
                productService.updateQuantity(importOrderDetails.get(i).getProduct().getId(), importOrderDetails.get(i).getQuantity(), false);
        }
        else{
            throw new MusicInstrumentException(Response.Status.BAD_REQUEST, "Wrong status");
        }
        importOrderInDB.setStatus(status);
        importOrderRepository.persist(importOrderInDB);
    }

    public List<ImportOrder> statistic(String type) {
        if(type.equals("month")){
            return importOrderRepository.find("SELECT DATE_TRUNC('month', date) AS month, SUM(total) AS total " +
                "FROM ImportOrder " + "WHERE status = 1" +
                "GROUP BY DATE_TRUNC('month', date)")
                .list();
        }
        else{
            LocalDate currentDate = LocalDate.now();

            // Tính ngày bắt đầu của tuần trước
            LocalDate lastWeekStartDate = currentDate.minusWeeks(1).with(DayOfWeek.MONDAY);

            return importOrderRepository.find("SELECT date, SUM(total) AS total " +
                "FROM ImportOrder " +
                "WHERE date >= ?1 AND date <= ?2 and status = ?3" +
                "GROUP BY date", lastWeekStartDate, currentDate, 1)
                .list();
        }
    }

    public Long statisticTotalSpending(){
        List<ImportOrder> importOrders = importOrderRepository.find("status", 1).list();
        Long totalSpeding  = 0L;
        for(ImportOrder importOrder : importOrders){
            totalSpeding += importOrder.getTotal();
        }
        return totalSpeding;
    }

    // @Transactional
    // public void deleteById(Long id){
        
    //     if(customerOrderRepository.deleteById(id))
    //         return;
    //     else
    //         throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");
    // }
}
