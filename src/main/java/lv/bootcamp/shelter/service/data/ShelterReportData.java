package lv.bootcamp.shelter.service.data;

import lv.bootcamp.shelter.model.Animal;

import java.util.List;
import java.util.Map;
import java.util.Set;


// keep the import result and calculated data together to report export
public record ShelterReportData(ImportResult importResult,
                                Map<String, Long> vaccinatedPerSpecies,
                                Map<String, Animal> oldestAnimalPerSpecies,
                                Set<String> uniqueSpecies,
                                Map<String, List<Animal>> animalsBySpecies,
                                List<String> animalsNeedingVetInput,
                                Map<String, Long> unvaccinatedPerSpecies) {

}
