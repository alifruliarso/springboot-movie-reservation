package com.galapea.techblog.moviereservation.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.galapea.techblog.moviereservation.entity.Movie;
import com.galapea.techblog.moviereservation.entity.Reservation;
import com.galapea.techblog.moviereservation.entity.Seat;
import com.galapea.techblog.moviereservation.entity.Show;
import com.galapea.techblog.moviereservation.entity.User;
import com.galapea.techblog.moviereservation.util.AppConstant;
import com.toshiba.mwcloud.gs.Collection;
import com.toshiba.mwcloud.gs.GSException;
import com.toshiba.mwcloud.gs.GridStore;
import com.toshiba.mwcloud.gs.GridStoreFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class GridDBConfig {

    @Value("${GRIDDB_NOTIFICATION_MEMBER}")
    private String notificationMember;

    @Value("${GRIDDB_CLUSTER_NAME}")
    private String clusterName;

    @Value("${GRIDDB_USER}")
    private String user;

    @Value("${GRIDDB_PASSWORD}")
    private String password;

    @Bean
    public GridStore gridStore() throws GSException {
        log.info("##notificationMember:{}", notificationMember);
        Properties properties = new Properties();
        properties.setProperty("notificationMember", notificationMember);
        properties.setProperty("clusterName", clusterName);
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        GridStore store = GridStoreFactory.getInstance().getGridStore(properties);

        /**
         * If you try to save an object that is different from the one used to create the
         * collection, the following error will occur:
         * com.toshiba.mwcloud.gs.common.GSStatementException: [60016:DS_DS_CHANGE_SCHEMA_DISABLE]
         * Just delete the collection and redefine it and it should be ok.
         **/
        // store.dropCollection(AppConstant.SEAT_CONTAINER);
        // store.dropCollection(AppConstant.SHOW_CONTAINER);
        // store.dropCollection(AppConstant.MOVIE_CONTAINER);
        // store.dropCollection(AppConstant.RESERVATION_CONTAINER);
        // store.dropCollection(AppConstant.USERS_CONTAINER);
        return store;
    }

    @Bean
    public Collection<String, User> userCollection(GridStore gridStore) throws GSException {
        Collection<String, User> collection =
                gridStore.putCollection(AppConstant.USERS_CONTAINER, User.class);
        collection.createIndex("email");
        return collection;
    }

    @Bean
    public Collection<String, Movie> movieCollection(GridStore gridStore) throws GSException {
        Collection<String, Movie> movieCollection =
                gridStore.putCollection(AppConstant.MOVIE_CONTAINER, Movie.class);
        movieCollection.createIndex("title");
        return movieCollection;
    }

    @Bean
    public Collection<String, Show> showCollection(GridStore gridStore) throws GSException {
        Collection<String, Show> showCollection =
                gridStore.putCollection(AppConstant.SHOW_CONTAINER, Show.class);
        showCollection.createIndex("movieId");
        return showCollection;
    }

    @Bean
    public Collection<String, Seat> seatCollection(GridStore gridStore) throws GSException {
        Collection<String, Seat> seatCollection =
                gridStore.putCollection(AppConstant.SEAT_CONTAINER, Seat.class);
        seatCollection.createIndex("showId");
        return seatCollection;
    }

    @Bean
    public Collection<String, Reservation> reservationCollection(GridStore gridStore)
            throws GSException {
        Collection<String, Reservation> reservationCollection =
                gridStore.putCollection(AppConstant.RESERVATION_CONTAINER, Reservation.class);
        reservationCollection.createIndex("userId");
        reservationCollection.createIndex("showId");
        return reservationCollection;
    }

}
