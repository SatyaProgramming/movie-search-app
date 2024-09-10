package com.moviedb.moviesearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final String API_URL = "https://api.themoviedb.org/3/search/movie?api_key=";

    private final RestTemplate restTemplate;

    public MovieService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MovieResponse searchMovies(String query, int page) {
        String url = API_URL + apiKey + "&query=" + query + "&page=" + page;

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonResponse, MovieResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
