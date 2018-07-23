package io.polarian.subscriber

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.listener.ChannelTopic
import java.util.concurrent.Executor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class RedisConfig {
    @Bean
    @ConfigurationProperties(prefix = "my.pool")
    fun taskExecutor():Executor {
        return ThreadPoolTaskExecutor()
    }

    @Bean
    fun redisContainer(redisConnectionFactory: RedisConnectionFactory, executor : Executor): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        container.addMessageListener(MessageListenerAdapter(RedisMessageSubscriber()), ChannelTopic("MSG"))
        container.setTaskExecutor(executor)

        return container
    }
}