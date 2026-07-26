package de.marcschuler.onyxserver.config;

import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.onyxserver.data.file.Hash;
import de.marcschuler.onyxserver.service.CryptoService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;

@Configuration
public class DataConfig {

    @Converter(autoApply = true)
    public static class HashConverter implements AttributeConverter<Hash, String> {

        @Override
        public String convertToDatabaseColumn(Hash attribute) {
            return attribute.getType().name() + ":" + attribute.getHash();
        }

        @Override
        public Hash convertToEntityAttribute(String dbData) {
            var split = dbData.split(":");
            var type = Hash.HashType.valueOf(split[0]);
            var hash = split[1];
            return new Hash(type, hash);
        }
    }

    @Converter(autoApply = true)
    @RequiredArgsConstructor
    public static class KeyPairJWKConverter implements AttributeConverter<OctetKeyPair, String> {

        private final CryptoService cryptoService;

        @Override
        public String convertToDatabaseColumn(OctetKeyPair keyPair) {
            return keyPair.toJSONString();
        }

        @Override
        public OctetKeyPair convertToEntityAttribute(String dbData) {
            try {
                return cryptoService.importKeyPair(dbData);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
