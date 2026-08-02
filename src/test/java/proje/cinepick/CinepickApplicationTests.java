package proje.cinepick;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@SpringBootTest
class CinepickApplicationTests {

    @MockBean
    private ChatModel chatModel;

    @MockBean
    private EmbeddingModel embeddingModel;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @Test
    void contextLoads() {
    }

}
