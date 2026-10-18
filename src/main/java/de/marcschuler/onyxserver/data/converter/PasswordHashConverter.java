package de.marcschuler.onyxserver.data.converter;

import de.marcschuler.onyxserver.service.CryptoService;
import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PasswordHashConverter implements AttributeConverter<String,String> {

    private final CryptoService cryptoService;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute==null)
            return null;
        if (attribute.startsWith(CryptoService.PASSWORD_HASH_ARG2_PREFIX)) //TODO might break if user enters "arg2:...." as password?
            return attribute;
        return cryptoService.hashPassword(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
