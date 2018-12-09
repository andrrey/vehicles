package com.example.vehicles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Random;

import static com.example.vehicles.VehiclesApplication.MAX_VEHICLES;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Entity
public class Vehicle implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(Vehicle.class);
    protected static final Random rnd = new Random();
    private static final VehicleType vehicleTypes[] = VehicleType.values();
    @Transient
    private ApplicationContext ctx;
    @Transient
    @Autowired
    JmsTemplate jmsTemplate;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private float fuelLevel;
    private final VehicleType type;
    private final float consumption5s;
    private float fullTank;

    protected Vehicle(){
        this(vehicleTypes[rnd.nextInt(vehicleTypes.length)]);
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
            default:
                throw new Error("I didn't expect that");
        }

        this.fuelLevel = this.fullTank;
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
        log.info("Alert in " + toString());
        this.fullTank = this.fullTank * 0.97f;
    }

    public void onIncident() {
        log.info("Incident in " + toString());
        this.fuelLevel = 0;
    }

    public void onMove(){
        log.info("Moving " + toString());
        Event e = drive();
        if(e != Event.Move){
            log.info("Happened: " + e.toString());
            if(e == Event.Incident) onIncident();
            sendMessage(e);
        }
    }

    void sendMessage(Event e){
        Msg m = new Msg(e, this.id, 0);
        if(e == Event.Incident) m.setCollisionId(rnd.nextInt(MAX_VEHICLES));
        JmsTemplate topicTemplate = jmsTemplate; //ctx.getBean("myQueueTemplate", JmsTemplate.class);
        topicTemplate.convertAndSend("systemQ", m);
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

    @JmsListener(destination = "topic", containerFactory = "myFactory")
    public void listen(Msg msg){
        log.info("I am " + toString() + " and I got event: " + msg.toString());
        switch (msg.getEvent()){
            case Move:
                onMove(); break;
            case Alert:
                if(msg.getType() == type) onAlert();
                break;
            case Incident:
                if(msg.getCollisionId() == id) onIncident();
                break;
            default: throw new Error("Whaaaat?!");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
