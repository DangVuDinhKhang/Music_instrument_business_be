package com.thesis.business.musicinstrument;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class MusicInstrumentException extends WebApplicationException {

    public MusicInstrumentException(Response.Status status, String message) {
        super(Response.status(status).entity(new ErrorMessage(message)).build());
    }
}
 