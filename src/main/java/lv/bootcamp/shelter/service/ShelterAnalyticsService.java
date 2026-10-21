package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

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

            // create a list for a new species and add the animal to its group
            animalsBySpecies
                    .computeIfAbsent(species, key -> new ArrayList<>())
                    .add(animal);

            // add the animal to the vet check list if its age is unknown
            if (animal.getAge() == null) {
                String entry = animal.getName() + "(" + species + ")";
                animalsNeedingVetInput.add(entry);
            }
        }


        // extracting unvaccinated and vaccinated animals

        Map<String,Long> unvaccinatedPerSpecies = allAnimals.stream()
                .filter(animal -> !animal.isVaccinated())
                .collect(Collectors.groupingBy(
                        Animal::getSpecies,
                        Collectors.counting()
                ));

        Map<String,Long> vaccinatedPerSpecies = allAnimals.stream()
            .filter(Animal::isVaccinated)
            .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));

        // extracting old Animal Per Species

        Map<String, Animal> oldAnimalPerSpecies = allAnimals.stream()
                .filter(animal -> animal.getAge() != null)
                .collect(Collectors.toMap(
                        Animal::getSpecies,
                        animal -> animal,
                        BinaryOperator.maxBy(Comparator.comparingInt(Animal::getAge)),
                        TreeMap::new
                ));

        return new ShelterReportData(
                importResult,
                vaccinatedPerSpecies,
                oldAnimalPerSpecies,
                uniqueSpecies,
                animalsBySpecies,
                animalsNeedingVetInput,
                unvaccinatedPerSpecies);
    }
}
