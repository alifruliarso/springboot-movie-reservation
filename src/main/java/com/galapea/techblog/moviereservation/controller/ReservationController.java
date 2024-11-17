package com.galapea.techblog.moviereservation.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galapea.techblog.moviereservation.model.MovieDTO;
import com.galapea.techblog.moviereservation.model.ReservationDTO;
import com.galapea.techblog.moviereservation.model.ShowDTO;
import com.galapea.techblog.moviereservation.model.UserDTO;
import com.galapea.techblog.moviereservation.service.MovieService;
import com.galapea.techblog.moviereservation.service.ReservationService;
import com.galapea.techblog.moviereservation.service.ShowService;
import com.galapea.techblog.moviereservation.service.UserService;
import com.galapea.techblog.moviereservation.util.JsonStringFormatter;
import com.galapea.techblog.moviereservation.util.WebUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final ShowService showService;
    private final MovieService movieService;

    public ReservationController(final ReservationService reservationService,
            final ObjectMapper objectMapper, UserService userService, ShowService showService,
            MovieService movieService) {
        this.reservationService = reservationService;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.showService = showService;
        this.movieService = movieService;
    }

    @InitBinder
    public void jsonFormatting(final WebDataBinder binder) {
        binder.addCustomFormatter(new JsonStringFormatter<List<String>>(objectMapper) {}, "seats");
    }

    @GetMapping
    public String list(final Model model) {
        List<ReservationDTO> allDtos = reservationService.findAll();
        List<ReservationDTO> reservations = new ArrayList<>(allDtos.size());
        for (ReservationDTO resvDTO : allDtos) {
            ShowDTO show = showService.get(resvDTO.getShowId());
            resvDTO.setShow(show);
            MovieDTO movieDTO = movieService.get(show.getMovieId());
            resvDTO.setMovie(movieDTO);
            reservations.add(resvDTO);
        }
        model.addAttribute("reservations", reservations);
        return "reservation/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("reservation") final ReservationDTO reservationDTO) {
        return "reservation/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("reservation") @Valid final ReservationDTO reservationDTO,
            @RequestParam List<String> selectedSeats, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        selectedSeats.forEach(seat -> System.out.println(seat));
        reservationDTO.setSeats(selectedSeats);
        System.out.println(reservationDTO);
        if (bindingResult.hasErrors()) {
            return "redirect:/reservations";
            // return "reservation/reserve";
            // return "reservation/add";
        }
        reservationAttributes(reservationDTO.getShowId(), reservationDTO);
        reservationService.create(reservationDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("reservation.create.success"));
        return "redirect:/reservations";
    }


    @GetMapping("/reserved/{showId}")
    public String reserved(@PathVariable(name = "showId") final String showId, final Model model) {
        ReservationDTO reservation = new ReservationDTO();
        reservationAttributes(showId, reservation);
        model.addAttribute("reservation", reservation);
        return "reservation/reserve";
    }

    private void reservationAttributes(final String showId, ReservationDTO dto) {
        dto.setShowId(showId);
        List<UserDTO> users = userService.findAll();
        Random rand = new Random();
        int nextInt = rand.nextInt(users.size());
        UserDTO userDTO = users.get(nextInt);
        dto.setUserId(userDTO.getId());
        dto.setUser(userDTO);
        ShowDTO show = showService.get(showId);
        dto.setShow(show);
        dto.setTotalPrice(show.getPrice());
        MovieDTO movie = movieService.get(show.getMovieId());
        dto.setMovie(movie);
    }

}
