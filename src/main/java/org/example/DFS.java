package org.example;

import java.io.*;
import java.util.*;

public class DFS {
    private Map<String, List<String>> graph = new HashMap<>();
    private List<String[]> steps = new ArrayList<>(); // Mỗi bước là một mảng: [Đỉnh phát triển, Đỉnh kề, Stack]
    private List<String> approvedPeak = new ArrayList<>();
    private String startNode;
    private String goalNode;

    public void readGraphFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            List<String> lines = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            int lastIndex = lines.size() - 1;
            while (lastIndex >= 0 && lines.get(lastIndex).trim().isEmpty()) {
                lastIndex--;
            }

            goalNode = lines.get(lastIndex).trim();
            startNode = lines.get(lastIndex - 1).trim();

            for (int i = 0; i < lastIndex - 1; i++) {
                String[] parts = lines.get(i).split(" ");
                String node = parts[0];
                List<String> neighbors = new ArrayList<>(List.of(Arrays.copyOfRange(parts, 1, parts.length)));
                graph.put(node, neighbors);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> dfs() {
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();
        stack.push(startNode);
        visited.add(startNode);
        while (!stack.isEmpty()) {

            String currentNode = stack.pop();
            approvedPeak.add(currentNode);

            // Lấy các đỉnh kề
            List<String> neighbors = graph.getOrDefault(currentNode, new ArrayList<>());

            // Đẩy các đỉnh kề vào ngăn xếp
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, currentNode);
                }
            }

            // Thêm bước hiện tại vào danh sách (sau khi cập nhật stack)
            steps.add(new String[] {
                    currentNode,                // Đỉnh phát triển
                    neighbors.toString(),       // Đỉnh kề
                    approvedPeak.toString(),    // Đỉnh đã duyệt
                    stack.toString()            // Trạng thái stack
            });

            if (currentNode.equals(goalNode)) {
                return constructPath(parentMap, startNode, goalNode);
            }
        }

        return Collections.emptyList();
    }

    private List<String> constructPath(Map<String, String> parentMap, String startNode, String goalNode) {
        List<String> path = new ArrayList<>();
        String current = goalNode;

        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    public void writeResultsToFile(String filename, List<String> resultPath, List<String[]> steps) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write(String.format("%-20s %-30s %-30s %-30s", "Đỉnh phát triển", "Đỉnh kề", "Đỉnh đã duyệt",
                    "Danh sách trong stack"));
            writer.newLine();
            writer.write("=".repeat(105));
            writer.newLine();

            for (String[] step : steps) {
                writer.write(String.format("%-20s %-30s %-30s %-30s", step[0], step[1], step[2], step[3]));
                writer.newLine();
            }
            if (!resultPath.isEmpty()) {
                writer.write("Đường đi: " + String.join(" -> ", resultPath));
                writer.newLine();
                writer.newLine();
            } else {
                writer.write("No path found from start to goal");
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DFS dfs = new DFS();

        dfs.readGraphFromFile("file//input//DFSInput.txt");
        List<String> resultPath = dfs.dfs();

        if (!resultPath.isEmpty()) {
            System.out.println("Path from " + dfs.startNode + " to " + dfs.goalNode + ": " + String.join(" -> ", resultPath));
        } else {
            System.out.println("No path found from " + dfs.startNode + " to " + dfs.goalNode);
        }

        System.out.println("\nSteps taken:");
        System.out.printf("%-20s %-30s %-30s\n", "Đỉnh phát triển", "Đỉnh kề", "Danh sách trong stack");
        System.out.println("=".repeat(80));
        for (String[] step : dfs.steps) {
            System.out.printf("%-20s %-30s %-30s\n", step[0], step[1], step[2]);
        }

        dfs.writeResultsToFile("file//output//DFSOutput.txt", resultPath, dfs.steps);
    }
}
