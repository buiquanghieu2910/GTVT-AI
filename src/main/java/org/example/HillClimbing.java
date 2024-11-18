package org.example;

import java.io.*;
import java.util.*;

public class HillClimbing {
    private Map<String, List<String>> graph = new HashMap<>();
    private Map<String, Integer> heuristicValues = new HashMap<>();
    private List<String[]> steps = new ArrayList<>();
    private List<String> path = new ArrayList<>();
    private boolean found = false;

    // Hàm đọc đồ thị từ file đầu vào
    public void loadGraph(String inputFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int vertices = Integer.parseInt(st.nextToken());  // Đọc số lượng đỉnh
        int edges = Integer.parseInt(st.nextToken());    // Đọc số lượng cạnh

        // Đọc các đỉnh và giá trị heuristic
        for (int i = 0; i < vertices; i++) {
            st = new StringTokenizer(br.readLine());
            String vertex = st.nextToken();
            int value = Integer.parseInt(st.nextToken());
            heuristicValues.put(vertex, value);
            graph.put(vertex, new ArrayList<>());
        }

        // Đọc các cạnh và xây dựng đồ thị từ định dạng đầu vào
        for (int i = 0; i < edges; i++) {
            st = new StringTokenizer(br.readLine());
            String vertex = st.nextToken();
            String neighbor = st.nextToken();

            // Đảm bảo đồ thị luôn chứa danh sách rỗng ban đầu cho các đỉnh
            graph.putIfAbsent(vertex, new ArrayList<>());

            // Thêm neighbor vào danh sách kề của vertex
            if (!graph.get(vertex).contains(neighbor)) {
                graph.get(vertex).add(neighbor);
            }
        }

        // Sắp xếp danh sách kề để đảm bảo thứ tự
        for (String vertex : graph.keySet()) {
            List<String> neighbors = graph.get(vertex);
            neighbors.sort(Comparator.naturalOrder());
            graph.put(vertex, neighbors);
        }

        // Kiểm tra và in danh sách kề để xác minh
        System.out.println("Danh sách kề sau khi xây dựng đồ thị:");
        for (String key : graph.keySet()) {
            System.out.println(key + ": " + graph.get(key));
        }

        // Đọc trạng thái bắt đầu và kết thúc
        String start = br.readLine().trim();
        String goal = br.readLine().trim();
        br.close();

        // Chạy thuật toán Hill Climbing
        hillClimb(start, goal);

        // Ghi kết quả ra file
        saveResult("file//output//HillClimbingOutput.txt", start, goal);
    }

    // Thuật toán Hill Climbing
    private void hillClimb(String start, String goal) {
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(heuristicValues::get));
        Set<String> visited = new HashSet<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            visited.add(current);
            path.add(current);

            // Nếu đã đến đích, kết thúc tìm kiếm
            if (current.equals(goal)) {
                found = true;
                steps.add(new String[] { current + "(" + heuristicValues.get(current) + ")", "DUNG", "[]" });
                return;
            }

            List<String> neighbors = graph.getOrDefault(current, new ArrayList<>());
            List<String> validNeighbors = new ArrayList<>();
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    validNeighbors.add(neighbor);
                }
            }

            // Thêm vào hàng đợi các đỉnh kề hợp lệ
            for (String neighbor : validNeighbors) {
                queue.add(neighbor);
            }

            List<String> neighborValues = new ArrayList<>();
            for (String neighbor : validNeighbors) {
                neighborValues.add(neighbor + "(" + heuristicValues.get(neighbor) + ")");
            }

            List<String> queueValues = new ArrayList<>(queue);
            queueValues.sort(Comparator.comparingInt(heuristicValues::get));

            List<String> queueFormatted = new ArrayList<>();
            for (String item : queueValues) {
                queueFormatted.add(item + "(" + heuristicValues.get(item) + ")");
            }

            steps.add(new String[] {
                    current + "(" + heuristicValues.get(current) + ")",
                    neighborValues.toString(),
                    queueFormatted.toString()
            });
        }
    }

    // Lưu kết quả ra file
    private void saveResult(String outputFile, String start, String goal) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        bw.write(String.format("%-25s %-40s %-40s", "Đỉnh phát triển", "Danh sách kề", "Danh sách L"));
        bw.newLine();
        bw.write("=".repeat(105));
        bw.newLine();

        for (String[] step : steps) {
            bw.write(String.format("%-25s %-40s %-40s", step[0], step[1], step[2]));
            bw.newLine();
        }

        bw.newLine();
        if (found) {
            bw.write("Đã tìm thấy đường đi từ " + start + " đến " + goal + ":\n");
            for (String node : path) {
                bw.write(node + "(" + heuristicValues.get(node) + ")  ");
            }
            bw.newLine();
        } else {
            bw.write("Không tìm thấy đường đi từ " + start + " đến " + goal + ".\n");
            bw.newLine();
        }
        bw.close();
    }

    // Hàm main để chạy chương trình
    public static void main(String[] args) {
        HillClimbing hillClimbingSearch = new HillClimbing();
        try {
            hillClimbingSearch.loadGraph("file//input//HillClimbingInput.txt");
        } catch (IOException e) {
            System.out.println("Đã xảy ra lỗi khi đọc hoặc ghi file.");
            e.printStackTrace();
        }
    }
}