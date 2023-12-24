package entity;

import java.time.LocalDateTime;

public record Flight(Long id,
                     Integer aircraftId,
                     String arrivalAirportCode,
                     LocalDateTime arrivalDate,
                     String departureAirportCode,
                     LocalDateTime departureDate,
                     String flightNo,
                     String status) {
}
