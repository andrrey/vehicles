package com.example.vehicles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@SpringBootApplication
@EnableJms
public class VehiclesApplication implements ApplicationContextAware {
	public static final int MAX_VEHICLES = 10;
	private static final Logger log = LoggerFactory.getLogger(VehiclesApplication.class);
	private ApplicationContext ctx;
	@Autowired
	JmsTemplate jmsTemplate;

	public static void main(String[] args) {
		SpringApplication.run(VehiclesApplication.class, args);
	}

	@Bean
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
													DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setPubSubDomain(true);
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	@Bean
	public CommandLineRunner task(VehiclesRepo repo){

		return args -> {
			for(int i=0; i <MAX_VEHICLES; i++){
				repo.save(ctx.getBean(Vehicle.class)); //beans will be created automatically, as they have prototype scope
			}

			log.info("Database:");
			for(Vehicle vehicle: repo.findAll()){
				log.info(vehicle.toString());
			}

			//jmsTemplate.setPubSubDomain(true);
			jmsTemplate.convertAndSend("topic", new Msg(Event.Move, -1, 0));
			jmsTemplate.convertAndSend("topic", new Msg(Event.Move, -1, 0));
		};
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

	@JmsListener(destination = "systemQ", containerFactory = "myFactory")
	public void listen(Msg msg){
		log.info("System got event: " + msg.toString());
		if(msg.getEvent() == Event.Incident){
			jmsTemplate.convertAndSend("topic",
					new Msg(Event.Incident, msg.getCollisionId(), msg.getVehicleId()));
		}

		if(msg.getEvent() == Event.Alert){
			jmsTemplate.convertAndSend("topic", new Msg(Event.Alert, msg.getType()));
		}
	}
}
