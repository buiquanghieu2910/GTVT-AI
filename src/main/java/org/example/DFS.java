package org.example;

import java.io.*;
import java.util.*;

public class DFS {
    // Đồ thị biểu diễn dưới dạng danh sách kề
    private Map<String, List<String>> graph = new HashMap<>();
    private List<String> steps = new ArrayList<>();
    private String startNode;
    private String goalNode;

    // Phương thức đọc đồ thị từ file
    public void readGraphFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            List<String> lines = new ArrayList<>();

            // Đọc tất cả các dòng từ file
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            // Bỏ qua dòng trống cuối trước khi đọc đỉnh bắt đầu và kết thúc
            int lastIndex = lines.size() - 1;
            while (lastIndex >= 0 && lines.get(lastIndex).trim().isEmpty()) {
                lastIndex--;
            }

            // Đặt đỉnh bắt đầu và đỉnh kết thúc từ các dòng cuối cùng (bỏ qua dòng trống)
            goalNode = lines.get(lastIndex).trim();         // Dòng cuối có đỉnh kết thúc
            startNode = lines.get(lastIndex - 1).trim();    // Dòng trước đó có đỉnh bắt đầu

            // Đọc các đỉnh và cạnh trong các dòng trước đó
            for (int i = 0; i < lastIndex - 1; i++) {
                String[] parts = lines.get(i).split(" ");
                String node = parts[0]; // Phần tử đầu tiên là đỉnh
                List<String> neighbors = new ArrayList<>(List.of(Arrays.copyOfRange(parts, 1, parts.length)));
                graph.put(node, neighbors);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm DFS sử dụng ngăn xếp để tìm đường từ đỉnh bắt đầu đến đỉnh đích
    public List<String> dfs() {
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

    // Phương thức ghi kết quả vào file
    private void writeResultsToFile(String filename, List<String> resultPath, List<String> steps) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            if (!resultPath.isEmpty()) {
                writer.write("Path from start to goal: " + String.join(" -> ", resultPath));
                writer.newLine();
            } else {
                writer.write("No path found from start to goal");
                writer.newLine();
            }

            writer.newLine();
            writer.write("Steps taken:");
            writer.newLine();
            for (String step : steps) {
                writer.write(step);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DFS dfs = new DFS();

        // Đọc đồ thị từ tệp DFSInput.txt
        dfs.readGraphFromFile("file//input//DFSInput.txt");

        // Thực hiện DFS và in đường đi
        List<String> resultPath = dfs.dfs();
        List<String> steps = dfs.getSteps();

        if (!resultPath.isEmpty()) {
            System.out.println("Path from " + dfs.startNode + " to " + dfs.goalNode + ": " + String.join(" -> ", resultPath));
        } else {
            System.out.println("No path found from " + dfs.startNode + " to " + dfs.goalNode);
        }

        // In bảng các bước thực hiện
        System.out.println("\nSteps taken:");
        for (String step : steps) {
            System.out.println(step);
        }

        // Ghi kết quả vào tệp output.txt
        dfs.writeResultsToFile("file//output//DFSOutput.txt", resultPath, steps);
    }
}