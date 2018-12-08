package com.example.vehicles;

import com.sun.javaws.exceptions.ErrorCodeResponseException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Random;

@Entity
public class Vehicle {
    protected static final Random rnd = new Random();

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private float fuelLevel;
    private final VehicleType type;
    private final float consumption5s;
    private float fullTank;

    protected Vehicle(){
        this.type = VehicleType.None;
        this.consumption5s = 0f;
        this.fullTank = 0f;
    }

    public Vehicle(VehicleType type) {
        this.fuelLevel = this.getFullTank();
        this.type = type;

        switch (type){
            case Car:
                this.consumption5s = 0.1f;
                this.fullTank = 40f;
                break;
            case Bus:
                this.consumption5s = 0.5f;
                this.fullTank = 100f;
                break;
            case Truck:
                this.consumption5s = 0.7f;
                this.fullTank = 130f;
                break;
            case None:
            default:
                throw new Error("I didn't expect that");
        }
    }

    public float getFuelLevel() {
        return fuelLevel;
    }

    public float getFullTank(){
        return fullTank;
    }

    public float get5sConsumption(){
        return consumption5s;
    }

    public Event drive(){
        if(getFuelLevel() > 0){
            step();
            return getNextEvent();
        }

        else return null;
    }

    private Event getNextEvent(){
            //TODO: Looks like not very good solution as values() will create new array every call
            return Event.values()[rnd.nextInt(Event.values().length)];
    }

    private void step(){
        if(fuelLevel > 0) fuelLevel -= get5sConsumption();
    }

    public void onAlert(){
        this.fullTank = this.fullTank * 0.97f;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", fuelLevel=" + fuelLevel +
                ", type=" + type +
                ", consumption5s=" + consumption5s +
                ", fullTank=" + fullTank +
                '}';
    }
}
