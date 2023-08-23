package com.thesis.business.musicinstrument.payment;

import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class PaymentService {

    @Inject
    PaymentRepository paymentRepository;

    @Transactional
    public Long add(Payment payment) {

        paymentRepository.persist(payment);
        return payment.getId();
    }

    public List<Payment> findAll() {

        return paymentRepository.listAll();
    }

    public Payment findById(Long id){
        
        return paymentRepository.findById(id);
    }

    @Transactional
    public void updateById(Long id, Payment payment) {

        Payment paymentInDB = paymentRepository.findById(id);
        if(paymentInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Payment does not exist");

        paymentInDB.setName(payment.getName());
        paymentRepository.persist(paymentInDB);
    }

    @Transactional
    public void deleteById(Long id){
        
        if(paymentRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Payment does not exist");
    }

}
