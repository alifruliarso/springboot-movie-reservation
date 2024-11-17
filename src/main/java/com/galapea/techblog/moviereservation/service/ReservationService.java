package com.galapea.techblog.moviereservation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import com.galapea.techblog.moviereservation.entity.Reservation;
import com.galapea.techblog.moviereservation.entity.Seat;
import com.galapea.techblog.moviereservation.model.ReservationDTO;
import com.galapea.techblog.moviereservation.util.AppConstant;
import com.galapea.techblog.moviereservation.util.AppErrorException;
import com.toshiba.mwcloud.gs.Collection;
import com.toshiba.mwcloud.gs.GSException;
import com.toshiba.mwcloud.gs.Query;
import com.toshiba.mwcloud.gs.RowSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationService {

    private final Collection<String, Reservation> reservationCollection;
    private final Collection<String, Seat> seatCollection;

    public ReservationService(Collection<String, Reservation> reservationCollection,
            Collection<String, Seat> seatCollection) {
        this.reservationCollection = reservationCollection;
        this.seatCollection = seatCollection;
    }

    private List<Reservation> fetchAll() {
        List<Reservation> reservations = new ArrayList<>(0);
        try {
            String tql = "SELECT * FROM " + AppConstant.RESERVATION_CONTAINER;
            Query<Reservation> query = reservationCollection.query(tql);
            RowSet<Reservation> rowSet = query.fetch();
            while (rowSet.hasNext()) {
                Reservation row = rowSet.next();
                reservations.add(row);
            }
        } catch (GSException e) {
            log.error("Error fetch all reservations", e);
        }
        return reservations;
    }

    public List<ReservationDTO> findAll() {
        final List<Reservation> reservations = fetchAll();
        log.info("fetched: {} reservations", reservations.size());
        return reservations.stream().map(reservation -> mapToDTO(reservation, new ReservationDTO()))
                .toList();
    }

    public String create(final ReservationDTO reservationDTO) {
        reservationDTO.setTotalPrice(reservationDTO.getShow().getPrice()
                .multiply(new BigDecimal(reservationDTO.getSeats().size())));
        final Reservation reservation = new Reservation();
        mapToEntity(reservationDTO, reservation);
        reservation.setId(KeyGenerator.next("rsv"));
        try {
            seatCollection.setAutoCommit(false);
            for (String seatId : reservationDTO.getSeats()) {
                String tql = "SELECT * WHERE id='" + seatId + "'";
                log.info(tql);
                Query<Seat> query = seatCollection.query(tql, Seat.class);
                RowSet<Seat> rs = query.fetch(true);
                if (rs.hasNext()) {
                    Seat seat = rs.next();
                    seat.setStatus("RESERVED");
                    log.info(">>> try update seat:{}", seat);
                    rs.update(seat);
                }
            }
            seatCollection.commit();
            reservationCollection.put(reservation);
        } catch (GSException e) {
            log.error("Failed to create reservations", e);
            throw new AppErrorException("Failed to save reservation");
        }
        return reservation.getId();
    }

    private ReservationDTO mapToDTO(final Reservation reservation,
            final ReservationDTO reservationDTO) {
        reservationDTO.setId(reservation.getId());
        reservationDTO.setUserId(reservation.getUserId());
        reservationDTO.setShowId(reservation.getShowId());
        reservationDTO.setTotalPrice(new BigDecimal(reservation.getTotalPrice()));
        reservationDTO.setNumberOfSeats(reservation.getNumberOfSeats());
        reservationDTO.setSeats(Arrays.asList(reservation.getSeats()));
        List<String> seatsNumber = new ArrayList<>();
        for (String seatId : reservation.getSeats()) {
            try {
                String tql = "SELECT * WHERE id='" + seatId + "'";
                Query<Seat> query = seatCollection.query(tql, Seat.class);
                RowSet<Seat> rs = query.fetch();
                if (rs.hasNext()) {
                    seatsNumber.add(rs.next().getSeatNumber());
                }
            } catch (GSException e) {
                log.error("error get seat by id", e);
            }
        }
        reservationDTO.setSeatsNumber(seatsNumber);
        return reservationDTO;
    }

    private Reservation mapToEntity(final ReservationDTO reservDTO, final Reservation reservation) {
        reservation.setUserId(reservDTO.getUserId());
        reservation.setShowId(reservDTO.getShowId());
        reservation.setTotalPrice(reservDTO.getTotalPrice().doubleValue());
        reservation.setNumberOfSeats(reservDTO.getNumberOfSeats());
        reservation.setSeats(reservDTO.getSeats().toArray(String[]::new));
        return reservation;
    }

    public List<Seat> fetchSeatsByShowId(String showId) {
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

}
