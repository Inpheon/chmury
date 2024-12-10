package pl.edu.agh.fis.chmury.dto;

import java.util.ArrayList;
import java.util.List;

public class GraphData {
    private List<GraphNode> nodes = new ArrayList<>();
    private List<GraphLink> links = new ArrayList<>();


    public List<GraphNode> getNodes() {
        return nodes;
    }

    public void addNode(GraphNode node) {
        this.nodes.add(node);
    }

    public List<GraphLink> getLinks() {
        return links;
    }

    public void addLink(GraphLink link) {
        this.links.add(link);
    }
}