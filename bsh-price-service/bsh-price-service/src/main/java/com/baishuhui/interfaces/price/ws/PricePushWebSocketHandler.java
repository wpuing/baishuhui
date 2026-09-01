package com.baishuhui.interfaces.price.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 行情推送：订阅回执 + 成交价广播。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PricePushWebSocketHandler extends TextWebSocketHandler {

    private static final int MAX_SESSIONS = 500;
    private static final int MAX_PAYLOAD_CHARS = 4096;

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        pruneClosed();
        if (sessions.size() >= MAX_SESSIONS) {
            log.warn("price ws reject: session limit {}", MAX_SESSIONS);
            session.close(CloseStatus.POLICY_VIOLATION.withReason("too many connections"));
            return;
        }
        sessions.add(session);
        log.info("price ws connected sessionId={} total={}", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("price ws closed sessionId={} status={} total={}", session.getId(), status, sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String raw = message.getPayload();
        if (raw != null && raw.length() > MAX_PAYLOAD_CHARS) {
            session.close(CloseStatus.TOO_BIG_TO_PROCESS);
            sessions.remove(session);
            return;
        }
        Map<String, Object> ack = new LinkedHashMap<>(2);
        ack.put("type", "subscribed");
        try {
            // 解析客户端订阅 JSON，原样回显在 ack 中
            JsonNode payload = objectMapper.readTree(raw);
            ack.put("payload", payload);
        } catch (Exception ex) {
            ack.put("payload", raw);
        }
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(ack)));
    }

    /**
     * 向所有在线会话广播 JSON。
     *
     * @param json 行情 JSON
     */
    public void broadcast(String json) {
        if (json == null || json.isBlank()) {
            return;
        }
        TextMessage message = new TextMessage(json);
        // 遍历在线会话广播行情 JSON
        for (WebSocketSession session : sessions) {
            // 清理已关闭会话
            if (session == null || !session.isOpen()) {
                sessions.remove(session);
                continue;
            }
            synchronized (session) {
                try {
                    session.sendMessage(message);
                } catch (IOException ex) {
                    log.warn("price ws send fail sessionId={}", session.getId(), ex);
                    sessions.remove(session);
                    try {
                        session.close(CloseStatus.SERVER_ERROR);
                    } catch (IOException ignored) {
                        // ignore
                    }
                }
            }
        }
    }

    private void pruneClosed() {
        sessions.removeIf(s -> s == null || !s.isOpen());
    }
}
