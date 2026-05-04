package org.example.features.realtime.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SseService {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1 hour timeout
        this.emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.debug("SSE Emitter completed");
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.debug("SSE Emitter timed out");
            emitter.complete();
            this.emitters.remove(emitter);
        });
        emitter.onError((e) -> {
            log.debug("SSE Emitter error", e);
            this.emitters.remove(emitter);
        });

        // Send an initial event to keep connection alive
        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected successfully"));
        } catch (IOException e) {
            emitter.completeWithError(e);
            this.emitters.remove(emitter);
        }

        return emitter;
    }

    public void sendEvent(String eventName, Object data) {
        log.info("Sending SSE Event: {} with data: {}", eventName, data);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                log.debug("Failed to send event to emitter, removing it", e);
                emitter.complete();
                this.emitters.remove(emitter);
            }
        }
    }
}
