package andrei.chirila.curatedeck.infrastructure.converters;

import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public @Nullable LocalDate convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        YearMonth yearMonth = YearMonth.parse(source, formatter);
        return  yearMonth.atDay(1);
    }
}