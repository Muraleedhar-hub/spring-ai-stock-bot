package com.ai.stock.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatClient chatClient;
    private final WebClient webClient;

    public ChatController(ChatClient.Builder builder, WebClient.Builder webClientBuilder) {
        this.chatClient = builder.build();
        this.webClient = webClientBuilder.build();
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

   @GetMapping("/chatPrompt")
    public Flux<String> fetchAsTextChunks(@RequestParam String message) {
        return webClient.get()
                .uri("http://localhost:8081/chat?query="+message)
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(chunk -> log.debug("Text chunk received: {} chars", chunk.length()))
                .doOnError(e -> log.error("Error fetching text from {}: {}", "Stock Exchange ", e.getMessage()));
    }

}
