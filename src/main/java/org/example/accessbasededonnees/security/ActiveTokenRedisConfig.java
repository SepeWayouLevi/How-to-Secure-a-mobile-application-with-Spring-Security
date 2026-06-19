package org.example.accessbasededonnees.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;

@Configuration
public class ActiveTokenRedisConfig {

    private final ActiveTokenProperties activeTokenProperties;

    @Autowired
    public ActiveTokenRedisConfig(ActiveTokenProperties activeTokenProperties){
        this.activeTokenProperties = activeTokenProperties;
    }

    @Bean("activeTokenConnectionFactory")
    public LettuceConnectionFactory activeTokenConnectionFactory(){
        RedisStandaloneConfiguration myredisStandloneConfiguration =  new RedisStandaloneConfiguration();
        myredisStandloneConfiguration.setUsername(activeTokenProperties.getUsername());
        myredisStandloneConfiguration.setPassword(activeTokenProperties.getPassword());
        myredisStandloneConfiguration.setDatabase(activeTokenProperties.getDatabase());
        myredisStandloneConfiguration.setPort(activeTokenProperties.getPort());
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .shutdownTimeout(Duration.ZERO)
                .build();
        return new LettuceConnectionFactory(myredisStandloneConfiguration, clientConfiguration) ;
    }


    @Bean("activeTokenRedisTemplate")
    public RedisTemplate<String, String> activeTokenRedisTemplate(
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(activeTokenConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
