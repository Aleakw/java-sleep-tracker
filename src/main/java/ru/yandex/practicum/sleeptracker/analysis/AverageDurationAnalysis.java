package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

public class AverageDurationAnalysis implements SleepAnalysis {

    @Override
    public SleepAnalysisResult<Double> analyze(List<SleepingSession> sessions) {
        double average = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .average()
                .orElse(0.0);

        return new SleepAnalysisResult<>("Средняя продолжительность сессии (мин)", average);
    }
}