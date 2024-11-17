package com.galapea.techblog.moviereservation.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SeatDTO {
    private String id;

    private String status;

    private String showId;

    private String seatNumber;

    private Boolean available;
}
