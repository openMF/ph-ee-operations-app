package org.apache.fineract.operations.converter;

import org.modelmapper.spi.MappingContext;
import org.modelmapper.Converter;

import java.util.Date;

import static org.apache.fineract.core.service.OperatorUtils.dateFormat;

public class DateToStringConverter implements Converter<Date, String> {

    @Override
    public String convert(MappingContext<Date, String> mappingContext) {
        if (mappingContext.getSource() == null) {
            return null;
        }
        return dateFormat().format(mappingContext.getSource());
    }
}
