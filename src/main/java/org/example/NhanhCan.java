package org.example;
import java.io.*;
import java.util.*;

public class NhanhCan {
    private Map<String, List<Node>> graph = new HashMap<>();
    private List<String> path = new ArrayList<>();
    private int totalCost = 0;

    // Node class để lưu thông tin đỉnh và chi phí đến đỉnh đó
    class Node {
        String vertex;
        int cost;

        Node(String vertex, int cost) {
            this.vertex = vertex;
            this.cost = cost;
        }
    }

    // Đọc dữ liệu từ file văn bản
    public void loadGraph(String inputFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;

        // Đọc các đỉnh và giá trị của chúng
        while (!(line = br.readLine()).isEmpty()) {
            String[] parts = line.split(" ");
            String vertex = parts[0];
            int value = Integer.parseInt(parts[1]);
            graph.put(vertex, new ArrayList<>());
        }

        // Đọc các cạnh với chi phí
        while (!(line = br.readLine()).isEmpty()) {
            String[] parts = line.split(" ");
            String u = parts[0];
            String v = parts[1];
            int cost = Integer.parseInt(parts[2]);
            graph.get(u).add(new Node(v, cost));
            graph.get(v).add(new Node(u, cost)); // Nếu là đồ thị vô hướng
        }

        // Đọc trạng thái đầu và trạng thái kết thúc
        String start = br.readLine().trim();
        String end = br.readLine().trim();
        br.close();

        // Gọi thuật toán Branch and Bound
        branchAndBound(start, end);

        // Ghi kết quả vào file
        saveResult("NhanhCanOutput.txt", start, end);
    }

    // Hàm thực hiện Branch and Bound
    private void branchAndBound(String start, String end) {
        PriorityQueue<Path> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.cost));
        queue.add(new Path(start, 0, new ArrayList<>()));

        Set<String> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            Path currentPath = queue.poll();
            String currentVertex = currentPath.vertex;
            int currentCost = currentPath.cost;

            // Nếu đã đến đỉnh kết thúc
            if (currentVertex.equals(end)) {
                path = currentPath.route;
                totalCost = currentCost;
                return;
            }

            // Đánh dấu đỉnh hiện tại là đã thăm
            visited.add(currentVertex);

            // Thêm các đỉnh kề chưa thăm vào hàng đợi với chi phí tương ứng
            for (Node neighbor : graph.getOrDefault(currentVertex, new ArrayList<>())) {
                if (!visited.contains(neighbor.vertex)) {
                    List<String> newRoute = new ArrayList<>(currentPath.route);
                    newRoute.add(neighbor.vertex); // Chỉ thêm đỉnh chưa thăm vào đường đi
                    queue.add(new Path(neighbor.vertex, currentCost + neighbor.cost, newRoute));
                }
            }
        }
    }

    // Class để lưu đường đi và chi phí đến đỉnh đó
    class Path {
        String vertex;
        int cost;
        List<String> route;

        Path(String vertex, int cost, List<String> route) {
            this.vertex = vertex;
            this.cost = cost;
            this.route = new ArrayList<>(route); // Tạo bản sao của route để tránh trùng lặp
            this.route.add(vertex);
        }
    }

    // Ghi kết quả ra file
    private void saveResult(String outputFile, String start, String end) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        if (!path.isEmpty()) {
            bw.write("Đã tìm thấy đường đi từ " + start + " đến " + end + ":\n");
            for (String node : path) {
                bw.write(node + " ");
            }
            bw.newLine();
            bw.write("Tổng chi phí: " + totalCost + "\n");
        } else {
            bw.write("Không tìm thấy đường đi từ " + start + " đến " + end + ".\n");
        }

        bw.write("Các bước thực hiện thuật toán:\n");
        for (String node : path) {
            bw.write("Thăm đỉnh " + node + "\n");
        }

        bw.close();
    }

    public static void main(String[] args) {
        NhanhCan search = new NhanhCan();
        try {
            search.loadGraph("NhanhCanInput.txt");
        } catch (IOException e) {
            System.out.println("Đã xảy ra lỗi khi đọc hoặc ghi file.");
            e.printStackTrace();
        }
    }
}
