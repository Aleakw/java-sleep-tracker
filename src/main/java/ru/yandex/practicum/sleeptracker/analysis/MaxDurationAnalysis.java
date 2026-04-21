package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.Comparator;
import java.util.List;

public class MaxDurationAnalysis implements SleepAnalysis {

    @Override
    public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
        long max = sessions.stream()
                .map(SleepingSession::getDurationInMinutes)
                .max(Comparator.naturalOrder())
                .orElse(0L);

        return new SleepAnalysisResult<>("Максимальная продолжительность сессии (мин)", max);
    }
}