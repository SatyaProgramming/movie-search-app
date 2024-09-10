package com.moviedb.moviesearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/")
    public String home() {
        return "home"; 
    }

    @GetMapping("/search-home")
    public String search(@RequestParam(name = "query") String query, @RequestParam(defaultValue = "1") int page, Model model) {
        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Query cannot be empty.");
            return "searchResults";
        }

        if (page < 1) {
            model.addAttribute("errorMessage", "Page number must be greater than 0.");
            return "searchResults";
        }

        try {
            MovieResponse movieResponse = movieService.searchMovies(query, page);

            if (movieResponse == null || movieResponse.getResults() == null || movieResponse.getResults().isEmpty()) {
                model.addAttribute("errorMessage", "No results found for the query '" + query + "'.");
            } else {
                model.addAttribute("movies", movieResponse);
                model.addAttribute("query", query);
                model.addAttribute("page", page);
                model.addAttribute("totalPages", movieResponse.getTotalPages());
            }

        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while searching for movies.");
        }

        return "searchResults";
    }

}
