package com.westminster.smartcampusapi.store;

import com.westminster.smartcampusapi.model.Room;
import com.westminster.smartcampusapi.model.Sensor;
import com.westminster.smartcampusapi.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {

    public static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();
    public static final Map<String, List<SensorReading>> READINGS = new ConcurrentHashMap<>();

    private DataStore() {
    }

    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        return READINGS.computeIfAbsent(sensorId, k -> new ArrayList<>());
    }
}