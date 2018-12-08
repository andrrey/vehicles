package com.example.vehicles;

import org.springframework.data.repository.CrudRepository;

public interface VehiclesRepo extends CrudRepository<Vehicle, Long> {
}
