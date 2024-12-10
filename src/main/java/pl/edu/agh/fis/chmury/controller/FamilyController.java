package pl.edu.agh.fis.chmury.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.fis.chmury.domain.Family;
import pl.edu.agh.fis.chmury.service.FamilyService;
import pl.edu.agh.fis.chmury.service.PersonService;

import java.util.List;
import java.util.Optional;

@Controller
public class FamilyController {

    private final FamilyService familyService;
    private final PersonService personService;

    public FamilyController(FamilyService familyService, PersonService personService) {
        this.familyService = familyService;
        this.personService = personService;
    }

    @GetMapping("/families")
    public String listFamilies(Model model) {
        List<Family> families = familyService.findAllFamilies();
        model.addAttribute("families", families);
        return "families";
    }

    @GetMapping("/add-family")
    public String showAddFamilyForm() {
        return "add-family";
    }

    @PostMapping("/add-family")
    public String addFamily(@RequestParam("familyName") String familyName, Model model) {
        try {
            familyService.createFamily(familyName);
            model.addAttribute("successMessage", "Rodzina została dodana pomyślnie.");
            return "redirect:/families";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Rodzina o tej nazwie już istnieje.");
            return "add-family";
        }
    }

    @GetMapping("/assign-family")
    public String showAssignFamilyForm(Model model) {
        model.addAttribute("people", personService.findAll());
        model.addAttribute("families", familyService.findAllFamilies());
        return "assign-family";
    }

    @PostMapping("/assign-family")
    public String assignFamily(@RequestParam("personId") Long personId,
                               @RequestParam("familyId") Long familyId,
                               Model model) {
        try {
            familyService.addPersonToFamily(personId, familyId);
            model.addAttribute("successMessage", "Osoba została przypisana do rodziny pomyślnie.");
            return "redirect:/families";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Nie można przypisać osoby do rodziny.");
            model.addAttribute("people", personService.findAll());
            model.addAttribute("families", familyService.findAllFamilies());
            return "assign-family";
        }
    }

    @GetMapping("/edit-family/{id}")
    public String showEditFamilyForm(@PathVariable("id") Long id, Model model) {
        Optional<Family> familyOpt = familyService.findFamilyById(id);
        if (familyOpt.isPresent()) {
            model.addAttribute("family", familyOpt.get());
            return "edit-family";
        } else {
            model.addAttribute("errorMessage", "Rodzina nie znaleziona.");
            return "redirect:/families";
        }
    }

    @PostMapping("/edit-family")
    public String editFamily(@RequestParam("id") Long id,
                             @RequestParam("familyName") String familyName,
                             Model model) {
        try {
            Family updatedFamily = familyService.updateFamilyName(id, familyName);
            if (updatedFamily != null) {
                model.addAttribute("successMessage", "Rodzina została zaktualizowana pomyślnie.");
                return "redirect:/families";
            } else {
                model.addAttribute("errorMessage", "Rodzina nie znaleziona.");
                return "edit-family";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Nie można zaktualizować rodziny.");
            model.addAttribute("family", familyService.findFamilyById(id).orElse(null));
            return "edit-family";
        }
    }

    @PostMapping("/delete-family")
    public String deleteFamily(@RequestParam("id") Long id, Model model) {
        try {
            familyService.deleteFamily(id);
            model.addAttribute("successMessage", "Rodzina została usunięta pomyślnie.");
            return "redirect:/families";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Nie można usunąć rodziny. Upewnij się, że nie ma przypisanych członków.");
            List<Family> families = familyService.findAllFamilies();
            model.addAttribute("families", families);
            return "families";
        }
    }
}
