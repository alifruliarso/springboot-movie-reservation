package com.galapea.techblog.moviereservation.entity;

import java.util.Date;
import com.toshiba.mwcloud.gs.RowKey;
import lombok.Data;

@Data
public class Reservation {
    @RowKey
    private String id;

    private String userId;

    private String showId;

    private Double totalPrice;

    private Integer numberOfSeats;

    private String[] seats;

    Date createdAt;
}
