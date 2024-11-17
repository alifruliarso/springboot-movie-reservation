package com.galapea.techblog.moviereservation.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import com.galapea.techblog.moviereservation.entity.Seat;
import com.galapea.techblog.moviereservation.entity.Show;
import com.galapea.techblog.moviereservation.model.SeatDTO;
import com.galapea.techblog.moviereservation.model.ShowDTO;
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
public class ShowService {
    private final Collection<String, Show> showCollection;
    private final Collection<String, Seat> seatCollection;

    public ShowService(Collection<String, Show> showCollection,
            Collection<String, Seat> seatCollection) {
        this.showCollection = showCollection;
        this.seatCollection = seatCollection;
    }


    private List<Show> fetchAll() {
        List<Show> shows = new ArrayList<>(0);
        try {
            String tql = "SELECT * FROM " + AppConstant.SHOW_CONTAINER;
            Query<Show> query = showCollection.query(tql);
            RowSet<Show> rowSet = query.fetch();
            while (rowSet.hasNext()) {
                Show row = rowSet.next();
                shows.add(row);
            }
        } catch (GSException e) {
            log.error("Error fetch all shows", e);
        }
        return shows;
    }

    public List<ShowDTO> findAll() {
        final List<Show> shows = fetchAll();
        return shows.stream().map(show -> mapToDTO(show, new ShowDTO())).toList();
    }

    private List<Show> fetchAllByMovieId(String movieId) {
        List<Show> shows = new ArrayList<>(0);
        try (Query<Show> query =
                showCollection.query("SELECT * WHERE movieId='" + movieId + "'", Show.class)) {
            RowSet<Show> rowSet = query.fetch();
            while (rowSet.hasNext()) {
                Show row = rowSet.next();
                shows.add(row);
            }
        } catch (GSException e) {
            log.error("Error fetch all shows by movieId", e);
        }
        return shows;
    }

    public List<ShowDTO> findAllByMovieId(String movieId) {
        final List<Show> shows = fetchAllByMovieId(movieId);
        return shows.stream().map(show -> mapToDTO(show, new ShowDTO())).toList();
    }

    private List<Seat> fetchSeatsByShowId(String showId) {
        List<Seat> seats = new ArrayList<>(0);
        try (Query<Seat> query =
                seatCollection.query("SELECT * WHERE showId='" + showId + "'", Seat.class)) {
            RowSet<Seat> rowSet = query.fetch();
            while (rowSet.hasNext()) {
                Seat row = rowSet.next();
                seats.add(row);
            }
        } catch (GSException e) {
            log.error("Error fetch all seats by showId", e);
        }
        return seats;
    }

    public ShowDTO get(final String id) {
        try (Query<Show> query =
                showCollection.query("SELECT * WHERE id='" + id + "'", Show.class)) {
            RowSet<Show> rowSet = query.fetch();
            if (rowSet.hasNext()) {
                Show row = rowSet.next();
                ShowDTO dto = mapToDTO(row, new ShowDTO());
                List<Seat> seats = fetchSeatsByShowId(id);
                dto.setSeats(seats.stream().map(seat -> mapSeatDTO(seat, new SeatDTO())).toList());
                return dto;
            }
        } catch (GSException e) {
            log.error("Error fetch by Id", e);
        }
        throw new NotFoundException();
    }

    public String create(final ShowDTO showDTO) {
        final Show show = new Show();
        mapToEntity(showDTO, show);
        show.setId(showDTO.getId());
        String seatsLocation = "ABCDEFGHIJK";
        for (int row = 0; row < seatsLocation.length(); row++) {
            for (int col = 1; col <= 15; col++) {
                String seatNumber = seatsLocation.charAt(row) + String.valueOf(col);
                String seatId = showDTO.getId() + "_" + seatNumber;
                Seat seat = new Seat();
                seat.setId(seatId);
                seat.setShowId(showDTO.getId());
                seat.setStatus("AVAILABLE");
                seat.setSeatNumber(seatNumber);
                try {
                    seatCollection.put(seat);
                } catch (GSException e) {
                    throw new AppErrorException();
                }
            }
        }
        try {
            showCollection.put(show);
        } catch (GSException e) {
            throw new AppErrorException();
        }
        return show.getId();
    }

    private static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private ShowDTO mapToDTO(final Show show, final ShowDTO showDTO) {
        showDTO.setId(show.getId());
        showDTO.setMovieId(show.getMovieId());
        showDTO.setStartTime(convertToLocalDateTime(show.getStartTime()));
        showDTO.setEndTime(convertToLocalDateTime(show.getEndTime()));
        showDTO.setPrice(new BigDecimal(show.getPrice()));
        showDTO.setTotalSeats(show.getTotalSeats());
        return showDTO;
    }

    private static Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Show mapToEntity(final ShowDTO showDTO, final Show show) {
        show.setMovieId(showDTO.getMovieId());
        show.setStartTime(convertToDate(showDTO.getStartTime()));
        show.setEndTime(convertToDate(showDTO.getEndTime()));
        show.setPrice(showDTO.getPrice().doubleValue());
        show.setTotalSeats(showDTO.getTotalSeats());
        return show;
    }

    private SeatDTO mapSeatDTO(final Seat seat, final SeatDTO seatDTO) {
        seatDTO.setId(seat.getId());
        seatDTO.setSeatNumber(seat.getSeatNumber());
        seatDTO.setShowId(seat.getShowId());
        seatDTO.setStatus(seat.getStatus());
        seatDTO.setAvailable(seat.getStatus().equalsIgnoreCase("AVAILABLE"));
        if (seatDTO.getSeatNumber().equalsIgnoreCase("A1")) {
            seatDTO.setAvailable(false);
        }
        return seatDTO;
    }

}
