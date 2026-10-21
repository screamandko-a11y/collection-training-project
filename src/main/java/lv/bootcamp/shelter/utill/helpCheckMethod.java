package lv.bootcamp.shelter.utill;

import lv.bootcamp.shelter.model.Animal;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class helpCheckMethod {

    public static void prinAllAnimals(
            List<Animal> allAnimals,
            Set<String> uniqueSpecies,
            Map<String, List<Animal>> animalsBySpecies,
            List<String> animalsNeedingVetInput
    ) {
        System.out.println("Total animals: " + allAnimals.size());
        System.out.println("Unique species: " + uniqueSpecies);

        System.out.println("Animals by species:");
        animalsBySpecies.forEach((species, animals) -> {
            System.out.println(species + ": " + animals.size() + " animals");
        });

        System.out.println("Animals needing vet input:");
        for (String entry : animalsNeedingVetInput) {
            System.out.println(entry);
        }

        System.out.println("All animals:");
        for (Animal animal : allAnimals) {
            System.out.println(
                    animal.getName()
                            + " | species: " + animal.getSpecies()
                            + " | age: " + animal.getAge()
                            + " | vaccinated: " + animal.isVaccinated()
                            + " | intake date: " + animal.getIntakeDate()
            );
        }
    }
}