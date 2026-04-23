package com.westminster.smartcampusapi.resource;

import com.westminster.smartcampusapi.exception.LinkedResourceNotFoundException;
import com.westminster.smartcampusapi.model.Room;
import com.westminster.smartcampusapi.model.Sensor;
import com.westminster.smartcampusapi.store.DataStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = new ArrayList<>(DataStore.SENSORS.values());

        if (type == null || type.trim().isEmpty()) {
            return sensors;
        }

        return sensors.stream()
                .filter(sensor -> sensor.getType() != null && sensor.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor ID is required.")
                    .build();
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("roomId is required.")
                    .build();
        }

        Room room = DataStore.ROOMS.get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("Cannot create sensor. Room with ID " + sensor.getRoomId() + " does not exist.");
        }

        DataStore.SENSORS.put(sensor.getId(), sensor);

        if (!room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }

        URI uri = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(uri).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.SENSORS.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        return Response.ok(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.SENSORS.get(sensorId);

        if (sensor == null) {
            throw new LinkedResourceNotFoundException("Sensor with ID " + sensorId + " does not exist.");
        }

        return new SensorReadingResource(sensorId);
    }
}