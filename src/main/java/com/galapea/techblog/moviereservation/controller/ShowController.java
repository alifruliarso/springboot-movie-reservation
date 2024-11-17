package com.galapea.techblog.moviereservation.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.galapea.techblog.moviereservation.model.ShowDTO;
import com.galapea.techblog.moviereservation.service.MovieService;
import com.galapea.techblog.moviereservation.service.ShowService;
import com.galapea.techblog.moviereservation.util.WebUtils;


@Controller
@RequestMapping("/shows")
public class ShowController {

    private final ShowService showService;
    private final MovieService movieService;

    public ShowController(final ShowService showService, final MovieService movieService) {
        this.showService = showService;
        this.movieService = movieService;
    }

    @GetMapping
    public String list(final Model model) {
        List<ShowDTO> allShow = showService.findAll();
        allShow.stream().forEach(show -> {
            show.setMovie(movieService.get(show.getMovieId()));
        });
        model.addAttribute("shows", allShow);
        return "show/list";
    }

    @GetMapping("/add/{movieId}")
    public String add(@PathVariable(name = "movieId") final String movieId,
            @ModelAttribute("show") final ShowDTO showDTO) {
        showDTO.setMovieId(movieId);
        return "show/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("show") @Valid final ShowDTO showDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "show/add";
        }
        // showService.create(showDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("show.create.success"));
        return "redirect:/shows";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        // model.addAttribute("show", showService.get(id));
        return "show/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final String id,
            @ModelAttribute("show") @Valid final ShowDTO showDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "show/edit";
        }
        // showService.update(id, showDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("show.update.success"));
        return "redirect:/shows";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final String id,
            final RedirectAttributes redirectAttributes) {
        // showService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                WebUtils.getMessage("show.delete.success"));
        return "redirect:/shows";
    }

    @GetMapping("/movie/{movieId}")
    public String movieShow(@PathVariable(name = "movieId") final String movieId,
            final Model model) {
        List<ShowDTO> allShow = showService.findAllByMovieId(movieId);
        allShow.stream().forEach(show -> {
            show.setMovie(movieService.get(show.getMovieId()));
        });
        model.addAttribute("shows", allShow);
        model.addAttribute("movieId", movieId);
        return "show/list";
    }

}
