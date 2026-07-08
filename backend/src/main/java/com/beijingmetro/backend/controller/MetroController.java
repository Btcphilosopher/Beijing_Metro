package com.beijingmetro.backend.controller;

import com.beijingmetro.backend.service.RoutingService;
import com.beijingmetro.backend.service.RoutingService.RoutePlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class MetroController {

    @Autowired
    private RoutingService routingService;

    // Simulate database list
    private final List<Map<String, Object>> systemAnnouncements = new ArrayList<>();

    public MetroController() {
        Map<String, Object> advisory1 = new HashMap<>();
        advisory1.put("id", 1);
        advisory1.put("contentZh", "北京地铁全线运营正常。今日大兴机场线、首都机场线运行平稳，发车间隔正常。");
        advisory1.put("contentEn", "All Beijing Metro lines are operating normally today. Capital Airport Express and Daxing Airport Express are running smoothly.");
        advisory1.put("time", "2026-07-08 06:00");
        advisory1.put("isUrgent", false);
        systemAnnouncements.add(advisory1);
    }

    /**
     * GET /api/route/plan
     * Returns the optimal route plan between start station and end station
     */
    @GetMapping("/route/plan")
    public ResponseEntity<Map<String, Object>> planRoute(
            @RequestParam String startStationId,
            @RequestParam String endStationId) {
        
        RoutePlan plan = routingService.calculateOptimalPath(startStationId, endStationId);
        
        Map<String, Object> response = new HashMap<>();
        if (plan == null) {
            response.put("success", false);
            response.put("message", "Could not calculate a route between the given stations.");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", true);
        response.put("data", plan);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/announcements
     * Fetch live travel alerts & announcements
     */
    @GetMapping("/announcements")
    public ResponseEntity<List<Map<String, Object>>> getAnnouncements() {
        return ResponseEntity.ok(systemAnnouncements);
    }

    /**
     * POST /api/admin/announcements
     * Operator post endpoint to broadcast a scheduling advisory
     */
    @PostMapping("/admin/announcements")
    public ResponseEntity<Map<String, Object>> publishAnnouncement(@RequestBody Map<String, Object> payload) {
        String zh = (String) payload.get("contentZh");
        String en = (String) payload.get("contentEn");
        Boolean urgent = (Boolean) payload.get("isUrgent");

        Map<String, Object> newAd = new HashMap<>();
        newAd.put("id", systemAnnouncements.size() + 1);
        newAd.put("contentZh", zh);
        newAd.put("contentEn", en);
        newAd.put("time", "2026-07-08 10:15");
        newAd.put("isUrgent", urgent != null && urgent);

        systemAnnouncements.add(0, newAd); // Prepend to show most recent first

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Operational bulletin published successfully!");
        response.put("announcement", newAd);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/tickets/scan
     * Mimics dynamic RFID QR ride code scanner validation at turnstile gates
     */
    @PostMapping("/tickets/scan")
    public ResponseEntity<Map<String, Object>> scanTicket(@RequestBody Map<String, Object> scanData) {
        String qrCode = (String) scanData.get("qrCode");
        Double fare = (Double) scanData.get("fare");

        Map<String, Object> response = new HashMap<>();
        if (qrCode == null || !qrCode.startsWith("BJMETRO-RIDE-")) {
            response.put("authorized", false);
            response.put("gateState", "CLOSED");
            response.put("reason", "INVALID_QR_SIGNATURE");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("authorized", true);
        response.put("gateState", "OPEN");
        response.put("deductedFare", fare != null ? fare : 4.0);
        response.put("transactionTimestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
