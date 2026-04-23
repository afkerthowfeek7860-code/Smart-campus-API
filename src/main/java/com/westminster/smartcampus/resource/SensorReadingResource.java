package com.westminster.smartcampusapi.resource;

import com.westminster.smartcampusapi.exception.SensorUnavailableException;
import com.westminster.smartcampusapi.model.Sensor;
import com.westminster.smartcampusapi.model.SensorReading;
import com.westminster.smartcampusapi.store.DataStore;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        return DataStore.getReadingsForSensor(sensorId);
    }

    @POST
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        Sensor sensor = DataStore.SENSORS.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor " + sensorId + " is under maintenance and cannot accept new readings."
            );
        }

        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reading body is required.")
                    .build();
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() == 0L) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        DataStore.getReadingsForSensor(sensorId).add(reading);

        // side effect required by coursework
        sensor.setCurrentValue(reading.getValue());

        URI uri = uriInfo.getAbsolutePathBuilder().path(reading.getId()).build();
        return Response.created(uri).entity(reading).build();
    }
}