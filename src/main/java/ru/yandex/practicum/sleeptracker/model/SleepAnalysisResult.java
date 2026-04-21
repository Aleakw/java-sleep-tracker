package ru.yandex.practicum.sleeptracker.model;

public record SleepAnalysisResult<T>(String description, T value) {
}