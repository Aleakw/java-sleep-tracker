package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.Comparator;
import java.util.List;

public class MinDurationAnalysis implements SleepAnalysis {

    @Override
    public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
        long min = sessions.stream()
                .map(SleepingSession::getDurationInMinutes)
                .min(Comparator.naturalOrder())
                .orElse(0L);

        return new SleepAnalysisResult<>("Минимальная продолжительность сессии (мин)", min);
    }
}