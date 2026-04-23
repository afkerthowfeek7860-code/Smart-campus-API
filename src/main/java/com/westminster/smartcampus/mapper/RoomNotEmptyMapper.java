package com.westminster.smartcampusapi.mapper;

import com.westminster.smartcampusapi.exception.RoomNotEmptyException;
import com.westminster.smartcampusapi.model.ApiError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        ApiError error = new ApiError(
                409,
                "RoomNotEmpty",
                ex.getMessage()
        );

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}