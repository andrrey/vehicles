package com.example.vehicles;

import java.io.Serializable;
import java.util.UUID;

public class Msg implements Serializable {
    private final String msgid = UUID.randomUUID().toString();

    private Event event;
    private long vehicleId;
    private long collisionId;
    private VehicleType type;

    public Msg() {
    }

    public String getMsgid() {
        return msgid;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public Msg(Event event, long vehicleId, long collisionId) {
        this.event = event;
        this.vehicleId = vehicleId;
        this.collisionId = collisionId;
    }

    public Msg(Event event, VehicleType type) {
        this.event = event;
        this.type = type;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public long getCollisionId() {
        return collisionId;
    }

    public void setCollisionId(long collisionId) {
        this.collisionId = collisionId;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "msgid='" + msgid + '\'' +
                ", event=" + event +
                ", vehicleId=" + vehicleId +
                ", collisionId=" + collisionId +
                ", type=" + type +
                '}';
    }

}
