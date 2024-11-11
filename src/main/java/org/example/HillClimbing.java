package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;

public class HillClimbing {
    private Map<String, List<String>> graph = new HashMap<>();
    private Map<String, Integer> values = new HashMap<>();
    private List<String> path = new ArrayList<>();
    private boolean found = false;

    // Đọc dữ liệu từ file văn bản
    public void loadGraph(String inputFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int vertices = Integer.parseInt(st.nextToken());
        int edges = Integer.parseInt(st.nextToken());

        // Đọc các đỉnh với giá trị gắn liền
        for (int i = 0; i < vertices; i++) {
            st = new StringTokenizer(br.readLine());
            String vertex = String.valueOf(st.nextToken().charAt(0));
            int value = Integer.parseInt(st.nextToken());
            values.put(vertex, value);
            graph.put(vertex, new ArrayList<>());
        }

        // Đọc các cạnh
        for (int i = 0; i < edges; i++) {
            st = new StringTokenizer(br.readLine());
            String u = String.valueOf(st.nextToken().charAt(0));
            String v = String.valueOf(st.nextToken().charAt(0));
            graph.get(u).add(v);
            graph.get(v).add(u); // Nếu là đồ thị vô hướng
        }

        // Đọc trạng thái đầu và trạng thái kết thúc
        String start = String.valueOf(br.readLine().trim().charAt(0));
        String end = String.valueOf(br.readLine().trim().charAt(0));
        br.close();

        // Gọi thuật toán Hill Climbing
        hillClimb(start, end);

        // Ghi kết quả vào file
        saveResult("HillClimbingOutput.txt", start, end);
    }

    // Hàm thực hiện Hill Climbing với hàng đợi ưu tiên
    private void hillClimb(String start, String end) {
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(values::get));
        Set<String> visited = new HashSet<>();
        queue.add(start);
        // Đánh dấu diểm vừa duyệt lần trước để kiểm tra điểm tiếp theo có phải xuất phát từ đây không???
        String lastPoint = "";
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (graph.get(lastPoint) != null && lastPoint.length() > 0 && !graph.get(lastPoint).contains(current) && path.size() > 0) {
                var index = path.size() - 1;
                path.remove(index);
            }
            path.add(current);
//      System.out.println("Current: ===== " + current);
            visited.add(current);

            // Kiểm tra nếu đạt đến đỉnh kết thúc
            if (current.equals(end)) {
                found = true;
                return;
            }
            lastPoint = current;

            // Thêm các đỉnh kề vào hàng đợi ưu tiên
            for (String neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
    }

    // Ghi kết quả ra file
    private void saveResult(String outputFile, String start, String end) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        if (found) {
            bw.write("Đã tìm thấy đường đi từ " + start + " đến " + end + ":\n");
            for (String node : path) {
                bw.write(node + "-" + values.get(node) + " ");
            }
            bw.newLine();
        } else {
            bw.write("Không tìm thấy đường đi từ " + start + " đến " + end + ".\n");
        }

        bw.write("Các bước thực hiện thuật toán:\n");
        for (String node : path) {
            bw.write("Thăm đỉnh " + node + " (giá trị: " + values.get(node) + ")\n");
        }

        bw.close();
    }

    public static void main(String[] args) {
        HillClimbing hillClimbSearch = new HillClimbing();
        try {
            hillClimbSearch.loadGraph("HillClimbingInput.txt");
        } catch (IOException e) {
            System.out.println("Đã xảy ra lỗi khi đọc hoặc ghi file.");
            e.printStackTrace();
        }
    }

}
