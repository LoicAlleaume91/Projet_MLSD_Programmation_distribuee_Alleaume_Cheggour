package com.example.carrental;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CarService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private final Map<String, Car> cars = new LinkedHashMap<>();

    public CarService() {
        add(new Car("11AA22", "Ferrari", 100));
        add(new Car("AA11BB", "Renault", 45));
        add(new Car("BB22CC", "Peugeot", 50));
        add(new Car("CC33DD", "Toyota", 65));
    }

    private void add(Car car) { cars.put(car.getPlateNumber(), car); }

    public synchronized List<Car> availableCars() {
        List<Car> result = new ArrayList<>();
        for (Car car : cars.values()) {
            if (!car.isRented()) result.add(car);
        }
        return result;
    }

    public synchronized Car getCar(String plateNumber) {
        Car car = cars.get(plateNumber);
        if (car == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found: " + plateNumber);
        return car;
    }

    public synchronized Car changeRental(String plateNumber, boolean rent, Dates dates) {
        Car car = getCar(plateNumber);
        if (rent) {
            if (car.isRented()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Car is already rented");
            validateDates(dates);
            car.rent(dates);
        } else {
            if (!car.isRented()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Car is not rented");
            car.returnCar();
        }
        return car;
    }

    private void validateDates(Dates dates) {
        if (dates == null || dates.getBegin() == null || dates.getEnd() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "begin and end are required");
        }
        try {
            LocalDate begin = LocalDate.parse(dates.getBegin(), DATE_FORMAT);
            LocalDate end = LocalDate.parse(dates.getEnd(), DATE_FORMAT);
            if (end.isBefore(begin)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end must not be before begin");
            }
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates must use day/month/year, e.g. 11/11/2017");
        }
    }
}
