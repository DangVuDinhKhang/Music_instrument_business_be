package com.thesis.business.musicinstrument.supplier;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/supplier")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SupplierResource {

    @Inject
    SupplierService supplierService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UriInfo uriInfo;

    @POST
    @Path("/")
    @RolesAllowed("admin")
    public Response add(Supplier supplier) {

        Long supplierId = supplierService.add(supplier);
        URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(supplierId)).build();
        return Response.status(Response.Status.CREATED).location(location).build();
    }

    @GET
    @Path("/")
    public Response findAll(){
        
        List<Supplier> suppliers = supplierService.findAll();
        return Response.status(Response.Status.OK).entity(suppliers).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response updateById(@PathParam("id") Long id, Supplier supplier){

        supplierService.updateById(id, supplier);

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteById(@PathParam("id") Long id){

        supplierService.deleteById(id);

        return Response.status(Response.Status.OK).build();
    }
}
