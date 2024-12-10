package pl.edu.agh.fis.chmury.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;

    @Relationship(type = "MARRIED_TO", direction = Relationship.Direction.OUTGOING)
    private Person spouse;

    @Relationship(type = "CHILD_OF", direction = Relationship.Direction.INCOMING)
    private Set<Person> parents = new HashSet<>();

    @Relationship(type = "CHILD_OF", direction = Relationship.Direction.OUTGOING)
    private Set<Person> children = new HashSet<>();

    @Relationship(type = "SIBLING_OF", direction = Relationship.Direction.OUTGOING)
    private Set<Person> siblings = new HashSet<>();

    @Relationship(type = "MEMBER_OF", direction = Relationship.Direction.OUTGOING)
    private Family family;

    public Person() {}

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Set<Person> getParents() { return parents; }
    public Set<Person> getChildren() { return children; }
    public Person getSpouse() { return spouse; }
    public Set<Person> getSiblings() { return siblings; }

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    public void addParent(Person parent) {
        this.parents.add(parent);
        parent.children.add(this);
    }

    public void marry(Person other) {
        this.spouse = other;
        other.spouse = this;
    }

    public void addSibling(Person sibling) {
        this.siblings.add(sibling);
        sibling.siblings.add(this);
    }

    public Family getFamily() { return family; }

    public void setFamily(Family family) {
        this.family = family;
        if (family != null) {
            family.addMember(this);
        }
    }
}