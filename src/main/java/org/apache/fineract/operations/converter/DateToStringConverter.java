package org.apache.fineract.operations.converter;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.Converter;

import java.lang.reflect.Field;
import java.util.Date;

import static org.apache.fineract.core.service.OperatorUtils.formatDate;
import static org.apache.fineract.core.service.OperatorUtils.formatDateTime;

public class DateToStringConverter implements Converter<Date, String> {

    @Override
    public String convert(MappingContext<Date, String> mappingContext) {
        Date source = mappingContext.getSource();
        if (source == null) {
            return null;
        }

        Field field = getFieldFromSource(mappingContext);

        if (field != null && field.isAnnotationPresent(Temporal.class)) {
            TemporalType temporalType = field.getAnnotation(Temporal.class).value();
            if (temporalType == TemporalType.TIMESTAMP) {
                return formatDateTime(source);
            } else if (temporalType == TemporalType.DATE) {
                return formatDate(source);
            }
        }

        return formatDateTime(source);
    }

    private Field getFieldFromSource(MappingContext<Date, String> context) {
        try {
            return context.getParent().getSource().getClass().getDeclaredField(context.getMapping().getLastDestinationProperty().getName());
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
