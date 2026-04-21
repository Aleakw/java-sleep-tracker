package ru.yandex.practicum.sleeptracker.parser;

import ru.yandex.practicum.sleeptracker.enums.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SleepSessionParser {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private SleepSessionParser() {
    }

    public static SleepingSession parse(String line) {
        String[] parts = line.split(";");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректная строка: " + line);
        }

        LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

        return new SleepingSession(start, end, quality);
    }
}