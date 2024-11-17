package com.galapea.techblog.moviereservation.model;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ReservationDTO {

    private String id;

    @NotNull
    @Size(max = 255)
    private String userId;

    @NotNull
    @Size(max = 255)
    private String showId;

    private BigDecimal totalPrice;

    private Integer numberOfSeats;

    private ShowDTO show;

    private UserDTO user;

    private MovieDTO movie;

    private List<String> seats;

    private List<String> seatsNumber;

}
