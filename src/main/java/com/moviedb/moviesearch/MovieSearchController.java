package com.moviedb.moviesearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MovieSearchController {

    private static final Logger logger = LoggerFactory.getLogger(MovieSearchController.class);

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public MovieSearchController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam String query, 
                               @RequestParam(defaultValue = "1") int page, 
                               Model model) {
        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Query cannot be empty.");
            return "searchResults";
        }

        if (page < 1) {
            model.addAttribute("errorMessage", "Page number must be greater than 0.");
            return "searchResults";
        }

        // Encode query to handle special characters
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Error encoding query: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error encoding the query.");
            return "searchResults";
        }

        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + encodedQuery + "&page=" + page;
        logger.info("API Request URL: {}", url);

        try {
            MovieResponse response = restTemplate.getForObject(url, MovieResponse.class);

            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                model.addAttribute("movies", response);
                model.addAttribute("query", query);
                model.addAttribute("page", page);
                model.addAttribute("totalPages", response.getTotalPages());
            } else {
                model.addAttribute("errorMessage", "No movies found for the query '" + query + "'.");
            }

            return "searchResults";
        } catch (HttpClientErrorException e) {
            logger.error("HTTP error: {}", e.getStatusCode());
            model.addAttribute("errorMessage", "HTTP error occurred while searching for movies.");
            return "searchResults";
        } catch (RestClientException e) {
            logger.error("Error during API call: {}", e.getMessage());
            model.addAttribute("errorMessage", "An error occurred while calling the API.");
            return "searchResults";
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            model.addAttribute("errorMessage", "An unexpected error occurred.");
            return "searchResults";
        }
    }

    // Autocomplete endpoint for fetching suggestions based on user input
    @GetMapping("/autocomplete")
    @ResponseBody
    public List<String> autocompleteMovies(@RequestParam String query) {
        // Encode query to handle special characters
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Error encoding query: {}", e.getMessage());
            return Collections.emptyList(); // Return an empty list in case of error
        }

        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + encodedQuery;
        logger.info("API Autocomplete Request URL: {}", url);

        try {
            MovieResponse response = restTemplate.getForObject(url, MovieResponse.class);
            if (response != null && response.getResults() != null) {
                // Extract the movie titles from the response
                return response.getResults().stream()
                        .map(Movie::getTitle)  // Assuming Movie class has getTitle method
                        .collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        } catch (RestClientException e) {
            logger.error("Error during autocomplete API call: {}", e.getMessage());
            return Collections.emptyList(); // Return empty list on error
        }
    }
}
