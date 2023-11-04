package com.thesis.business.musicinstrument.import_order_detail;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/import-order-detail")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImportOrderDetailResource {
    @Inject
    ImportOrderDetailService importOrderDetailService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UriInfo uriInfo;


    @GET
    @Path("/{importOrderId}")
    public Response findByImportOrderId(@PathParam("importOrderId") Long importOrderId){
        
        List<ImportOrderDetail> importOrderDetails = importOrderDetailService.findByImportOrderId(importOrderId);
        return Response.status(Response.Status.OK).entity(importOrderDetails).build();
    }

}
