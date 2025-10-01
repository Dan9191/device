package dan.competition.device.config;

import dan.competition.device.model.Complications;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация приложения.
 */
@Configuration
public class AppConfig {

    @Bean
    public List<Complications> complicationsList() {
        return List.of(
                new Complications(1L,
                        "Децелерация",
                        "Эпизод снижения частоты сердечных сокращений (ЧСС) плода на 15 ударов в минуту и более продолжительностью от 15 секунд",
                        "false"
                ),
                new Complications(2L,
                        "Тахикардия",
                        "Увеличение частоты сердечных сокращений",
                        "avgBpm > bpmMax"
                ),
                new Complications(3L,
                        "Брадикардия",
                        "Замедление частоты сердечных сокращений",
                        "avgBpm < bpmMin"
                ),
                new Complications(4L,
                        "Вариабельность сердечного ритма",
                        "Показатель, который отражает разницу во времени между последовательными ударами сердца",
                        "false"
                )
        );
    }
}
