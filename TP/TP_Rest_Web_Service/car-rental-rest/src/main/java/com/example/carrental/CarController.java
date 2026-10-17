package com.example.carrental;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarController {
    private final CarService service;

    public CarController(CarService service) { this.service = service; }

    @GetMapping("/")
    public String hello() { return "hello"; }

    @GetMapping("/cars")
    public List<Car> listOfCars() { return service.availableCars(); }

    @GetMapping("/cars/{plateNumber}")
    public Car aCar(@PathVariable String plateNumber) { return service.getCar(plateNumber); }

    @PutMapping("/cars/{plateNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Car rentOrGetBack(@PathVariable String plateNumber,
                             @RequestParam boolean rent,
                             @RequestBody(required = false) Dates dates) {
        return service.changeRental(plateNumber, rent, dates);
    }
}
