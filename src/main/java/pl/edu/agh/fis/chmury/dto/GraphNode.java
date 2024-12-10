package pl.edu.agh.fis.chmury.dto;

public class GraphNode {
    private Long id;
    private String name;
    private String type; // "Person" or "Family"

    public GraphNode(Long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}