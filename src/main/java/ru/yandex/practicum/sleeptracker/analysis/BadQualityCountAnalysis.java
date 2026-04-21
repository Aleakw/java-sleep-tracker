package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.enums.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

public class BadQualityCountAnalysis implements SleepAnalysis {

    @Override
    public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
        long count = sessions.stream()
                .filter(session -> session.getQuality() == SleepQuality.BAD)
                .count();

        return new SleepAnalysisResult<>("Количество сессий с плохим качеством сна", count);
    }
}