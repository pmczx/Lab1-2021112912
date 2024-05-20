package com.atguigu.com;

import java.io.*;
import java.util.*;

public class App
{
    public static void processFile(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\n", " ").replace("\r", " "); // 替换换行符和回车符
                line = line.replaceAll("\\p{Punct}", " "); // 替换标点符号
                line = line.replaceAll("[^a-zA-Z ]", ""); // 移除非字母的字符
                line = line.replaceAll("\\s+", " "); //移除多余空格
                sb.append(line).append(" ");
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            System.out.println(sb.toString());
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Map<String, Integer>> createGraphAndReturn(String filePath) throws IOException {
        // 读取文本数据
        @SuppressWarnings("resource")
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        Map<String, Map<String, Integer>> graph = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            // 将文本数据分割成单词
            String[] words = line.toLowerCase().split("\\s+");

            // 遍历所有的单词
            for (int i = 0; i < words.length - 1; i++) {
                // 如果图中不存在这条边，则添加一条新的边
                if (!graph.containsKey(words[i])) {
                    graph.put(words[i], new HashMap<String,Integer>());
                }
                // 如果图中已经存在这条边，则增加这条边的权重
                if (!graph.get(words[i]).containsKey(words[i + 1])) {
                    graph.get(words[i]).put(words[i + 1], 1);
                } else {
                    graph.get(words[i]).put(words[i + 1], graph.get(words[i]).get(words[i + 1]) + 1);
                }
            }
        }
        return graph;
    }

