package com.example.rental_car;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CarController {
    private final List<Car> cars = new ArrayList<>();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(java.time.format.ResolverStyle.STRICT);

    public CarController() {
        cars.add(new Car("11AA22", "Ferrari", 100));
        cars.add(new Car("AA11BB", "Renault", 50));
        cars.add(new Car("CC33DD", "Peugeot", 60));
    }

    @GetMapping("/cars")
    public List<Car> listOfCars() {
        return cars.stream().filter(car -> !car.isRented()).toList();
    }

    @GetMapping("/cars/{plateNumber}")
    public Car aCar(@PathVariable String plateNumber) {
        return findCar(plateNumber);
    }

    @PutMapping("/cars/{plateNumber}")
    public void rentOrGetBack(@PathVariable String plateNumber,
                              @RequestParam boolean rent,
                              @RequestBody(required = false) Dates dates) {
        Car car = findCar(plateNumber);
        if (rent) {
            if (car.isRented()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Car already rented");
            }
            if (dates == null || dates.getBegin() == null || dates.getEnd() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Begin and end dates required");
            }
            try {
                LocalDate begin = LocalDate.parse(dates.getBegin(), DATE_FORMAT);
                LocalDate end = LocalDate.parse(dates.getEnd(), DATE_FORMAT);
                if (end.isBefore(begin)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End before begin");
                }
            } catch (DateTimeParseException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date (d/M/yyyy)");
            }
            car.setRented(true);
        } else {
            if (!car.isRented()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Car not rented");
            }
            car.setRented(false);
        }
    }

    private Car findCar(String plateNumber) {
        return cars.stream().filter(car -> car.getPlateNumber().equals(plateNumber))
                .findFirst().orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
    }
}
