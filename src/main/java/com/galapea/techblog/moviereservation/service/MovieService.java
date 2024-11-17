package com.galapea.techblog.moviereservation.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.galapea.techblog.moviereservation.entity.Movie;
import com.galapea.techblog.moviereservation.model.MovieDTO;
import com.galapea.techblog.moviereservation.util.AppConstant;
import com.galapea.techblog.moviereservation.util.AppErrorException;
import com.galapea.techblog.moviereservation.util.NotFoundException;
import com.toshiba.mwcloud.gs.Collection;
import com.toshiba.mwcloud.gs.GSException;
import com.toshiba.mwcloud.gs.Query;
import com.toshiba.mwcloud.gs.RowSet;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class MovieService {

    private final Collection<String, Movie> movieCollection;

    public MovieService(Collection<String, Movie> movieCollection) {
        this.movieCollection = movieCollection;
    }

    private List<Movie> fetchAll() {
        List<Movie> movies = new ArrayList<>(0);
        try {
            String tql = "SELECT * FROM " + AppConstant.MOVIE_CONTAINER;
            Query<Movie> query = movieCollection.query(tql);
            RowSet<Movie> rowSet = query.fetch();
            while (rowSet.hasNext()) {
                Movie row = rowSet.next();
                movies.add(row);
            }
        } catch (GSException e) {
            log.error("Error fetch all movies", e);
        }
        return movies;
    }

    public List<MovieDTO> findAll() {
        final List<Movie> movies = fetchAll();
        return movies.stream().map(movie -> mapToDTO(movie, new MovieDTO())).toList();
    }

    public MovieDTO get(final String id) {
        try (Query<Movie> query =
                movieCollection.query("SELECT * WHERE id='" + id + "'", Movie.class)) {
            RowSet<Movie> rowSet = query.fetch();
            if (rowSet.hasNext()) {
                return mapToDTO(rowSet.next(), new MovieDTO());
            } else {
                throw new NotFoundException();
            }
        } catch (GSException e) {
            throw new AppErrorException();
        }
    }

    public String create(final MovieDTO movieDTO) {
        if (titleExists(movieDTO.getTitle())) {
            return "";
        }
        final Movie movie = new Movie();
        mapToEntity(movieDTO, movie);
        movie.setId(KeyGenerator.next("mv_"));
        try {
            movieCollection.put(movie);
        } catch (GSException e) {
            log.error("Failed put into Movie collection", e);
            throw new AppErrorException();
        }
        return movie.getId();
    }

    private MovieDTO mapToDTO(final Movie movie, final MovieDTO movieDTO) {
        movieDTO.setId(movie.getId());
        movieDTO.setTitle(movie.getTitle());
        movieDTO.setGenre(movie.getGenre());
        return movieDTO;
    }

    private Movie mapToEntity(final MovieDTO movieDTO, final Movie movie) {
        movie.setTitle(movieDTO.getTitle());
        movie.setGenre(movieDTO.getGenre());
        return movie;
    }

    public boolean idExists(final String id) {
        try (Query<Movie> query =
                movieCollection.query("SELECT * WHERE id='" + id + "'", Movie.class)) {
            RowSet<Movie> rowSet = query.fetch();
            return rowSet.hasNext();
        } catch (GSException e) {
            throw new AppErrorException();
        }
    }

    public boolean titleExists(final String title) {
        try (Query<Movie> query =
                movieCollection.query("SELECT * WHERE title='" + title + "'", Movie.class)) {
            RowSet<Movie> rowSet = query.fetch();
            return rowSet.hasNext();
        } catch (GSException e) {
            throw new AppErrorException();
        }
    }

}
