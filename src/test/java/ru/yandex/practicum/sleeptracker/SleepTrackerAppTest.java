package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analysis.AverageDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.BadQualityCountAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.ChronotypeAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MaxDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MinDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.SessionCountAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.SleeplessNightsAnalysis;
import ru.yandex.practicum.sleeptracker.enums.Chronotype;
import ru.yandex.practicum.sleeptracker.enums.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.parser.SleepSessionParser;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepTrackerAppTest {

    @Test
    void shouldParseSessionCorrectly() {
        SleepingSession session =
                SleepSessionParser.parse("01.10.25 23:15;02.10.25 07:30;GOOD");

        assertEquals(LocalDateTime.of(2025, 10, 1, 23, 15), session.getStart());
        assertEquals(LocalDateTime.of(2025, 10, 2, 7, 30), session.getEnd());
        assertEquals(SleepQuality.GOOD, session.getQuality());
    }

    @Test
    void shouldCountSessions() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 15, 2025, 10, 2, 7, 30, SleepQuality.GOOD),
                session(2025, 10, 2, 23, 50, 2025, 10, 3, 6, 40, SleepQuality.NORMAL)
        );

        long result = new SessionCountAnalysis().analyze(sessions).value();

        assertEquals(2L, result);
    }

    @Test
    void shouldReturnMinimumDuration() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 15, 2025, 10, 2, 7, 30, SleepQuality.GOOD),   // 495
                session(2025, 10, 2, 13, 0, 2025, 10, 2, 13, 45, SleepQuality.NORMAL), // 45
                session(2025, 10, 2, 23, 50, 2025, 10, 3, 6, 40, SleepQuality.NORMAL)  // 410
        );

        long result = new MinDurationAnalysis().analyze(sessions).value();

        assertEquals(45L, result);
    }

    @Test
    void shouldReturnMaximumDuration() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 15, 2025, 10, 2, 7, 30, SleepQuality.GOOD),   // 495
                session(2025, 10, 2, 13, 0, 2025, 10, 2, 13, 45, SleepQuality.NORMAL), // 45
                session(2025, 10, 2, 23, 40, 2025, 10, 3, 8, 0, SleepQuality.BAD)      // 500
        );

        long result = new MaxDurationAnalysis().analyze(sessions).value();

        assertEquals(500L, result);
    }

    @Test
    void shouldReturnAverageDuration() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 15, 2025, 10, 2, 7, 30, SleepQuality.GOOD),   // 495
                session(2025, 10, 2, 13, 0, 2025, 10, 2, 13, 45, SleepQuality.NORMAL), // 45
                session(2025, 10, 2, 23, 50, 2025, 10, 3, 6, 40, SleepQuality.NORMAL)  // 410
        );

        double result = new AverageDurationAnalysis().analyze(sessions).value();

        assertEquals((495.0 + 45.0 + 410.0) / 3.0, result);
    }

    @Test
    void shouldCountBadQualitySessions() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 15, 2025, 10, 2, 7, 30, SleepQuality.GOOD),
                session(2025, 10, 2, 23, 40, 2025, 10, 3, 8, 0, SleepQuality.BAD),
                session(2025, 10, 3, 23, 50, 2025, 10, 4, 6, 40, SleepQuality.BAD)
        );

        long result = new BadQualityCountAnalysis().analyze(sessions).value();

        assertEquals(2L, result);
    }

    @Test
    void shouldReturnZeroSleeplessNightsWhenEachNightHasSleep() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 15, 2025, 10, 2, 7, 30, SleepQuality.GOOD),
                session(2025, 10, 2, 23, 50, 2025, 10, 3, 6, 40, SleepQuality.NORMAL),
                session(2025, 10, 3, 23, 40, 2025, 10, 4, 8, 0, SleepQuality.BAD)
        );

        long result = new SleeplessNightsAnalysis().analyze(sessions).value();

        assertEquals(0L, result);
    }

    @Test
    void shouldCountOneSleeplessNight() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 15, 2025, 10, 2, 7, 30, SleepQuality.GOOD),
                session(2025, 10, 3, 23, 40, 2025, 10, 4, 8, 0, SleepQuality.BAD)
        );

        long result = new SleeplessNightsAnalysis().analyze(sessions).value();

        assertEquals(1L, result);
    }

    @Test
    void shouldNotTreatNightSleepAsSleepless() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 0, 2025, 10, 2, 3, 0, SleepQuality.GOOD)
        );

        long result = new SleeplessNightsAnalysis().analyze(sessions).value();

        assertEquals(0L, result);
    }

    @Test
    void shouldTreatDaySleepAsSleeplessNight() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 2, 7, 0, 2025, 10, 2, 11, 0, SleepQuality.NORMAL)
        );

        long result = new SleeplessNightsAnalysis().analyze(sessions).value();

        assertEquals(1L, result);
    }

    @Test
    void shouldTreatSleepStartingAfterNoonAsNextNightCandidate() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 17, 0, 2025, 10, 1, 23, 0, SleepQuality.NORMAL)
        );

        long result = new SleeplessNightsAnalysis().analyze(sessions).value();

        assertEquals(1L, result);
    }

    @Test
    void shouldDetectOwlChronotype() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 50, 2025, 10, 2, 10, 10, SleepQuality.GOOD),
                session(2025, 10, 2, 23, 40, 2025, 10, 3, 9, 30, SleepQuality.NORMAL),
                session(2025, 10, 3, 23, 45, 2025, 10, 4, 9, 20, SleepQuality.NORMAL)
        );

        Chronotype result = new ChronotypeAnalysis().analyze(sessions).value();

        assertEquals(Chronotype.OWL, result);
    }

    @Test
    void shouldDetectLarkChronotype() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 21, 30, 2025, 10, 2, 6, 20, SleepQuality.GOOD),
                session(2025, 10, 2, 21, 40, 2025, 10, 3, 6, 10, SleepQuality.NORMAL),
                session(2025, 10, 3, 21, 50, 2025, 10, 4, 6, 30, SleepQuality.NORMAL)
        );

        Chronotype result = new ChronotypeAnalysis().analyze(sessions).value();

        assertEquals(Chronotype.LARK, result);
    }

    @Test
    void shouldReturnDoveWhenTypesAreMixed() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 23, 50, 2025, 10, 2, 10, 10, SleepQuality.GOOD),   // owl
                session(2025, 10, 2, 21, 30, 2025, 10, 3, 6, 20, SleepQuality.NORMAL)   // lark
        );

        Chronotype result = new ChronotypeAnalysis().analyze(sessions).value();

        assertEquals(Chronotype.DOVE, result);
    }

    @Test
    void shouldIgnoreDaySleepForChronotype() {
        List<SleepingSession> sessions = List.of(
                session(2025, 10, 1, 13, 0, 2025, 10, 1, 14, 0, SleepQuality.NORMAL),   // day sleep
                session(2025, 10, 1, 23, 50, 2025, 10, 2, 10, 10, SleepQuality.GOOD),
                session(2025, 10, 2, 23, 40, 2025, 10, 3, 9, 30, SleepQuality.NORMAL)
        );

        Chronotype result = new ChronotypeAnalysis().analyze(sessions).value();

        assertEquals(Chronotype.OWL, result);
    }

    private SleepingSession session(
            int startYear, int startMonth, int startDay, int startHour, int startMinute,
            int endYear, int endMonth, int endDay, int endHour, int endMinute,
            SleepQuality quality
    ) {
        return new SleepingSession(
                LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute),
                LocalDateTime.of(endYear, endMonth, endDay, endHour, endMinute),
                quality
        );
    }
}