    public static void WriteDOT(Map<String, Map<String, Integer>> graph)throws IOException{
        // 将图写入到output.dot文件中
        BufferedWriter writer = new BufferedWriter(new FileWriter("output.dot"));
        writer.write("digraph shapes {\n");
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
                writer.write(entry.getKey() + " -> " + subEntry.getKey() + " [label=\"" + subEntry.getValue() + "\"];\n");
            }
        }
        writer.write("}");
        writer.close();
    }

    public static void convertDotToImage(String dotFilePath, String imageFilePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", dotFilePath, "-o", imageFilePath);
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("Dot file converted to PNG image successfully!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List<String> findBridgeWords(String word1, String word2, Map<String, Map<String, Integer>> graph) {
        List<String> bridgeWords = new ArrayList<>();
        if(graph.containsKey(word1) && graph.containsKey(word2)){
            for (String word3 : graph.keySet()) {
                if (graph.get(word1).containsKey(word3) && graph.get(word3).containsKey(word2)) {
                    bridgeWords.add(word3);
                }
            }
        }
        return bridgeWords;
    }

    public static String processNewText(String newText, Map<String, Map<String, Integer>> graph) {
        String[] words = newText.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            List<String> bridgeWords = findBridgeWords(words[i], words[i + 1], graph);
            if (!bridgeWords.isEmpty()) {
                String bridgeWord = bridgeWords.get(new Random().nextInt(bridgeWords.size()));
                sb.append(words[i]).append(" ").append(bridgeWord).append(" ");
            } else {
                sb.append(words[i]).append(" ");
            }
        }
        sb.append(words[words.length - 1]);
        return sb.toString();
    }

    public static List<List<String>> findAllShortestPaths(String word1, String word2, Map<String, Map<String, Integer>> graph) {
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(Arrays.asList(word1));

        List<List<String>> shortestPaths = new ArrayList<>();
        int shortestLength = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastWord = path.get(path.size() - 1);

            if (lastWord.equals(word2)) {
                if (path.size() - 1 < shortestLength) {
                    shortestLength = path.size() - 1;
                    shortestPaths.clear();
                    shortestPaths.add(new ArrayList<>(path));
                } else if (path.size() - 1 == shortestLength) {
                    shortestPaths.add(new ArrayList<>(path));
                }
            }

            if (!visited.contains(lastWord)) {
                visited.add(lastWord);
                Map<String, Integer> edges = graph.get(lastWord);
                if (edges != null) {
                    for (String edge : edges.keySet()) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(edge);
                        queue.add(newPath);
                    }
                }
            }
        }

        return shortestPaths; // 如果没有找到路径，返回空列表
    }

    public static List<String> OneNodefindShortestPath(Map<String, Map<String, Integer>> graph, String startWord) {
        Queue<String> queue = new LinkedList<>();
        Map<String, String> previousWords = new HashMap<>();
        Set<String> visitedWords = new HashSet<>();

        queue.add(startWord);
        visitedWords.add(startWord);

        while (!queue.isEmpty()) {
            String currentWord = queue.poll();
            if (graph.containsKey(currentWord)) {
                for (String neighbor : graph.get(currentWord).keySet()) {
                    if (!visitedWords.contains(neighbor)) {
                        queue.add(neighbor);
                        visitedWords.add(neighbor);
                        previousWords.put(neighbor, currentWord);
                    }
                }
            }
        }

        List<String> shortestPath = new ArrayList<>();
        for (String word : graph.keySet()) {
            if (!word.equals(startWord)) {
                StringBuilder path = new StringBuilder();
                String currentWord = word;
                while (currentWord != null) {
                    path.insert(0, currentWord + " -> ");
                    currentWord = previousWords.get(currentWord);
                }
                // 删除最后一个 "-> "
                if (path.length() > 3) {
                    path.delete(path.length() - 3, path.length());
                }
                shortestPath.add(path.toString());
            }
        }

        return shortestPath;
    }

    public static void randomTraversal(Map<String, Map<String, Integer>> graph) throws IOException {
        Random random = new Random();
        List<String> nodes = new ArrayList<>(graph.keySet());
        String currentNode = nodes.get(random.nextInt(nodes.size()));
        Set<String> visitedEdges = new HashSet<>();
        List<String> traversalResult = new ArrayList<>();

        while (true) {
            traversalResult.add(currentNode);
            Map<String, Integer> edges = graph.get(currentNode);
            if (edges == null || edges.isEmpty()) {
                break;
            }

            List<String> nextNodes = new ArrayList<>(edges.keySet());
            String nextNode = nextNodes.get(random.nextInt(nextNodes.size()));
            String edge = currentNode + " -> " + nextNode;

            if (visitedEdges.contains(edge)) {
                break;
            }

            visitedEdges.add(edge);
            currentNode = nextNode;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("traversalResult.txt"))) {
            for (String node : traversalResult) {
                writer.write(node + "\n");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        //文件预处理
        String filePath = "text.txt"; // 文件路径
        processFile(filePath);
        Map<String, Map<String, Integer>> graph = createGraphAndReturn(filePath); // 修改这里，直接返回创建的图

        WriteDOT(graph);
//-----------------------------------------------------------------------------------
        String dotFilePath = "output.dot"; // 输入的dot文件路径
        String imageFilePath = "image.png"; // 输出的图像文件路径
        convertDotToImage(dotFilePath, imageFilePath);
//-----------------------------------------------------------------------------------
        //获取用户输入
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the first word:");
        String word1 = scanner.nextLine().toLowerCase();
        System.out.println("Please enter the second word:");
        String word2 = scanner.nextLine().toLowerCase();

        //查询桥接词
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            System.out.println("No " + word1 + " or " + word2 + " in the graph!");
        } else{
            List<String> bridgeWords = findBridgeWords(word1, word2, graph);
            if (bridgeWords.isEmpty()) {
                System.out.println("No bridge words from " + word1 + " to " + word2 + "!");
            } else {
                System.out.println("The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords) + ".");
            }
        }
//-----------------------------------------------------------------------------------
        // 获取用户输入的新文本
        System.out.println("Please enter the new text:");
        @SuppressWarnings("resource")
        Scanner scanner1 = new Scanner(System.in);
        String newText = scanner1.nextLine();

        // 处理新文本并输出结果
        String result = processNewText(newText, graph);
        System.out.println("The processed text is: " + result);
//-----------------------------------------------------------------------------------
        // 获取用户输入的单词
        @SuppressWarnings({ "resource", "unused" })
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("Please enter the number of words:");
        int numWords = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (numWords == 2) {
            System.out.println("Please enter the first word:");
            String word3 = scanner.nextLine().toLowerCase();
            System.out.println("Please enter the second word:");
            String word4 = scanner.nextLine().toLowerCase();

            // 查找两个单词之间的所有最短路径
            List<List<String>> allShortestPaths = findAllShortestPaths(word3, word4, graph);
            if (allShortestPaths.isEmpty()) {
                System.out.println("No path from " + word3 + " to " + word4 + "!");
            } else {
                for (List<String> path : allShortestPaths) {
                    System.out.println("One of the shortest paths from " + word3 + " to " + word4 + " is: " + String.join(" -> ", path));
                    System.out.println("The length of the path is: " + (path.size() - 1));
                }
            }
        } else if (numWords == 1) {
            System.out.println("Please enter a word:");
            String word = scanner.nextLine().toLowerCase();

            // 如果用户输入了一个单词，计算并输出最短路径
            if (word.length() > 0) {
                List<String> shortestPaths = OneNodefindShortestPath(graph, word);
                for (String path : shortestPaths) {
                    System.out.println(path);
                }
            } else {
                System.out.println("No word entered.");
            }
        } else {
            System.out.println("Invalid number of words entered.");
        }
//-----------------------------------------------------------------------------------
        //开始随机游走
        System.out.println("Begin random walk:");
        randomTraversal(graph);//2-R5
        //1-R3
        //2-R4
    }
    //2-R7
}
