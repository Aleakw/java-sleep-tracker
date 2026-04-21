package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

public class SessionCountAnalysis implements SleepAnalysis {

    @Override
    public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
        return new SleepAnalysisResult<>("Общее количество сессий сна", (long) sessions.size());
    }
}