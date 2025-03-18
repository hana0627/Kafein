package com.hana.baseproject.core.config

import io.lettuce.core.RedisURI
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories // redisRepository 쓸거야!
class RedisConfig (
    private val redisProperties: RedisProperties,
){


    // 커넥션 정보 설정
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        // redisProperties.host와 redisProperties.port 사용
        val configuration = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
        return LettuceConnectionFactory(configuration)
    }

    // RedisTemplate 설정
    @Bean
    fun userRedisTemplate(): RedisTemplate<String, String> {
        val redisTemplate: RedisTemplate<String, String> = RedisTemplate()
        redisTemplate.connectionFactory = redisConnectionFactory()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(String::class.java)
        return redisTemplate
    }

}