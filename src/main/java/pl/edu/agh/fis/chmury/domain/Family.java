package pl.edu.agh.fis.chmury.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
public class Family {

    @Id
    @GeneratedValue
    private Long id;

    private String familyName;

    @Relationship(type = "MEMBER_OF", direction = Relationship.Direction.INCOMING)
    private Set<Person> members = new HashSet<>();

    public Family() {}

    public Family(String familyName) {
        this.familyName = familyName;
    }

    // Getters and Setters

    public Long getId() { return id; }
    public String getFamilyName() { return familyName; }
    public Set<Person> getMembers() { return members; }

    public void setFamilyName(String familyName) { this.familyName = familyName; }
    public void addMember(Person person) {
        this.members.add(person);
    }
}
