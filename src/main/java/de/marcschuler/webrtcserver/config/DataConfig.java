package de.marcschuler.webrtcserver.config;

import de.marcschuler.webrtcserver.data.Hash;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {

    @Converter(autoApply = true)
    public static class HashConverter implements AttributeConverter<Hash,String>{

        @Override
        public String convertToDatabaseColumn(Hash attribute) {
            return attribute.getType().name() + ":" + attribute.getHash();
        }

        @Override
        public Hash convertToEntityAttribute(String dbData) {
            var split = dbData.split(":");
            var type = Hash.HashType.valueOf(split[0]);
            var hash = split[1];
            return new Hash(type,hash);
        }
    }
}
