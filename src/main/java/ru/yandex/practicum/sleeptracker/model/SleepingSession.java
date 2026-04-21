package ru.yandex.practicum.sleeptracker.model;

import ru.yandex.practicum.sleeptracker.enums.SleepQuality;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SleepingSession {
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime start, LocalDateTime end, SleepQuality quality) {
        if (start == null || end == null || quality == null) {
            throw new IllegalArgumentException("Поля сессии сна не должны быть null");
        }

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Время окончания должно быть позже времени начала");
        }

        this.start = start;
        this.end = end;
        this.quality = quality;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public long getDurationInMinutes() {
        return ChronoUnit.MINUTES.between(start, end);
    }
}