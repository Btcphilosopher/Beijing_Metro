package com.beijingmetro.backend.service;

import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class RoutingService {

    public static class StationNode {
        public String id;
        public String nameZh;
        public String nameEn;
        public List<String> lines;

        public StationNode(String id, String nameZh, String nameEn, List<String> lines) {
            this.id = id;
            this.nameZh = nameZh;
            this.nameEn = nameEn;
            this.lines = lines;
        }
    }

    public static class RoutePlan {
        public List<String> path;
        public int totalTimeMinutes;
        public double priceRmb;
        public int transferCount;
        public String description;

        public RoutePlan(List<String> path, int totalTimeMinutes, double priceRmb, int transferCount, String description) {
            this.path = path;
            this.totalTimeMinutes = totalTimeMinutes;
            this.priceRmb = priceRmb;
            this.transferCount = transferCount;
            this.description = description;
        }
    }

    // Adjacency graph representing connected subway nodes
    private final Map<String, StationNode> stationRegistry = new HashMap<>();
    private final Map<String, Set<String>> adjacencyList = new HashMap<>();

    public RoutingService() {
        // Seed some sample lines & stations matching Android Client
        registerStation(new StationNode("pingguoyuan", "苹果园", "Pingguoyuan", Arrays.asList("Line1")));
        registerStation(new StationNode("fuxingmen", "复兴门", "Fuxingmen", Arrays.asList("Line1", "Line2")));
        registerStation(new StationNode("xidan", "西单", "Xidan", Arrays.asList("Line1", "Line4")));
        registerStation(new StationNode("tiananmen_east", "天安门东", "Tian'anmen East", Arrays.asList("Line1")));
        registerStation(new StationNode("guomao", "国贸", "Guomao", Arrays.asList("Line1", "Line10")));
        registerStation(new StationNode("xizhimen", "西直门", "Xizhimen", Arrays.asList("Line2", "Line4")));
        registerStation(new StationNode("yonghegong", "雍和宫", "Yonghegong", Arrays.asList("Line2", "Line5")));
        registerStation(new StationNode("dongzhimen", "东直门", "Dongzhimen", Arrays.asList("Line2", "AirportExpress")));
        registerStation(new StationNode("beijing_south", "北京南站", "Beijing South", Arrays.asList("Line4")));

        // Map line connectors
        connect("pingguoyuan", "fuxingmen");
        connect("fuxingmen", "xidan");
        connect("xidan", "tiananmen_east");
        connect("tiananmen_east", "guomao");
        connect("xizhimen", "fuxingmen");
        connect("fuxingmen", "beijing_south");
        connect("yonghegong", "dongzhimen");
        connect("dongzhimen", "guomao");
    }

    private void registerStation(StationNode node) {
        stationRegistry.put(node.id, node);
        adjacencyList.putIfAbsent(node.id, new HashSet<>());
    }

    private void connect(String s1, String s2) {
        if (adjacencyList.containsKey(s1) && adjacencyList.containsKey(s2)) {
            adjacencyList.get(s1).add(s2);
            adjacencyList.get(s2).add(s1);
        }
    }

    /**
     * Finds the shortest subway path using Breadth-First Search (BFS).
     * Calculates ticket pricing and transfer rates dynamically.
     */
    public RoutePlan calculateOptimalPath(String startId, String endId) {
        if (!stationRegistry.containsKey(startId) || !stationRegistry.containsKey(endId)) {
            return null;
        }

        if (startId.equals(endId)) {
            return new RoutePlan(Collections.singletonList(startId), 0, 0.0, 0, "出发地与目的地相同");
        }

        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(Collections.singletonList(startId));
        visited.add(startId);

        List<String> shortestPath = null;

        while (!queue.isEmpty()) {
            List<String> currentPath = queue.poll();
            String lastNode = currentPath.get(currentPath.size() - 1);

            if (lastNode.equals(endId)) {
                shortestPath = currentPath;
                break;
            }

            for (String neighbor : adjacencyList.getOrDefault(lastNode, Collections.emptySet())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    List<String> nextPath = new ArrayList<>(currentPath);
                    nextPath.add(neighbor);
                    queue.add(nextPath);
                }
            }
        }

        if (shortestPath == null) {
            return null;
        }

        int stationsPassed = shortestPath.size() - 1;
        int estimatedTimeMinutes = stationsPassed * 3; // roughly 3 mins per station
        
        // standard mileage fare computation
        double price = 3.0;
        if (stationsPassed > 4 && stationsPassed <= 10) price = 4.0;
        else if (stationsPassed > 10) price = 5.0;

        // Count transfers by checking lines intersection along the path
        int transfers = 0;
        String activeLine = getCommonLine(stationRegistry.get(shortestPath.get(0)), stationRegistry.get(shortestPath.get(1)));
        
        for (int i = 1; i < shortestPath.size() - 1; i++) {
            StationNode s1 = stationRegistry.get(shortestPath.get(i));
            StationNode s2 = stationRegistry.get(shortestPath.get(i + 1));
            String nextLine = getCommonLine(s1, s2);
            if (nextLine != null && !nextLine.equals(activeLine)) {
                transfers++;
                activeLine = nextLine;
                estimatedTimeMinutes += 4; // add 4 mins transfer walking time
            }
        }

        String description = String.format("乘车途径 %d 站，预计耗时 %d 分钟，票价 %.1f 元，换乘 %d 次。", 
            stationsPassed, estimatedTimeMinutes, price, transfers);

        return new RoutePlan(shortestPath, estimatedTimeMinutes, price, transfers, description);
    }

    private String getCommonLine(StationNode n1, StationNode n2) {
        for (String line : n1.lines) {
            if (n2.lines.contains(line)) {
                return line;
            }
        }
        return null;
    }
}
