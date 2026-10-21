package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import lv.bootcamp.shelter.model.Animal;


public class ReportExportService {

        private static final DateTimeFormatter DATE_FORMATTER =
                DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void writeReport(Path outputPath, ShelterReportData reportData) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                outputPath,
                StandardCharsets.UTF_8))
        {
            writeHeader(writer, reportData);
            writeUniqueSpecies(writer, reportData);
            writeSpeciesBreakdown(writer, reportData);
            writeOldestPerSpecies(writer, reportData);
            writeVetInput(writer, reportData);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Failed to write report: " + outputPath, e);
        }
    }

        private void writeHeader (BufferedWriter writer, ShelterReportData data) throws IOException {
            writeLine(writer, "=== Shelter Intake Report ===");
            writeLine(writer, "Date generated: "
                    // using zone "Europe/Riga" because SonarQube got error
                    // and now report date does not depend on computer settings
                    + LocalDate.now(ZoneId.of("Europe/Riga")).format(DATE_FORMATTER));
            writeLine(writer);
            writeLine(writer, "Total imported: " + data.importResult().allAnimals().size());
            writeLine(writer, "Total skipped: " + data.importResult().skippedRows());
            writeLine(writer);
        }

        private void writeUniqueSpecies (BufferedWriter writer, ShelterReportData data) throws IOException {
            writeLine(writer, "--- Unique species ---");
            writeLine(writer, String.join(", ", sortedSpecies(data)));
            writeLine(writer);
        }

        private void writeSpeciesBreakdown (BufferedWriter writer, ShelterReportData data) throws IOException {
            writeLine(writer, "--- Per-species breakdown ---");
            for (String species : sortedSpecies(data)) {
                int total = data.animalsBySpecies().getOrDefault(species, List.of()).size();
                long vaccinated = data.vaccinatedPerSpecies().getOrDefault(species, 0L);
                writeLine(writer, species + ": " + total + " total, " + vaccinated + " vaccinated");
            }
            writeLine(writer);
        }

        private void writeOldestPerSpecies (BufferedWriter writer, ShelterReportData data) throws IOException {
            writeLine(writer, "--- Oldest per species ---");
            for (String species : sortedSpecies(data)) {
                Animal oldest = data.oldestAnimalPerSpecies().get(species);

                // a species may have no animals with a known age
                String text = (oldest == null)
                        ? "no known age"
                        : oldest.getName() + " (age " + oldest.getAge() + ")";
                writeLine(writer, species + ": " + text);
            }
            writeLine(writer);
        }

        private void writeVetInput (BufferedWriter writer, ShelterReportData data) throws IOException {
            writeLine(writer, "--- Needs vet input to determine age ---");
            writeLine(writer, String.join(", ", data.animalsNeedingVetInput()));
        }

        // stretch goal 1: sorted by species
        private List<String> sortedSpecies(ShelterReportData data) {
            return data.animalsBySpecies().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getKey)
                    .toList();
        }

        private void writeLine (BufferedWriter writer, String text) throws IOException {
            writer.write(text);
            writer.newLine();
        }

        private void writeLine (BufferedWriter writer) throws IOException {
            writer.newLine();
        }
    }


