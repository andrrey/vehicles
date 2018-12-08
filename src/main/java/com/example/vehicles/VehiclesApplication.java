package com.example.vehicles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class VehiclesApplication {
	private static final Logger log = LoggerFactory.getLogger(VehiclesApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(VehiclesApplication.class, args);
	}

	@Bean
	public CommandLineRunner task(VehiclesRepo repo){
		Random rnd = new Random();
		VehicleType vehicleTypes[] = VehicleType.values();

		return args -> {
			for(int i=0; i<1000; i++){
				VehicleType type = vehicleTypes[rnd.nextInt(vehicleTypes.length)];
				repo.save(new Vehicle(type));
			}


			log.info("Database:");
			for(Vehicle vehicle: repo.findAll()){
				log.info(vehicle.toString());
			}
		};
	}
}
