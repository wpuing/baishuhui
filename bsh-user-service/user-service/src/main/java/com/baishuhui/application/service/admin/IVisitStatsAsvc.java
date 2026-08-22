package com.baishuhui.application.service.admin;

import com.baishuhui.user.vo.admin.VisitLoginDTO;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 应用服务接口（原 IVisitStatsAsvc）。
 *
 * @author wei yz
 */
public interface IVisitStatsAsvc {
    long recordLogin(String userId, String username, String clientIp);
    long todayCount();
    List<Map<String, Object>> lastDays(int days);
    List<VisitLoginDTO> todayLoginRecords();
}
