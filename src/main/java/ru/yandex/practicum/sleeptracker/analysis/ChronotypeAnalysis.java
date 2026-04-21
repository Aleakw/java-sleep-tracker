package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.enums.Chronotype;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChronotypeAnalysis implements SleepAnalysis {

    @Override
    public SleepAnalysisResult<Chronotype> analyze(List<SleepingSession> sessions) {
        Map<LocalDate, List<SleepingSession>> nightSessions = sessions.stream()
                .filter(this::isNightSession)
                .collect(Collectors.groupingBy(this::getNightKey));

        long owlCount = nightSessions.values().stream()
                .map(this::classifyNight)
                .filter(type -> type == Chronotype.OWL)
                .count();

        long larkCount = nightSessions.values().stream()
                .map(this::classifyNight)
                .filter(type -> type == Chronotype.LARK)
                .count();

        long doveCount = nightSessions.values().stream()
                .map(this::classifyNight)
                .filter(type -> type == Chronotype.DOVE)
                .count();

        Chronotype result = resolveFinalType(owlCount, larkCount, doveCount);

        return new SleepAnalysisResult<>("Хронотип пользователя", result);
    }

    private boolean isNightSession(SleepingSession session) {
        return intersectsNightWindow(session, getNightKey(session));
    }

    private LocalDate getNightKey(SleepingSession session) {
        return session.getStart().toLocalTime().isAfter(LocalTime.NOON) || session.getStart().toLocalTime().equals(LocalTime.NOON)
                ? session.getStart().toLocalDate().plusDays(1)
                : session.getStart().toLocalDate();
    }

    private boolean intersectsNightWindow(SleepingSession session, LocalDate nightDate) {
        LocalDateTime nightStart = nightDate.atStartOfDay();
        LocalDateTime nightEnd = nightDate.atTime(6, 0);

        return session.getStart().isBefore(nightEnd) && session.getEnd().isAfter(nightStart);
    }

    private Chronotype classifyNight(List<SleepingSession> sessionsOfNight) {
        LocalDateTime firstStart = sessionsOfNight.stream()
                .map(SleepingSession::getStart)
                .min(Comparator.naturalOrder())
                .orElseThrow();

        LocalDateTime lastEnd = sessionsOfNight.stream()
                .map(SleepingSession::getEnd)
                .max(Comparator.naturalOrder())
                .orElseThrow();

        LocalTime sleepTime = firstStart.toLocalTime();
        LocalTime wakeTime = lastEnd.toLocalTime();

        boolean isOwl = sleepTime.isAfter(LocalTime.of(23, 0))
                && wakeTime.isAfter(LocalTime.of(9, 0));

        boolean isLark = sleepTime.isBefore(LocalTime.of(22, 0))
                && wakeTime.isBefore(LocalTime.of(7, 0));

        if (isOwl) {
            return Chronotype.OWL;
        }

        if (isLark) {
            return Chronotype.LARK;
        }

        return Chronotype.DOVE;
    }

    private Chronotype resolveFinalType(long owlCount, long larkCount, long doveCount) {
        long max = Stream.of(owlCount, larkCount, doveCount)
                .max(Long::compareTo)
                .orElse(0L);

        long winners = Stream.of(owlCount, larkCount, doveCount)
                .filter(count -> count == max)
                .count();

        if (winners > 1) {
            return Chronotype.DOVE;
        }

        if (max == owlCount) {
            return Chronotype.OWL;
        }

        if (max == larkCount) {
            return Chronotype.LARK;
        }

        return Chronotype.DOVE;
    }
}