package com.galapea.techblog.moviereservation.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    // @Size(max = 50)
    // @UserIdValid
    private String id;

    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 50)
    @UserEmailUnique
    private String email;

}
