package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analysis.*;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.parser.SleepSessionParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SleepTrackerApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Передайте путь к файлу с логом сна как аргумент командной строки.");
            return;
        }

        try {
            List<SleepingSession> sessions = Files.lines(Path.of(args[0]))
                    .filter(line -> !line.isBlank())
                    .map(SleepSessionParser::parse)
                    .toList();

            List<SleepAnalysis> analyses = List.of(
                    new SessionCountAnalysis(),
                    new MinDurationAnalysis(),
                    new MaxDurationAnalysis(),
                    new AverageDurationAnalysis(),
                    new BadQualityCountAnalysis(),
                    new SleeplessNightsAnalysis(),
                    new ChronotypeAnalysis()
            );

            analyses.stream()
                    .map(analysis -> analysis.analyze(sessions))
                    .forEach(SleepTrackerApp::printResult);

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка обработки данных: " + e.getMessage());
        }
    }

    private static void printResult(SleepAnalysisResult<?> result) {
        System.out.println(result.description() + ": " + result.value());
    }
}