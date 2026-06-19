package org.example.accessbasededonnees.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RevokedTokenRedisConfig {
    private final RevokeTokenProperties revokeTokenProperties;


    @Autowired
    public RevokedTokenRedisConfig(RevokeTokenProperties revokeTokenProperties) {
        this.revokeTokenProperties = revokeTokenProperties;
    }


    @Bean("revokeTokenConnectionFactory")
    public LettuceConnectionFactory revokeTokenFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration =  new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setUsername(revokeTokenProperties.getUsername());
        redisStandaloneConfiguration.setPassword(revokeTokenProperties.getPassword());
        redisStandaloneConfiguration.setDatabase(revokeTokenProperties.getDatabase());
        redisStandaloneConfiguration.setPort(revokeTokenProperties.getPort());
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .shutdownTimeout(Duration.ZERO)
                .build();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration) ;
    }


    @Bean("revokeTokenRedisTemplate")
    public RedisTemplate<String, String> revokedTokenRedisTemplate(
            @Qualifier("revokeTokenConnectionFactory") LettuceConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }


}
