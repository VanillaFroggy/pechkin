package ru.intech.pechkin.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.intech.pechkin.config.converter.ZonedDateTimeReadConverter;
import ru.intech.pechkin.config.converter.ZonedDateTimeWriteConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableTransactionManagement
@EnableMongoRepositories(basePackages = {
        "ru.intech.pechkin.messenger.infrastructure.persistence.repo",
        "ru.intech.pechkin.corporate.infrastructure.persistence.repo"
})
public class MongoConfig extends AbstractMongoClientConfiguration {
    private final List<Converter<?, ?>> converters = new ArrayList<>();

    @Bean
    public PlatformTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @NonNull
    @Override
    protected String getDatabaseName() {
        return "messenger";
    }

    @NonNull
    @Override
    public MongoCustomConversions customConversions() {
        converters.add(new ZonedDateTimeReadConverter());
        converters.add(new ZonedDateTimeWriteConverter());
        return new MongoCustomConversions(converters);
    }
}
