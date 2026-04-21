package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SleeplessNightsAnalysis implements SleepAnalysis {

    @Override
    public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        LocalDate firstNight = getPotentialNightDate(sessions.get(0).getStart());
        LocalDate lastNight = getPotentialNightDate(sessions.get(sessions.size() - 1).getEnd().minusMinutes(1));

        long totalNights = ChronoUnit.DAYS.between(firstNight, lastNight.plusDays(1));

        Set<LocalDate> nightsWithSleep = sessions.stream()
                .flatMap(this::coveredNightDates)
                .collect(Collectors.toSet());

        long sleeplessNights = totalNights - nightsWithSleep.size();

        return new SleepAnalysisResult<>("Количество бессонных ночей", sleeplessNights);
    }

    private Stream<LocalDate> coveredNightDates(SleepingSession session) {
        LocalDate candidateNight = getPotentialNightDate(session.getStart());

        return Stream.of(candidateNight)
                .filter(nightDate -> intersectsNightWindow(session, nightDate));
    }

    private LocalDate getPotentialNightDate(LocalDateTime dateTime) {
        return dateTime.toLocalTime().isAfter(LocalTime.NOON) || dateTime.toLocalTime().equals(LocalTime.NOON)
                ? dateTime.toLocalDate().plusDays(1)
                : dateTime.toLocalDate();
    }

    private boolean intersectsNightWindow(SleepingSession session, LocalDate nightDate) {
        LocalDateTime nightStart = nightDate.atStartOfDay();
        LocalDateTime nightEnd = nightDate.atTime(6, 0);

        return session.getStart().isBefore(nightEnd) && session.getEnd().isAfter(nightStart);
    }
}