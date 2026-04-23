package com.westminster.smartcampusapi.mapper;

import com.westminster.smartcampusapi.exception.LinkedResourceNotFoundException;
import com.westminster.smartcampusapi.model.ApiError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        ApiError error = new ApiError(
                422,
                "LinkedResourceNotFound",
                ex.getMessage()
        );

        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}