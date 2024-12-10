package pl.edu.agh.fis.chmury.dto;

public class GraphLink {
    private Long source;
    private Long target;
    private String relationshipType;

    public GraphLink(Long source, Long target, String relationshipType) {
        this.source = source;
        this.target = target;
        this.relationshipType = relationshipType;
    }


    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }

    public String getRelationshipType() {
        return relationshipType;
    }
}