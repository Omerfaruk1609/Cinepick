package proje.cinepick.config;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@Configuration
public class LocalAiConfig {

    @Value("${app.onnx.model-path:classpath:models/all-MiniLM-L6-v2.onnx}")
    private Resource modelResource;

    @Value("${app.onnx.tokenizer-path:classpath:models/tokenizer.json}")
    private Resource tokenizerResource;

    @Bean
    public OrtEnvironment ortEnvironment() {
        try {
            return OrtEnvironment.getEnvironment();
        } catch (Throwable t) {
            log.warn("ONNX Runtime native environment could not be initialized (libstdc++/glibc issue): {}. Falling back to internal embedding vector engine.", t.getMessage());
            return null;
        }
    }

    @Bean
    public OrtSession ortSession(@Autowired(required = false) OrtEnvironment env) {
        if (env == null) {
            return null;
        }
        if (!modelResource.exists()) {
            log.warn("ONNX model resource not found at {}", modelResource);
            return null;
        }
        try (InputStream is = modelResource.getInputStream()) {
            byte[] modelBytes = is.readAllBytes();
            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            log.info("Successfully loaded ONNX model from {}", modelResource);
            return env.createSession(modelBytes, options);
        } catch (Throwable t) {
            log.warn("Failed to create ONNX Session: {}. Using fallback vector engine.", t.getMessage());
            return null;
        }
    }

    @Bean
    public HuggingFaceTokenizer huggingFaceTokenizer() {
        if (!tokenizerResource.exists()) {
            log.warn("Tokenizer resource not found at {}", tokenizerResource);
            return null;
        }
        try (InputStream is = tokenizerResource.getInputStream()) {
            log.info("Successfully loaded HuggingFace Tokenizer from {}", tokenizerResource);
            return HuggingFaceTokenizer.newInstance(is, Map.of());
        } catch (Throwable t) {
            log.warn("Failed to create HuggingFace Tokenizer: {}. Using fallback.", t.getMessage());
            return null;
        }
    }
}
