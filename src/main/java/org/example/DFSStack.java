package org.example;

import java.io.*;
import java.util.*;

public class DFSStack {
    // Đồ thị biểu diễn dưới dạng danh sách kề
    private Map<String, List<String>> graph = new HashMap<>();
    private List<String> steps = new ArrayList<>();

    // Phương thức đọc đồ thị từ file
    public void readGraphFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String node = parts[0]; // Phần tử đầu tiên là đỉnh
                List<String> neighbors = new ArrayList<>(List.of(Arrays.copyOfRange(parts, 1, parts.length)));
                graph.put(node, neighbors);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm DFS sử dụng ngăn xếp để tìm đường từ đỉnh bắt đầu đến đỉnh đích
    public List<String> dfs(String startNode, String goalNode) {
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>(); // Lưu lại đỉnh trước đó để truy vết đường đi
        stack.push(startNode);
        visited.add(startNode);

        // Lặp đến khi ngăn xếp trống
        while (!stack.isEmpty()) {
            String currentNode = stack.pop();
            steps.add("Visited node: " + currentNode);

            // In các đỉnh kề của currentNode
            List<String> neighbors = graph.getOrDefault(currentNode, new ArrayList<>());
            steps.add("Neighbors of " + currentNode + ": " + neighbors);

            // Nếu tìm thấy đỉnh đích
            if (currentNode.equals(goalNode)) {
                return constructPath(parentMap, startNode, goalNode);
            }

            // Đẩy các đỉnh kề vào ngăn xếp theo đúng thứ tự từ trái sang phải (ngăn xếp sẽ xử lý từ phải sang trái)
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, currentNode); // Lưu lại cha của đỉnh này để truy vết
                }
            }

            // In trạng thái hiện tại của ngăn xếp (không cần đảo ngược)
            steps.add("Stack: " + stack + "\n"); // In ra ngăn xếp như đang lưu trữ
        }

        return Collections.emptyList(); // Trả về danh sách rỗng nếu không tìm thấy đường
    }

    // Phương thức truy vết lại đường đi từ đỉnh bắt đầu đến đỉnh đích
    private List<String> constructPath(Map<String, String> parentMap, String startNode, String goalNode) {
        List<String> path = new ArrayList<>();
        String current = goalNode;

        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }

        Collections.reverse(path); // Đảo ngược đường đi để có đúng thứ tự từ start -> goal
        return path;
    }

    public List<String> getSteps() {
        return steps;
    }

    public static void main(String[] args) {
        DFSStack dfs = new DFSStack();
        String start = "A";  // Đỉnh bắt đầu
        String goal = "G";   // Đỉnh đích

        // Đọc đồ thị từ tệp input.txt
        dfs.readGraphFromFile("C:\\Users\\buiqu\\Downloads\\input.txt");

        // Thực hiện DFS và in đường đi
        List<String> resultPath = dfs.dfs(start, goal);
        List<String> steps = dfs.getSteps();

        if (!resultPath.isEmpty()) {
            System.out.println("Path from " + start + " to " + goal + ": " + String.join(" -> ", resultPath));
        } else {
            System.out.println("No path found from " + start + " to " + goal);
        }

        // In bảng các bước thực hiện
        System.out.println("\nSteps taken:");
        for (String step : steps) {
            System.out.println(step);
        }
    }
}
