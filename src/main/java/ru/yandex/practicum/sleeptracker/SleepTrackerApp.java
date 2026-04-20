package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SleepTrackerApp {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Передайте путь к файлу с логом сна как аргумент командной строки.");
            return;
        }

        try {
            List<SleepingSession> sessions = Files.lines(Path.of(args[0]))
                    .filter(line -> !line.isBlank())
                    .map(SleepTrackerApp::parseSession)
                    .toList();

            List<Function<List<SleepingSession>, ? extends SleepAnalysisResult<?>>> analyses = List.of(
                    new SessionCountAnalysis(),
                    new MinDurationAnalysis(),
                    new MaxDurationAnalysis(),
                    new AverageDurationAnalysis(),
                    new BadQualityCountAnalysis(),
                    new SleeplessNightsAnalysis(),
                    new ChronotypeAnalysis()
            );

            analyses.stream()
                    .map(analysis -> analysis.apply(sessions))
                    .forEach(result -> System.out.println(result.description() + ": " + result.value()));

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка обработки данных: " + e.getMessage());
        }
    }

    static SleepingSession parseSession(String line) {
        String[] parts = line.split(";");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректная строка: " + line);
        }

        LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

        return new SleepingSession(start, end, quality);
    }

    enum SleepQuality {
        GOOD,
        NORMAL,
        BAD
    }

    enum Chronotype {
        OWL,
        LARK,
        DOVE
    }

    record SleepAnalysisResult<T>(String description, T value) {
    }

    static class SleepingSession {
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final SleepQuality quality;

        SleepingSession(LocalDateTime start, LocalDateTime end, SleepQuality quality) {
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

    static class SessionCountAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {
        @Override
        public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
            return new SleepAnalysisResult<>("Общее количество сессий сна", (long) sessions.size());
        }
    }

    static class MinDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {
        @Override
        public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
            long min = sessions.stream()
                    .map(SleepingSession::getDurationInMinutes)
                    .min(Comparator.naturalOrder())
                    .orElse(0L);

            return new SleepAnalysisResult<>("Минимальная продолжительность сессии (мин)", min);
        }
    }

    static class MaxDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {
        @Override
        public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
            long max = sessions.stream()
                    .map(SleepingSession::getDurationInMinutes)
                    .max(Comparator.naturalOrder())
                    .orElse(0L);

            return new SleepAnalysisResult<>("Максимальная продолжительность сессии (мин)", max);
        }
    }

    static class AverageDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Double>> {
        @Override
        public SleepAnalysisResult<Double> apply(List<SleepingSession> sessions) {
            double average = sessions.stream()
                    .mapToLong(SleepingSession::getDurationInMinutes)
                    .average()
                    .orElse(0.0);

            return new SleepAnalysisResult<>("Средняя продолжительность сессии (мин)", average);
        }
    }

    static class BadQualityCountAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {
        @Override
        public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
            long count = sessions.stream()
                    .filter(session -> session.getQuality() == SleepQuality.BAD)
                    .count();

            return new SleepAnalysisResult<>("Количество сессий с плохим качеством сна", count);
        }
    }

    static class SleeplessNightsAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {
        @Override
        public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
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

    static class ChronotypeAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Chronotype>> {
        @Override
        public SleepAnalysisResult<Chronotype> apply(List<SleepingSession> sessions) {
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
                    .min(LocalDateTime::compareTo)
                    .orElseThrow();

            LocalDateTime lastEnd = sessionsOfNight.stream()
                    .map(SleepingSession::getEnd)
                    .max(LocalDateTime::compareTo)
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
}