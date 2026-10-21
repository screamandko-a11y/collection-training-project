package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;
import lv.bootcamp.shelter.utill.helpCheckMethod;

import java.util.*;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals();

        Set<String> uniqueSpecies = new TreeSet<>();
        Map<String, List<Animal>> animalsBySpecies = new HashMap<>();
        List<String> animalsNeedingVetInput = new ArrayList<>();

        for (Animal animal : allAnimals) {
            String species = animal.getSpecies();

            // unique animal species
            uniqueSpecies.add(species);

            // group by species
            animalsBySpecies
                    .computeIfAbsent(species, key -> new ArrayList<>())
                    .add(animal);


            // add the animal to the vet check list if its age is unknown
            if (animal.getAge() == null) {
                String entry = animal.getName() + "(" + species + ")";
                animalsNeedingVetInput.add(entry);
            }

        }

        helpCheckMethod.prinAllAnimals(
                allAnimals,
                uniqueSpecies,
                animalsBySpecies,
                animalsNeedingVetInput
        );



        // TODO Step 3:
        // Add necessary fields to ShelterReportData
        // Use stream pipelines for:
        // - vaccinated vs unvaccinated counts per species
        // - oldest animal per species (excluding unknown ages)

        return new ShelterReportData(importResult);
    }
}
