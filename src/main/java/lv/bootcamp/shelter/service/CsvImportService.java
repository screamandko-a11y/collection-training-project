package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
public class CsvImportService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ImportResult importAnimals(Path inputPath) {
        log.info("Starting import from {}", inputPath);

        try {
            List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);


            List<Animal> animals = IntStream.range(1, lines.size())
                    .mapToObj(i -> parseRow(lines.get(i), i + 1))
                    .flatMap(Optional::stream)
                    .toList();

            int dataRows = Math.max(0, lines.size() - 1);
            int skippedRows = dataRows - animals.size();

            return new ImportResult(animals, skippedRows);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + inputPath, e);
        }
    }


    private Optional<Animal> parseRow(String line, int rowNumber) {
        try {
            return Optional.of(parseAnimal(line));
        } catch (IllegalArgumentException | DateTimeParseException e) {
            log.warn("Skipping row {}: {}", rowNumber, e.getMessage());
            return Optional.empty();
        }
    }

    private Animal parseAnimal(String line) {
        if (line.isBlank()) {
            throw new IllegalArgumentException("Empty row");
        }

        String[] columns = line.split(",", -1);
        if (columns.length != 5) {
            throw new IllegalArgumentException(
                    "Expected 5 columns, got " + columns.length
            );
        }

        String name = columns[0].strip();
        String species = columns[1].strip();
        String ageText = columns[2].strip();
        String vaccinatedText = columns[3].strip();
        String dateText = columns[4].strip();

        if (name.isEmpty() || species.isEmpty()
                || vaccinatedText.isEmpty() || dateText.isEmpty()) {
            throw new IllegalArgumentException("Missing required fields");
        }

        Integer age = ageText.isEmpty() ? null : Integer.valueOf(ageText);

        if (age != null && age <= 0) {
            throw new IllegalArgumentException(
                    "Age must be greater than 0: " + age
            );
        }

        if (!vaccinatedText.equalsIgnoreCase("true")
                && !vaccinatedText.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException(
                    "Invalid vaccinated value: " + vaccinatedText
            );
        }

        return new Animal(
                name,
                species,
                age,
                Boolean.parseBoolean(vaccinatedText),
                LocalDate.parse(dateText, DATE_FORMATTER)
        );
    }
}
