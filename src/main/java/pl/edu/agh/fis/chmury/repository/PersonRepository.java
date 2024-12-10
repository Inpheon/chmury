package pl.edu.agh.fis.chmury.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import pl.edu.agh.fis.chmury.domain.Person;

import java.util.List;
import java.util.Map;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

    List<Person> findByLastNameContainingIgnoreCase(String lastName);
    List<Person> findByFirstNameContainingIgnoreCase(String firstName);

    @Query("MATCH p=shortestPath((a:Person {firstName:$name1, lastName:$lname1})-[*]-(b:Person {firstName:$name2, lastName:$lname2})) RETURN p")
    List<Map<String,Object>> findShortestPathBetween(String name1, String lname1, String name2, String lname2);

}