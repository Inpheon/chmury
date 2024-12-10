package pl.edu.agh.fis.chmury.controller;

import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.fis.chmury.domain.Family;
import pl.edu.agh.fis.chmury.domain.Person;
import pl.edu.agh.fis.chmury.dto.GraphData;
import pl.edu.agh.fis.chmury.dto.GraphLink;
import pl.edu.agh.fis.chmury.dto.GraphNode;
import pl.edu.agh.fis.chmury.service.FamilyService;
import pl.edu.agh.fis.chmury.service.PersonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PersonController {

    private final PersonService personService;
    private final FamilyService familyService;

    public PersonController(PersonService personService, FamilyService familyService) {
        this.personService = personService;
        this.familyService = familyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Person> all = personService.findAll();
        model.addAttribute("people", all);
        return "index";
    }

    @GetMapping("/person/{id}")
    public String personDetail(@PathVariable("id") Long id, Model model) {
        Optional<Person> pOpt = personService.findById(id);
        if (pOpt.isEmpty()) {
            model.addAttribute("message", "Person not found.");
            return "search-results";
        }
        Person p = pOpt.get();
        model.addAttribute("person", p);
        return "person-detail";
    }

    @GetMapping("/add-person")
    public String showAddPersonForm() {
        return "add-person";
    }

    @PostMapping("/add-person")
    public String addPerson(@RequestParam("firstName") String firstName,
                            @RequestParam("lastName") String lastName) {
        personService.addPerson(firstName, lastName);
        return "redirect:/";
    }

    @GetMapping("/edit-person/{id}")
    public String showEditPersonForm(@PathVariable("id") Long id, Model model) {
        Optional<Person> pOpt = personService.findById(id);
        if (pOpt.isEmpty()) {
            model.addAttribute("message", "Person not found.");
            return "search-results";
        }
        model.addAttribute("person", pOpt.get());
        return "edit-person";
    }

    @PostMapping("/edit-person")
    public String editPerson(@RequestParam("id") Long id,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName) {
        personService.updatePerson(id, firstName, lastName);
        return "redirect:/person/" + id;
    }

    @PostMapping("/delete-person")
    public String deletePerson(@RequestParam("id") Long id) {
        personService.deletePerson(id);
        return "redirect:/";
    }

    @GetMapping("/set-relationship")
    public String showSetRelationshipForm(Model model) {
        model.addAttribute("people", personService.findAll());
        return "set-relationship";
    }

    @PostMapping("/set-relationship")
    public String setRelationship(@RequestParam("childId") Long childId,
                                  @RequestParam("parentId") Long parentId) {
        personService.addParentRelationship(childId, parentId);
        return "redirect:/";
    }

    @GetMapping("/set-spouse")
    public String showSetSpouseForm(Model model) {
        model.addAttribute("people", personService.findAll());
        return "set-spouse";
    }

    @PostMapping("/set-spouse")
    public String setSpouse(@RequestParam("personId") Long personId,
                            @RequestParam("spouseId") Long spouseId) {
        personService.setSpouse(personId, spouseId);
        return "redirect:/";
    }

    @GetMapping("/add-sibling")
    public String showAddSiblingForm(Model model) {
        model.addAttribute("people", personService.findAll());
        return "add-sibling";
    }

    @PostMapping("/add-sibling")
    public String addSibling(@RequestParam("personId") Long personId,
                             @RequestParam("siblingId") Long siblingId) {
        personService.addSibling(personId, siblingId);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String showSearchForm() {
        return "search";
    }

    @PostMapping("/search")
    public String searchRelationship(@RequestParam("firstName1") String firstName1,
                                     @RequestParam("lastName1") String lastName1,
                                     @RequestParam("firstName2") String firstName2,
                                     @RequestParam("lastName2") String lastName2,
                                     Model model) {
        Path shortestPath = personService.findShortestPath(firstName1, lastName1, firstName2, lastName2);
        if (shortestPath == null) {
            model.addAttribute("message", "Nie znaleziono relacji.");
            return "search-results";
        }

        List<org.neo4j.driver.types.Node> nodeList = new ArrayList<>();
        shortestPath.nodes().forEach(nodeList::add);

        List<org.neo4j.driver.types.Relationship> relList = new ArrayList<>();
        shortestPath.relationships().forEach(relList::add);

        List<String> pathDescription = new ArrayList<>();
        for (int i = 0; i < relList.size(); i++) {
            org.neo4j.driver.types.Node startNode = nodeList.get(i);
            org.neo4j.driver.types.Node endNode = nodeList.get(i + 1);
            org.neo4j.driver.types.Relationship rel = relList.get(i);

            String startName = startNode.get("firstName").asString() + " " + startNode.get("lastName").asString();
            String endName = endNode.get("firstName").asString() + " " + endNode.get("lastName").asString();
            String relType = rel.type();

            pathDescription.add(startName + " --[" + relType + "]--> " + endName);
        }

        model.addAttribute("message", "Znaleziono relację:");
        model.addAttribute("pathDescription", pathDescription);
        return "search-results";
    }


    @GetMapping("/graph-data")
    @ResponseBody
    public GraphData graphData() {
        GraphData graphData = new GraphData();

        // Add Person nodes
        List<Person> allPeople = personService.findAll();
        for (Person p : allPeople) {
            String nodeName = p.getFirstName() + " " + p.getLastName();
            graphData.addNode(new GraphNode(p.getId(), nodeName, "Person"));
        }

        List<Family> allFamilies = familyService.findAllFamilies();
        for (Family f : allFamilies) {
            String nodeName = f.getFamilyName();

            Long familyId = -f.getId();
            graphData.addNode(new GraphNode(familyId, nodeName, "Family"));
        }

        for (Person p : allPeople) {
            Long sourceId = p.getId();

            if (p.getSpouse() != null) {
                Long targetId = p.getSpouse().getId();
                graphData.addLink(new GraphLink(sourceId, targetId, "MARRIED_TO"));
            }

            for (Person parent : p.getParents()) {
                Long targetId = parent.getId();
                graphData.addLink(new GraphLink(sourceId, targetId, "CHILD_OF"));
            }

            for (Person sibling : p.getSiblings()) {
                Long targetId = sibling.getId();
                graphData.addLink(new GraphLink(sourceId, targetId, "SIBLING_OF"));
            }
        }

        for (Person p : allPeople) {
            if (p.getFamily() != null) {
                Long sourceId = p.getId();
                Long targetId = -p.getFamily().getId(); // Negative ID for Family
                graphData.addLink(new GraphLink(sourceId, targetId, "MEMBER_OF"));
            }
        }

        return graphData;
    }

    @GetMapping("/visualize")
    public String visualize() {
        return "visualize";
    }
}