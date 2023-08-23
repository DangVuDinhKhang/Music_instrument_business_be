package com.thesis.business.musicinstrument.order;

import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.account.AccountService;
import com.thesis.business.musicinstrument.payment.PaymentService;

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

    @Transactional
    public Long add(CustomerOrder customerOrder, String username, String role) {

        if(accountService.findById(customerOrder.getAccount().getId(), username, role) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");
        if(paymentService.findById(customerOrder.getPayment().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Payment method does not exist");

        customerOrderRepository.persist(customerOrder);
        return customerOrder.getId();
    }

    public List<CustomerOrder> findAll() {

        return customerOrderRepository.listAll();
    }

    @Transactional
    public void updateById(Long id, CustomerOrder customerOrder) {

        CustomerOrder customerOrderInDB = customerOrderRepository.findById(id);
        if(customerOrderInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Order does not exist");

        customerOrderInDB.setName(customerOrderInDB.getName());
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
