package de.marcschuler.onyxserver.data.converter;

import de.marcschuler.onyxserver.Util;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of strings as a JSON array in a single column
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final TypeReference<List<String>> TYPE = new TypeReference<>() {
    };

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return Util.objectMapper.writeValueAsString(attribute == null ? List.of() : attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank())
            return new ArrayList<>();
        return new ArrayList<>(Util.objectMapper.readValue(dbData, TYPE));
    }
}
