package com.galapea.techblog.moviereservation.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.galapea.techblog.moviereservation.model.MovieDTO;
import com.galapea.techblog.moviereservation.model.ShowDTO;
import com.galapea.techblog.moviereservation.model.UserDTO;
import com.galapea.techblog.moviereservation.service.KeyGenerator;
import com.galapea.techblog.moviereservation.service.MovieService;
import com.galapea.techblog.moviereservation.service.ShowService;
import com.galapea.techblog.moviereservation.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppSeeder implements CommandLineRunner {
    private final MovieService movieService;
    private final ShowService showService;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        seedMovies();
        seedShows();
        seedUser();
    }

    private void seedUser() {
        UserDTO user = new UserDTO();
        user.setId(KeyGenerator.next("usr_"));
        user.setEmail("john.d@gmail.com");
        user.setName("John D");
        userService.create(user);
        user = new UserDTO();
        user.setId(KeyGenerator.next("usr_"));
        user.setEmail("kevin.d@gmail.com");
        user.setName("Kevin D");
        userService.create(user);
        new UserDTO();
        user.setId(KeyGenerator.next("usr_"));
        user.setEmail("t.collin@gmail.com");
        user.setName("T C");
        userService.create(user);
    }

    private void seedShows() {
        List<MovieDTO> allMovies = movieService.findAll().stream().collect(Collectors.toList());
        Collections.shuffle(allMovies);
        ShowDTO show = new ShowDTO();
        show.setId(KeyGenerator.next("sh_"));
        show.setPrice(BigDecimal.valueOf(2));
        show.setStartTime(LocalDateTime.now().plusHours(1));
        show.setEndTime(LocalDateTime.now().plusHours(2));
        show.setTotalSeats(77);
        show.setMovieId(allMovies.get(0).getId());
        showService.create(show);
        show = new ShowDTO();
        show.setId(KeyGenerator.next("sh_"));
        show.setPrice(BigDecimal.valueOf(3));
        show.setStartTime(LocalDateTime.now().plusHours(2).plusMinutes(30));
        show.setEndTime(LocalDateTime.now().plusHours(3).plusMinutes(30));
        show.setTotalSeats(86);
        Collections.shuffle(allMovies);
        show.setMovieId(allMovies.get(0).getId());
        showService.create(show);
        show = new ShowDTO();
        show.setId(KeyGenerator.next("sh_"));
        show.setPrice(BigDecimal.valueOf(3.5));
        show.setStartTime(LocalDateTime.now().plusHours(2).plusMinutes(30));
        show.setEndTime(LocalDateTime.now().plusHours(3).plusMinutes(30));
        show.setTotalSeats(86);
        Collections.shuffle(allMovies);
        show.setMovieId(allMovies.get(0).getId());
        showService.create(show);
    }

    private void seedMovies() {
        MovieDTO movie = new MovieDTO();
        movie.setId(KeyGenerator.next("mv_"));
        movie.setTitle("Civil War");
        movie.setGenre("Action");
        movieService.create(movie);
        movie = new MovieDTO();
        movie.setId(KeyGenerator.next("mv_"));
        movie.setTitle("Inception");
        movie.setGenre("Action");
        movieService.create(movie);
        movie = new MovieDTO();
        movie.setId(KeyGenerator.next("mv_"));
        movie.setTitle("Monster Horror");
        movie.setGenre("Horror");
        movieService.create(movie);
        movie = new MovieDTO();
        movie.setId(KeyGenerator.next("mv_"));
        movie.setTitle("Slasher HoRROR");
        movie.setGenre("Horror");
        movieService.create(movie);
        movie = new MovieDTO();
        movie.setId(KeyGenerator.next("mv_"));
        movie.setTitle("Agatha All Along");
        movie.setGenre("Sci-Fi");
        movieService.create(movie);
        movie = new MovieDTO();
        movie.setId(KeyGenerator.next("mv_"));
        movie.setTitle("Subservience");
        movie.setGenre("Sci-Fi");
        movieService.create(movie);
    }

}
