package com.galapea.techblog.moviereservation.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.galapea.techblog.moviereservation.entity.User;
import com.galapea.techblog.moviereservation.model.UserDTO;
import com.galapea.techblog.moviereservation.util.AppConstant;
import com.galapea.techblog.moviereservation.util.AppErrorException;
import com.galapea.techblog.moviereservation.util.NotFoundException;
import com.toshiba.mwcloud.gs.Collection;
import com.toshiba.mwcloud.gs.GSException;
import com.toshiba.mwcloud.gs.Query;
import com.toshiba.mwcloud.gs.RowSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    private final Collection<String, User> userCollection;


    public UserService(Collection<String, User> userCollection) {
        this.userCollection = userCollection;
    }

    private List<User> fetchAll() {
        List<User> users = new ArrayList<>(0);
        try {
            String tql = "SELECT * FROM " + AppConstant.USERS_CONTAINER;
            Query<User> query = userCollection.query(tql);
            RowSet<User> rowSet = query.fetch();
            while (rowSet.hasNext()) {
                User row = rowSet.next();
                users.add(row);
            }
        } catch (GSException e) {
            log.error("Error fetch all users", e);
        }
        return users;
    }

    public List<UserDTO> findAll() {
        final List<User> users = fetchAll();
        return users.stream().map(user -> mapToDTO(user, new UserDTO())).toList();
    }

    public UserDTO get(final String id) {
        try (Query<User> query =
                userCollection.query("SELECT * WHERE id='" + id + "'", User.class)) {
            RowSet<User> rowSet = query.fetch();
            if (rowSet.hasNext()) {
                return mapToDTO(rowSet.next(), new UserDTO());
            } else {
                throw new NotFoundException();
            }
        } catch (GSException e) {
            throw new AppErrorException();
        }
    }

    public String create(final UserDTO userDTO) {
        if (emailExists(userDTO.getEmail())) {
            return "";
        }
        final User user = new User();
        mapToEntity(userDTO, user);
        user.setId(KeyGenerator.next("usr_"));
        try {
            userCollection.put(user);
        } catch (GSException e) {
            throw new AppErrorException();
        }
        return user.getId();
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setFullName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }

    public boolean emailExists(final String email) {
        try (Query<User> query =
                userCollection.query("SELECT * WHERE email='" + email + "'", User.class)) {
            RowSet<User> rowSet = query.fetch();
            return rowSet.hasNext();
        } catch (GSException e) {
            return false;
        }
    }

}
