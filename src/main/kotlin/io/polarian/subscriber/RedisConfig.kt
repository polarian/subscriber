package io.polarian.subscriber

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

class RedisProperties() {
    lateinit var host: String
    lateinit var port: String
    lateinit var password: String
}

@Configuration
class RedisConfig {

    @Bean("myProperties")
    @ConfigurationProperties(prefix = "my.redis")
    fun getRedisProperties(): RedisProperties {
        return RedisProperties()
    }

    @Bean("myExecutor")
    @ConfigurationProperties(prefix = "my.pool")
    fun taskExecutor(): Executor {
        return ThreadPoolTaskExecutor()
    }

    @Bean("myConnectionFactory")
    fun redisConnectionFactory(@Qualifier("myProperties") redisProperties: RedisProperties): RedisConnectionFactory {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(redisProperties.host, redisProperties.port.toInt()).apply {
            password = RedisPassword.of(redisProperties.password)
        })
    }

    @Bean("myContainer")
    fun redisContainer(@Qualifier("myConnectionFactory") redisConnectionFactory: RedisConnectionFactory, @Qualifier("myExecutor") executor: Executor): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            setConnectionFactory(redisConnectionFactory)
            addMessageListener(MessageListenerAdapter(RedisMessageSubscriber()), ChannelTopic("MSG"))
            setTaskExecutor(executor)
        }
    }
}