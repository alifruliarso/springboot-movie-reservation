package com.galapea.techblog.moviereservation.entity;

import java.util.Date;
import com.toshiba.mwcloud.gs.RowKey;
import lombok.Data;

@Data
public class Show {
    @RowKey
    private String id;

    private String movieId;

    private Date startTime;

    private Date endTime;

    private Double price;

    private Integer totalSeats;
}
