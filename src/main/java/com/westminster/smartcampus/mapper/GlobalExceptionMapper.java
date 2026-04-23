package com.westminster.smartcampusapi.mapper;

import com.westminster.smartcampusapi.model.ApiError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {
        ex.printStackTrace(); // server-side only

        ApiError error = new ApiError(
                500,
                "InternalServerError",
                "An unexpected error occurred. Please contact the API administrator."
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}