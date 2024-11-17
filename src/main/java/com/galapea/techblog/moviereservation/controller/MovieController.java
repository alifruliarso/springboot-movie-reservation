package com.galapea.techblog.moviereservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.galapea.techblog.moviereservation.model.MovieDTO;
import com.galapea.techblog.moviereservation.service.MovieService;
import com.galapea.techblog.moviereservation.util.WebUtils;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(final MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("movies", movieService.findAll());
        return "movie/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("movie") final MovieDTO movieDTO) {
        return "movie/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("movie") @Valid final MovieDTO movieDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "movie/add";
        }
        movieService.create(movieDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("movie.create.success"));
        return "redirect:/movies";
    }

}
