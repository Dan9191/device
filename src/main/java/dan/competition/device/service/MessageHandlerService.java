package dan.competition.device.service;

import dan.competition.device.config.AppProperties;
import dan.competition.device.model.Complications;
import dan.competition.device.model.PatientData;
import dan.competition.device.model.Prediction;
import dan.competition.device.model.websocket.DiagnosisDTO;
import dan.competition.device.model.websocket.MedicalDataWebSocket;
import dan.competition.device.model.websocket.PatientDataWebSocket;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис по обработке данных с приборов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHandlerService {

    /**
     * Отправлятор сообщений по WebSocket.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Конфигурация приложения.
     */
    private final AppProperties appConfig;

    /**
     * Список частот сердечных сокращений плода за текущий период для нахождения среднего значения
     */
    private List<Double> bpmWindow = new ArrayList<>();

    /**
     * Список сократительной активность матки за текущий период для нахождения среднего значения.
     */
    private List<Double> uterusWindow = new ArrayList<>();

    /**
     * Список возможных осложнений.
     */
    private final List<Complications> complicationsList;

    /**
     * Счетчик осложнений.
     */
    private Map<Long, Integer> riskCounters = new ConcurrentHashMap<>();

    /**
     * Номер текущего окна (в 1 минутах от начала)
     */
    private long currentWindowIndex = -1;

    /**
     * Количество проанализированных временных отрезков.
     */
    private int windowCount = 0;

    /**
     * Данные о пациенте.
     */
    private PatientData prevPatientData;

    private Map<String, Object> avgVariables;

    @PostConstruct
    public void init() {
        avgVariables = new HashMap<>();
        //частота сердечных сокращений плода за текущий период
        avgVariables.put("avgBpm", 0.0);
        //сократительная активность матки за текущий период
        avgVariables.put("avgUterus", 0.0);
        //частота сердечных сокращений плода за прошлый период
        avgVariables.put("prevAvgBpm", 0.0);
        //сократительная активность матки за прошлый период
        avgVariables.put("prevAvgUterus", 0.0);
        //частота сердечных сокращений плода за весь период
        avgVariables.put("fullAvgBpm", 0.0);
        //сократительная активность матки за весь период
        avgVariables.put("fullAvgUterus", 0.0);
        // текущий риск осложнений
        avgVariables.put("riskComplications", 0.0);
        // нижняя граница нормы частоты сердечных сокращений плода
        avgVariables.put("bpmMin", 110.0);
        // верхняя граница нормы частоты сердечных сокращений плода
        avgVariables.put("bpmMax", 160.0);
        // нижняя граница нормы сократительной активность матки
        avgVariables.put("uterusMin", 0.0);
        // верхняя граница нормы сократительной активность матки
        avgVariables.put("uterusMax", 60.0);


        prevPatientData = appConfig.getPatientData();
    }

    /**
     * Возврат данных о пациенте.
     *
     * @return Данные текущего пациента.
     */
    public PatientData getPatientData() {
        if (prevPatientData == null) {
            return appConfig.getPatientData();
        }
        return prevPatientData;
    }

    /**
     * Обработка данных о пациенте с приборов.
     *
     * @param patientDataWebSocketInput Данные о пациенте.
     */
    public void handlePatientData(PatientDataWebSocket patientDataWebSocketInput) {
        log.info("Received PatientData: {}", patientDataWebSocketInput);
        PatientData patientData = PatientData.fromWebSocket(patientDataWebSocketInput);

        // Если пришел новый пациент - обнулим текущие данные.
        if (!patientData.getId().equals(prevPatientData.getId())) {
            avgVariables.put("avgBpm", 0.0);
            avgVariables.put("avgUterus", 0.0);
            avgVariables.put("prevAvgBpm", 0.0);
            avgVariables.put("prevAvgUterus", 0.0);
            avgVariables.put("fullAvgBpm", 0.0);
            avgVariables.put("fullAvgUterus", 0.0);
            avgVariables.put("riskComplications", 0.0);
            avgVariables.put("bpmMin", 110.0);
            avgVariables.put("bpmMax", 160.0);
            avgVariables.put("uterusMin", 0.0);
            avgVariables.put("uterusMax", 60.0);

            bpmWindow = new ArrayList<>();
            uterusWindow = new ArrayList<>();
            currentWindowIndex = -1;
            windowCount = 0;
            riskCounters = new ConcurrentHashMap<>();

            // применяем impact-groovy скрипты диагнозов
            List<DiagnosisDTO> diagnoses = patientDataWebSocketInput.getDiagnoses();

            GroovyShell shell = new GroovyShell(new Binding(avgVariables));
            for (DiagnosisDTO diagnosis : diagnoses) {
                String script = diagnosis.getImpact();
                if (script != null && !script.trim().isEmpty() && !script.contains("нет влияния")) {
                    try {
                        shell.evaluate(script);
                        log.info("Applied impact script for diagnosis {}: {}", diagnosis.getName(), script);
                    } catch (Exception e) {
                        log.error("Error executing impact script for {}: {}", diagnosis.getName(), e.getMessage());
                    }
                }
            }

            log.info("Base avgVariables after diagnosis impact: {}", avgVariables);
        }

        prevPatientData = patientData;
    }

    /**
     * Показатели bpm и uterus c приборов.
     *
     * @param medicalData данные с приборов.
     */
    public void handleMedicalData(MedicalDataWebSocket medicalData) {
        log.debug("Received MedicalData: {}", medicalData);
        dataProcessing(medicalData);
        messagingTemplate.convertAndSend("/topic/data", medicalData);
        log.debug("send MedicalData: {}", medicalData);
    }


    /**
     * Обработка получаемых данных.
     *
     * @param medicalData Данные с приборов.
     */
    private void dataProcessing(MedicalDataWebSocket medicalData) {

        // считаем номер 1-минутного окна
        long windowIndex = (long) (medicalData.getTimeSec() / 10.0);

        // инициализация первого окна
        if (currentWindowIndex == -1) {
            currentWindowIndex = windowIndex;
        }

        if (windowIndex != currentWindowIndex) {
            if (!bpmWindow.isEmpty() || !uterusWindow.isEmpty()) {
                closeWindow();
            }
            currentWindowIndex = windowIndex;
            bpmWindow.clear();
            uterusWindow.clear();

            dataAnalysis();
            Prediction prediction = buildPrediction();
            messagingTemplate.convertAndSend("/topic/predictions", prediction);
            log.info("send Predict {}", prediction);
        }

        // добавляем значения в окно, если они валидны
        if (medicalData.getBpm() != 0.0 && medicalData.getBpm() > 0) {
            bpmWindow.add(medicalData.getBpm());
        }
        if (medicalData.getUterus() != 0.0 && medicalData.getUterus() > 0) {
            uterusWindow.add(medicalData.getUterus());
        }
    }


    /**
     * Вычисление средних значений медицинских показателей за отрезок времени.
     * При окончании текущего окна происходит анализ данных для прогнозирования осложнений.
     */
    private void closeWindow() {
        avgVariables.put("prevAvgBpm", avgVariables.get("avgBpm"));
        avgVariables.put("prevAvgUterus", avgVariables.get("avgUterus"));

        avgVariables.put("avgBpm", bpmWindow.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        avgVariables.put("avgUterus", uterusWindow.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));

        windowCount++;
        if (windowCount == 1) {
            avgVariables.put("fullAvgBpm", avgVariables.get("avgBpm"));
            avgVariables.put("fullAvgUterus", avgVariables.get("avgUterus"));
        } else {
            double fullAvgBpm = (double) avgVariables.get("fullAvgBpm");
            double fullAvgUterus = (double) avgVariables.get("fullAvgUterus");
            double avgBpm = (double) avgVariables.get("avgBpm");
            double avgUterus = (double) avgVariables.get("avgUterus");

            avgVariables.put("fullAvgBpm",((fullAvgBpm * (windowCount - 1)) + avgBpm) / windowCount);
            avgVariables.put("fullAvgUterus",((fullAvgUterus * (windowCount - 1)) + avgUterus) / windowCount);
        }

        log.info("Closed 1-min window #{}", windowCount);
    }

    /**
     * Анализирование данных, запущенное в конце текущего периода.
     */
    private void dataAnalysis() {
        for (Complications complication : complicationsList) {
            boolean risk = executeBooleanScript(complication.getCondition(), avgVariables);
            updateCounter(complication.getId(), risk);
        }
    }

    /**
     * Вычисление риска осложнения.
     *
     * @param groovyScript Формула, по которой вычисляется наличие осложнения.
     * @param variables    Данные о текущем состоянии пациента.
     * @return Есть ли риск конкретного осложнения?
     */
    private boolean executeBooleanScript(String groovyScript, Map<String, Object> variables) {
        Binding binding = new Binding();
        variables.forEach(binding::setVariable);

        GroovyShell shell = new GroovyShell(binding);
        Object result = shell.evaluate(groovyScript);

        if (!(result instanceof Boolean)) {
            throw new IllegalArgumentException("Script must return boolean, but got: " + result);
        }
        return (Boolean) result;
    }

    /**
     * Вычисляется счетчик осложнений.
     * Если за текущий период есть риск осложнения, то его счетчик растет на + 1.
     * Если за текущий период нет риска осложнения, то его счетчик уменьшается на 2.
     *
     * @param complicationId Id осложнения.
     * @param risk           Есть ли риск осложнения.
     */
    private void updateCounter(Long complicationId, boolean risk) {
        riskCounters.merge(complicationId, 0, Integer::sum); // инициализация

        riskCounters.computeIfPresent(complicationId, (id, value) -> {
            if (risk) {
                return value + 1;
            } else {
                return Math.max(0, value - 2); // без ухода в минус
            }
        });
    }

    /**
     * Формирование предсказания.
     *
     * @return Предсказание.
     */
    private Prediction buildPrediction() {
        Prediction prediction = new Prediction();
        prediction.setTimestamp(LocalDateTime.now());

        // активные осложнения = все, у кого счетчик > 0
        List<String> activeComplications = complicationsList.stream()
                .filter(c -> riskCounters.getOrDefault(c.getId(), 0) > 0)
                .map(Complications::getName)
                .toList();

        if (activeComplications.isEmpty()) {
            prediction.setMessage("Прогноз положительный");
        } else {
            prediction.setMessage("Активные осложнения: " + String.join(", ", activeComplications));
        }

        // суммарный риск
        int total = riskCounters.values().stream().mapToInt(Integer::intValue).sum();

        if (total > 6) {
            prediction.setSeverity("negative");
        } else if (total == 0) {
            prediction.setSeverity("positive");
        } else {
            prediction.setSeverity("normal");
        }

        // базовый риск из avgVariables
        double baseRisk = (double) avgVariables.getOrDefault("riskComplications", 0.0);

        // динамический риск (каждый балл = +5%)
        double dynamicRisk = Math.min(100.0, total * 5.0);

        // итоговый риск (ограничим от 0 до 100)
        double finalRisk = Math.min(100.0, baseRisk + dynamicRisk);
        prediction.setRiskComplication(finalRisk);

        return prediction;
    }
}
