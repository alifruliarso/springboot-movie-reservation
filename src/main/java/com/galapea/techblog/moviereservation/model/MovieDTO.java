package com.galapea.techblog.moviereservation.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MovieDTO {

    private String id;

    @NotNull
    @Size(max = 255)
    @MovieTitleUnique
    private String title;

    @Size(max = 255)
    private String genre;

}
