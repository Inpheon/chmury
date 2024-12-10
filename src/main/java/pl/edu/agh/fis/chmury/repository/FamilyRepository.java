package pl.edu.agh.fis.chmury.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.fis.chmury.domain.Family;

public interface FamilyRepository extends Neo4jRepository<Family, Long> {
    Family findByFamilyNameIgnoreCase(String familyName);
}
