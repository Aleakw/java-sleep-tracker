package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepTrackerAppTest {

    @Test
    void shouldParseSessionCorrectly() {
        SleepTrackerApp.SleepingSession session =
                SleepTrackerApp.parseSession("01.10.25 23:15;02.10.25 07:30;GOOD");

        assertEquals(LocalDateTime.of(2025, 10, 1, 23, 15), session.getStart());
        assertEquals(LocalDateTime.of(2025, 10, 2, 7, 30), session.getEnd());
        assertEquals(SleepTrackerApp.SleepQuality.GOOD, session.getQuality());
    }

    @Test
    void shouldCountSessions() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 15),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepTrackerApp.SleepQuality.GOOD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 50),
                        LocalDateTime.of(2025, 10, 3, 6, 40),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.SessionCountAnalysis analysis = new SleepTrackerApp.SessionCountAnalysis();

        assertEquals(2L, analysis.apply(sessions).value());
    }

    @Test
    void shouldReturnMinimumDuration() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 15),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepTrackerApp.SleepQuality.GOOD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 10),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.MinDurationAnalysis analysis = new SleepTrackerApp.MinDurationAnalysis();

        assertEquals(50L, analysis.apply(sessions).value());
    }

    @Test
    void shouldReturnMaximumDuration() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 15),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepTrackerApp.SleepQuality.GOOD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 10),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.MaxDurationAnalysis analysis = new SleepTrackerApp.MaxDurationAnalysis();

        assertEquals(495L, analysis.apply(sessions).value());
    }

    @Test
    void shouldReturnAverageDuration() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 15),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepTrackerApp.SleepQuality.GOOD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 10),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.AverageDurationAnalysis analysis = new SleepTrackerApp.AverageDurationAnalysis();

        assertEquals(272.5, analysis.apply(sessions).value());
    }

    @Test
    void shouldCountBadQualitySessions() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 15),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepTrackerApp.SleepQuality.BAD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 50),
                        LocalDateTime.of(2025, 10, 3, 6, 40),
                        SleepTrackerApp.SleepQuality.NORMAL
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 23, 40),
                        LocalDateTime.of(2025, 10, 4, 8, 0),
                        SleepTrackerApp.SleepQuality.BAD
                )
        );

        SleepTrackerApp.BadQualityCountAnalysis analysis = new SleepTrackerApp.BadQualityCountAnalysis();

        assertEquals(2L, analysis.apply(sessions).value());
    }

    @Test
    void shouldReturnZeroSleeplessNightsWhenEachNightHasSleep() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 15),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepTrackerApp.SleepQuality.GOOD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 50),
                        LocalDateTime.of(2025, 10, 3, 6, 40),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.SleeplessNightsAnalysis analysis = new SleepTrackerApp.SleeplessNightsAnalysis();

        assertEquals(0L, analysis.apply(sessions).value());
    }

    @Test
    void shouldCountOneSleeplessNight() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 15),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepTrackerApp.SleepQuality.GOOD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 7, 0),
                        LocalDateTime.of(2025, 10, 3, 11, 0),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.SleeplessNightsAnalysis analysis = new SleepTrackerApp.SleeplessNightsAnalysis();

        assertEquals(1L, analysis.apply(sessions).value());
    }

    @Test
    void shouldNotTreatNightSleepAsSleepless() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 2, 0),
                        LocalDateTime.of(2025, 10, 2, 5, 0),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.SleeplessNightsAnalysis analysis = new SleepTrackerApp.SleeplessNightsAnalysis();

        assertEquals(0L, analysis.apply(sessions).value());
    }

    @Test
    void shouldTreatDaySleepAsSleeplessNight() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        LocalDateTime.of(2025, 10, 2, 11, 0),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.SleeplessNightsAnalysis analysis = new SleepTrackerApp.SleeplessNightsAnalysis();

        assertEquals(1L, analysis.apply(sessions).value());
    }

    @Test
    void shouldTreatSleepStartingAfterNoonAsNextNightCandidate() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 5, 23, 30),
                        LocalDateTime.of(2025, 10, 6, 7, 0),
                        SleepTrackerApp.SleepQuality.GOOD
                )
        );

        SleepTrackerApp.SleeplessNightsAnalysis analysis = new SleepTrackerApp.SleeplessNightsAnalysis();

        assertEquals(0L, analysis.apply(sessions).value());
    }

    @Test
    void shouldDetectOwlChronotype() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepTrackerApp.SleepQuality.GOOD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 40),
                        LocalDateTime.of(2025, 10, 3, 9, 45),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.ChronotypeAnalysis analysis = new SleepTrackerApp.ChronotypeAnalysis();

        assertEquals(SleepTrackerApp.Chronotype.OWL, analysis.apply(sessions).value());
    }

    @Test
    void shouldDetectLarkChronotype() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 21, 15),
                        LocalDateTime.of(2025, 10, 2, 6, 20),
                        SleepTrackerApp.SleepQuality.GOOD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 21, 30),
                        LocalDateTime.of(2025, 10, 3, 6, 30),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.ChronotypeAnalysis analysis = new SleepTrackerApp.ChronotypeAnalysis();

        assertEquals(SleepTrackerApp.Chronotype.LARK, analysis.apply(sessions).value());
    }

    @Test
    void shouldReturnDoveWhenTypesAreMixed() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 30),
                        LocalDateTime.of(2025, 10, 2, 8, 0),
                        SleepTrackerApp.SleepQuality.GOOD
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 30),
                        LocalDateTime.of(2025, 10, 3, 8, 30),
                        SleepTrackerApp.SleepQuality.NORMAL
                )
        );

        SleepTrackerApp.ChronotypeAnalysis analysis = new SleepTrackerApp.ChronotypeAnalysis();

        assertEquals(SleepTrackerApp.Chronotype.DOVE, analysis.apply(sessions).value());
    }

    @Test
    void shouldIgnoreDaySleepForChronotype() {
        List<SleepTrackerApp.SleepingSession> sessions = List.of(
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 15, 0),
                        SleepTrackerApp.SleepQuality.NORMAL
                ),
                new SleepTrackerApp.SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 21, 15),
                        LocalDateTime.of(2025, 10, 2, 6, 20),
                        SleepTrackerApp.SleepQuality.GOOD
                )
        );

        SleepTrackerApp.ChronotypeAnalysis analysis = new SleepTrackerApp.ChronotypeAnalysis();

        assertEquals(SleepTrackerApp.Chronotype.LARK, analysis.apply(sessions).value());
    }
}