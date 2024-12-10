package pl.edu.agh.fis.chmury.service;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.types.Path;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.fis.chmury.domain.Person;
import pl.edu.agh.fis.chmury.repository.PersonRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    private final Neo4jClient neo4jClient;

    public PersonService(PersonRepository personRepository, Neo4jClient neo4jClient) {
        this.personRepository = personRepository;
        this.neo4jClient = neo4jClient;
    }

    @Transactional
    public Person addPerson(String firstName, String lastName) {
        Person p = new Person(firstName, lastName);
        return personRepository.save(p);
    }

    @Transactional
    public void addParentRelationship(Long childId, Long parentId) {
        Optional<Person> childOpt = personRepository.findById(childId);
        Optional<Person> parentOpt = personRepository.findById(parentId);
        if (childOpt.isPresent() && parentOpt.isPresent()) {
            Person child = childOpt.get();
            Person parent = parentOpt.get();
            child.addParent(parent);
            personRepository.save(child);
            personRepository.save(parent);
        }
    }

    @Transactional
    public void setSpouse(Long personId, Long spouseId) {
        Optional<Person> p1Opt = personRepository.findById(personId);
        Optional<Person> p2Opt = personRepository.findById(spouseId);
        if (p1Opt.isPresent() && p2Opt.isPresent()) {
            Person p1 = p1Opt.get();
            Person p2 = p2Opt.get();
            p1.marry(p2);
            personRepository.save(p1);
            personRepository.save(p2);
        }
    }

    @Transactional
    public void addSibling(Long personId, Long siblingId) {
        Optional<Person> p1Opt = personRepository.findById(personId);
        Optional<Person> p2Opt = personRepository.findById(siblingId);
        if (p1Opt.isPresent() && p2Opt.isPresent()) {
            Person p1 = p1Opt.get();
            Person p2 = p2Opt.get();
            p1.addSibling(p2);
            personRepository.save(p1);
            personRepository.save(p2);
        }
    }

    public List<Person> searchByFirstName(String firstName) {
        return personRepository.findByFirstNameContainingIgnoreCase(firstName);
    }

    public List<Person> searchByLastName(String lastName) {
        return personRepository.findByLastNameContainingIgnoreCase(lastName);
    }


    public Path findShortestPath(String fn1, String ln1, String fn2, String ln2) {
        String cypher = "MATCH p=shortestPath((a:Person {firstName:$fn1, lastName:$ln1})-[*]-(b:Person {firstName:$fn2, lastName:$ln2})) RETURN p LIMIT 1";
        return neo4jClient.query(cypher)
                .bind(fn1).to("fn1")
                .bind(ln1).to("ln1")
                .bind(fn2).to("fn2")
                .bind(ln2).to("ln2")
                .fetchAs(Path.class)
                .mappedBy((typeSystem, record) -> record.get("p").asPath())
                .one()
                .orElse(null);
    }


    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    @Transactional
    public Person updatePerson(Long id, String firstName, String lastName) {
        Optional<Person> pOpt = personRepository.findById(id);
        if (pOpt.isPresent()) {
            Person p = pOpt.get();
            p.setFirstName(firstName);
            p.setLastName(lastName);
            return personRepository.save(p);
        }
        return null;
    }

    @Transactional
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
}