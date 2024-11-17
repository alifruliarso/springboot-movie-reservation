package com.galapea.techblog.moviereservation.entity;

import com.toshiba.mwcloud.gs.RowKey;
import lombok.Data;

@Data
public class Seat {
    @RowKey
    private String id;

    private String status;

    private String showId;

    private String seatNumber;
}
