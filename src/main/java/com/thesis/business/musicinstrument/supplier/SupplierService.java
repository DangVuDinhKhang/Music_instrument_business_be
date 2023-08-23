package com.thesis.business.musicinstrument.supplier;

import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class SupplierService {

    @Inject
    SupplierRepository supplierRepository;

    @Transactional
    public Long add(Supplier supplier) {

        supplierRepository.persist(supplier);
        return supplier.getId();
    }

    public List<Supplier> findAll() {

        return supplierRepository.listAll();
    }

    public Supplier findById(Long id) {
        return supplierRepository.findById(id);
    }

    @Transactional
    public void updateById(Long id, Supplier supplier) {

        Supplier supplierInDB = supplierRepository.findById(id);
        if (supplierInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Supplier does not exist");

        supplierInDB.setName(supplier.getName());
        supplierInDB.setContact(supplier.getContact());
        supplierRepository.persist(supplierInDB);
    }

    @Transactional
    public void deleteById(Long id) {

        if (supplierRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Supplier does not exist");
    }

}
