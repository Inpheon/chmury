package pl.edu.agh.fis.chmury.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.fis.chmury.domain.Family;
import pl.edu.agh.fis.chmury.domain.Person;
import pl.edu.agh.fis.chmury.repository.FamilyRepository;
import pl.edu.agh.fis.chmury.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final PersonRepository personRepository;

    public FamilyService(FamilyRepository familyRepository, PersonRepository personRepository) {
        this.familyRepository = familyRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    public Family createFamily(String familyName) {
        Family f = new Family(familyName);
        return familyRepository.save(f);
    }

    public List<Family> findAllFamilies() {
        return familyRepository.findAll();
    }

    public Optional<Family> findFamilyById(Long id) {
        return familyRepository.findById(id);
    }

    @Transactional
    public Family updateFamilyName(Long familyId, String newName) {
        Optional<Family> fOpt = familyRepository.findById(familyId);
        if (fOpt.isPresent()) {
            Family f = fOpt.get();
            f.setFamilyName(newName);
            return familyRepository.save(f);
        }
        return null;
    }

    @Transactional
    public void deleteFamily(Long familyId) {
        Optional<Family> fOpt = familyRepository.findById(familyId);
        if (fOpt.isPresent()) {
            Family f = fOpt.get();
            if (!f.getMembers().isEmpty()) {
                throw new IllegalStateException("Cannot delete a family with members.");
            }
            familyRepository.delete(f);
        } else {
            throw new IllegalArgumentException("Family not found.");
        }
    }

    @Transactional
    public void addPersonToFamily(Long personId, Long familyId) {
        Optional<Family> fOpt = familyRepository.findById(familyId);
        Optional<Person> pOpt = personRepository.findById(personId);
        if (fOpt.isPresent() && pOpt.isPresent()) {
            Family f = fOpt.get();
            Person p = pOpt.get();
            p.setFamily(f);
            personRepository.save(p);
            familyRepository.save(f);
        } else {
            throw new IllegalArgumentException("Family or Person not found.");
        }
    }
}