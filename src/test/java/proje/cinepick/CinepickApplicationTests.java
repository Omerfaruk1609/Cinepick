package proje.cinepick;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.ai.openai.api-key=dummy-test-key-123456789",
    "spring.data.redis.repositories.enabled=false",
    "spring.cache.type=none"
})
class CinepickApplicationTests {

    @MockBean(name = "openAiChatModel")
    private ChatModel openAiChatModel;

    @MockBean(name = "ollamaChatModel")
    private ChatModel ollamaChatModel;

    @MockBean(name = "openAiEmbeddingModel")
    private EmbeddingModel openAiEmbeddingModel;

    @MockBean(name = "ollamaEmbeddingModel")
    private EmbeddingModel ollamaEmbeddingModel;

    @MockBean
    private org.springframework.ai.image.ImageModel openAiImageModel;

    @MockBean
    private org.springframework.ai.chat.client.ChatClient.Builder chatClientBuilder;

    @MockBean
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private org.springframework.data.redis.connection.ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @MockBean
    private proje.cinepick.bootstrap.DatabaseMovieCatalogSeeder databaseMovieCatalogSeeder;

    @MockBean
    private proje.cinepick.job.MovieEmbeddingIngestionJob movieEmbeddingIngestionJob;

    @Test
    void contextLoads() {
    }

}
