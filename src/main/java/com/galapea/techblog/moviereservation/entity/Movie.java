package com.galapea.techblog.moviereservation.entity;

import com.toshiba.mwcloud.gs.RowKey;
import lombok.Data;

@Data
public class Movie {
    @RowKey
    private String id;

    private String title;

    private String genre;
}
