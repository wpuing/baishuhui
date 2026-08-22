package com.baishuhui.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 图形验证码：生成、校验与一次性消费；优先 Redis，不可用时回退本地缓存。
 *
 * @author wei yz
 */
@Service
public class CaptchaService {

    private static final String PREFIX = "bsh:captcha:";
    private static final String CHARS = "ACDEFGHJKLMNPQRTUVWX3467";
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final int LOCAL_MAX = 2000;

    /** 5x7 点阵，覆盖验证码字符集，避免依赖系统字体（无头 Linux 常见缺字体）。 */
    private static final Map<Character, int[]> GLYPHS = buildGlyphs();

    private final StringRedisTemplate redisTemplate;
    private final ConcurrentHashMap<String, LocalEntry> localStore = new ConcurrentHashMap<>();

    public CaptchaService(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 验证码载荷：前端凭 key 提交，imageBase64 为 data URL。
     */
    public record CaptchaPayload(String captchaKey, String imageBase64) {}

    private record LocalEntry(String code, long expireAtMs) {
        boolean expired(long now) {
            return now > expireAtMs;
        }
    }

    /**
     * 生成验证码并返回 key 与 PNG data URL。
     */
    public CaptchaPayload create() {
        String code = randomCode(4);
        String key = UUID.randomUUID().toString().replace("-", "");
        store(key, code);
        String image = "data:image/png;base64," + Base64.getEncoder().encodeToString(render(code));
        return new CaptchaPayload(key, image);
    }

    /**
     * 校验并消费验证码（成功或失败均删除，防重放）。
     */
    public boolean verifyAndConsume(String key, String input) {
        // 空入参直接失败，避免无意义 Redis/本地查找
        if (key == null || key.isBlank() || input == null || input.isBlank()) {
            return false;
        }
        String expect = take(key);
        return expect != null && expect.equalsIgnoreCase(input.trim());
    }

    private void store(String key, String code) {
        // Redis 可用时写远程；异常或未装配时回退本地，保证登录链路不因缓存挂掉
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(PREFIX + key, code, TTL);
                return;
            } catch (Exception ignored) {
                // fallback local
            }
        }
        storeLocal(key, code);
    }

    private void storeLocal(String key, String code) {
        long now = System.currentTimeMillis();
        evictExpiredLocal(now);
        if (localStore.size() >= LOCAL_MAX) {
            // 容量满时剔除约 10%，防止刷验证码撑爆堆
            int remove = Math.max(LOCAL_MAX / 10, 1);
            Iterator<String> it = localStore.keySet().iterator();
            while (it.hasNext() && remove > 0) {
                it.next();
                it.remove();
                remove--;
            }
        }
        localStore.put(key, new LocalEntry(code, now + TTL.toMillis()));
    }

    private void evictExpiredLocal(long now) {
        localStore.entrySet().removeIf(e -> e.getValue().expired(now));
    }

    private String take(String key) {
        // 优先从 Redis 取并删除，保证多实例下一次性消费；失败则读本地
        if (redisTemplate != null) {
            try {
                String redisKey = PREFIX + key;
                String val = redisTemplate.opsForValue().get(redisKey);
                if (val != null) {
                    redisTemplate.delete(redisKey);
                    return val;
                }
            } catch (Exception ignored) {
                // fallback
            }
        }
        LocalEntry entry = localStore.remove(key);
        if (entry == null || entry.expired(System.currentTimeMillis())) {
            return null;
        }
        return entry.code();
    }

    private static String randomCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(r.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private static byte[] render(String code) {
        int w = 140;
        int h = 44;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(new Color(18, 48, 36));
            g.fillRect(0, 0, w, h);
            ThreadLocalRandom r = ThreadLocalRandom.current();
            // 少量浅色干扰线，避免挡住点阵字
            for (int i = 0; i < 3; i++) {
                g.setColor(new Color(r.nextInt(40, 90), r.nextInt(90, 140), r.nextInt(50, 100)));
                g.drawLine(r.nextInt(w), r.nextInt(h), r.nextInt(w), r.nextInt(h));
            }
            g.setColor(new Color(220, 245, 170));
            int x = 12;
            // 放大点阵，提升可辨识度
            for (int i = 0; i < code.length(); i++) {
                drawGlyph(g, code.charAt(i), x, 8, 3);
                x += 30;
            }
        } finally {
            g.dispose();
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private static void drawGlyph(Graphics2D g, char ch, int ox, int oy, int scale) {
        int[] rows = GLYPHS.get(Character.toUpperCase(ch));
        if (rows == null) {
            return;
        }
        // 按位展开 5x7 点阵到像素块
        for (int y = 0; y < rows.length; y++) {
            int row = rows[y];
            for (int x = 0; x < 5; x++) {
                if (((row >> (4 - x)) & 1) == 1) {
                    g.fillRect(ox + x * scale, oy + y * scale, scale, scale);
                }
            }
        }
    }

    private static Map<Character, int[]> buildGlyphs() {
        Map<Character, int[]> m = new ConcurrentHashMap<>();
        m.put('2', new int[]{0b01110, 0b10001, 0b00001, 0b00010, 0b00100, 0b01000, 0b11111});
        m.put('3', new int[]{0b01110, 0b10001, 0b00001, 0b00110, 0b00001, 0b10001, 0b01110});
        m.put('4', new int[]{0b00010, 0b00110, 0b01010, 0b10010, 0b11111, 0b00010, 0b00010});
        m.put('5', new int[]{0b11111, 0b10000, 0b11110, 0b00001, 0b00001, 0b10001, 0b01110});
        m.put('6', new int[]{0b00110, 0b01000, 0b10000, 0b11110, 0b10001, 0b10001, 0b01110});
        m.put('7', new int[]{0b11111, 0b00001, 0b00010, 0b00100, 0b01000, 0b01000, 0b01000});
        m.put('8', new int[]{0b01110, 0b10001, 0b10001, 0b01110, 0b10001, 0b10001, 0b01110});
        m.put('9', new int[]{0b01110, 0b10001, 0b10001, 0b01111, 0b00001, 0b00010, 0b01100});
        m.put('A', new int[]{0b01110, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001});
        m.put('B', new int[]{0b11110, 0b10001, 0b10001, 0b11110, 0b10001, 0b10001, 0b11110});
        m.put('C', new int[]{0b01110, 0b10001, 0b10000, 0b10000, 0b10000, 0b10001, 0b01110});
        m.put('D', new int[]{0b11110, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b11110});
        m.put('E', new int[]{0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b11111});
        m.put('F', new int[]{0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b10000});
        m.put('G', new int[]{0b01110, 0b10001, 0b10000, 0b10111, 0b10001, 0b10001, 0b01110});
        m.put('H', new int[]{0b10001, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001});
        m.put('J', new int[]{0b00111, 0b00010, 0b00010, 0b00010, 0b00010, 0b10010, 0b01100});
        m.put('K', new int[]{0b10001, 0b10010, 0b10100, 0b11000, 0b10100, 0b10010, 0b10001});
        m.put('L', new int[]{0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b11111});
        m.put('M', new int[]{0b10001, 0b11011, 0b10101, 0b10001, 0b10001, 0b10001, 0b10001});
        m.put('N', new int[]{0b10001, 0b11001, 0b10101, 0b10011, 0b10001, 0b10001, 0b10001});
        m.put('P', new int[]{0b11110, 0b10001, 0b10001, 0b11110, 0b10000, 0b10000, 0b10000});
        m.put('Q', new int[]{0b01110, 0b10001, 0b10001, 0b10001, 0b10101, 0b10010, 0b01101});
        m.put('R', new int[]{0b11110, 0b10001, 0b10001, 0b11110, 0b10100, 0b10010, 0b10001});
        m.put('S', new int[]{0b01111, 0b10000, 0b10000, 0b01110, 0b00001, 0b00001, 0b11110});
        m.put('T', new int[]{0b11111, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100});
        m.put('U', new int[]{0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01110});
        m.put('V', new int[]{0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01010, 0b00100});
        m.put('W', new int[]{0b10001, 0b10001, 0b10001, 0b10001, 0b10101, 0b11011, 0b10001});
        m.put('X', new int[]{0b10001, 0b10001, 0b01010, 0b00100, 0b01010, 0b10001, 0b10001});
        m.put('Y', new int[]{0b10001, 0b10001, 0b01010, 0b00100, 0b00100, 0b00100, 0b00100});
        m.put('Z', new int[]{0b11111, 0b00001, 0b00010, 0b00100, 0b01000, 0b10000, 0b11111});
        return m;
    }
}
