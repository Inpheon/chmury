package pl.edu.agh.fis.chmury.dto;

import java.util.List;

public class SearchResult {
    private String message;
    private List<String> path;

    public SearchResult(String message, List<String> path) {
        this.message = message;
        this.path = path;
    }

    public String getMessage() { return message; }
    public List<String> getPath() { return path; }
}