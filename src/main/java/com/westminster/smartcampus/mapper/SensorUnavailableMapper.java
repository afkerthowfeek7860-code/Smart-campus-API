package com.westminster.smartcampusapi.mapper;

import com.westminster.smartcampusapi.exception.SensorUnavailableException;
import com.westminster.smartcampusapi.model.ApiError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        ApiError error = new ApiError(
                403,
                "SensorUnavailable",
                ex.getMessage()
        );

        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}