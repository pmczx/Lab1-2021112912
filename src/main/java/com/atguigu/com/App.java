package com.atguigu.com;

import com.kitfox.svg.A;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.Set;

/**.
 * 该类包含处理文本文件、创建图、查找桥接词、处理新文本和查找最短路径的方法。
 */
public class App {
  // 将graph变量设置为静态变量
  static SecureRandom random = new SecureRandom();
  static Map<String, Map<String, Integer>> graph = new HashMap<>();
  /**.
   * 处理文件，替换换行符、回车符、标点符号和非字母字符。

   * @param filePath 要处理的文件路径
   */

  public static void processFile(String filePath) {
    try {
      InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
      BufferedReader reader = new BufferedReader(isr);
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.replace("\n", " ").replace("\r", " "); // 替换换行符和回车符
        line = line.replaceAll("\\p{Punct}", " "); // 替换标点符号
        line = line.replaceAll("[^a-zA-Z ]", ""); // 移除非字母的字符
        line = line.toLowerCase(); // 将所有大写字母转化为小写字母
        sb.append(line).append(" ");
      }
      reader.close();
      filePath = "text.txt";
      OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
      BufferedWriter writer = new BufferedWriter(osw);
      writer.write(sb.toString());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**.
     * 根据给定的文件路径创建一个图并返回。

     * @param filePath 要读取的文件路径
     * @return 一个表示文本数据的图，其中节点表示单词，边表示单词之间的连接
     * @throws IOException 如果读取文件时发生错误
     */
  public static Map<String, Map<String, Integer>>
      createGraphAndReturn(String filePath) throws IOException {
    // 读取文本数据
    InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
    BufferedReader reader = new BufferedReader(isr);
    String line;
    Map<String, Map<String, Integer>> graph = new HashMap<>();

    // 循环读取每一行文本
    while ((line = reader.readLine()) != null) {
      // 将文本数据分割成单词
      String[] words = line.toLowerCase().split("\\s+");

      // 遍历每个单词
      for (int i = 0; i < words.length - 1; i++) {
        // 如果图中不存在当前单词，添加该单词作为新节点
        if (!graph.containsKey(words[i])) {
          graph.put(words[i], new HashMap<String, Integer>());
        }
        // 如果当前单词和下一个单词之间没有边，添加一条新的边并设置权重为1
        if (!graph.get(words[i]).containsKey(words[i + 1])) {
          graph.get(words[i]).put(words[i + 1], 1);
        } else {
          // 如果当前单词和下一个单词之间已经有边，将这条边的权重加1
          graph.get(words[i]).put(words[i + 1],
              graph.get(words[i]).get(words[i + 1]) + 1);
        }
      }
    }
    reader.close();
    return graph;
  }

  /**.
   * 将图写入到output.dot文件中。

   * @param graph 要写入的图
   * @throws IOException 如果写入文件时发生错误
   */
  public static void writeDot(Map<String, Map<String, Integer>> graph)throws IOException {
    // 将图写入到output.dot文件中
    OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("output.dot"), StandardCharsets.UTF_8);
    BufferedWriter writer = new BufferedWriter(osw);
    writer.write("digraph shapes {\n");
    for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
      for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
        writer.write(
                entry.getKey() + " -> " + subEntry.getKey() + " [label=\"" + subEntry.getValue()
                        + "\"];\n");
      }
    }
    writer.write("}");
    writer.close();
  }

  /**.
   * 将Dot文件转换为PNG图像。

   * @param dotFilePath Dot文件的路径
   * @param imageFilePath 生成的PNG图像的路径
   */
  public static void showDirectedGraph(String dotFilePath, String imageFilePath) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(
              "dot", "-Tpng", dotFilePath, "-o", imageFilePath);
      Process process = processBuilder.start();
      process.waitFor();
      System.out.println("Dot file converted to PNG image successfully!");
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**.
   * 在给定的图中查找两个单词之间的桥梁词。

   * @param word1 第一个单词
   * @param word2 第二个单词
   * @param graph 要搜索的图
   * @return 一个包含桥梁词的列表
   */
  public static List<String> queryBridgeWords(String word1, String word2,
                                              Map<String, Map<String, Integer>> graph) {
    List<String> bridgeWords = new ArrayList<>();

    if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
      System.out.println("No word1 or word2 in the graph!");
      return bridgeWords;
    }

    /*for (String word3 : graph.keySet()) {
      if (graph.get(word1).containsKey(word3) && graph.get(word3).containsKey(word2)) {
        bridgeWords.add(word3);
      }
    }*/
    for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
      String word3 = entry.getKey();
      if (graph.get(word1).containsKey(word3) && entry.getValue().containsKey(word2)) {
        bridgeWords.add(word3);
      }
    }

    if (bridgeWords.isEmpty()) {
      System.out.println("No bridge words from word1 to word2!");
    } else {
      System.out.println("The bridge words from word1 to word2 are: "
              + String.join(", ", bridgeWords));
    }

    return bridgeWords;
  }

  /**.
   * 处理新的文本，根据给定的图插入桥梁词。

   * @param newText 要处理的新文本
   * @param graph 用于查找桥梁词的图
   * @return 处理后的文本，其中桥梁词已插入
   */
  public static String generateNewText(String newText, Map<String, Map<String, Integer>> graph) {
    String[] words = newText.toLowerCase().split("\\s+");
    StringBuilder sb = new StringBuilder();
    App ruse = new App();
    for (int i = 0; i < words.length - 1; i++) {
      List<String> bridgeWords = queryBridgeWords(words[i], words[i + 1], graph);
      if (!bridgeWords.isEmpty()) {
        String bridgeWord = bridgeWords.get(ruse.random.nextInt(bridgeWords.size()));
        sb.append(words[i]).append(" ").append(bridgeWord).append(" ");
      } else {
        sb.append(words[i]).append(" ");
      }
    }
    sb.append(words[words.length - 1]);
    return sb.toString();
  }

  /**.
   * 在给定的图中查找从word1到word2的所有最短路径。

   * @param word1 起始单词
   * @param word2 目标单词
   * @param graph 要搜索的图
   * @return 一个包含所有最短路径的列表，每个路径都是一个字符串列表
   */
  public static List<List<String>> calcShortestPath(
          String word1, String word2, Map<String, Map<String, Integer>> graph) {
    Queue<List<String>> queue = new LinkedList<>();
    Set<String> visited = new HashSet<>();
    queue.add(Collections.singletonList(word1));

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

  /**.
   * 查找从给定起始单词到图中所有其他单词的最短路径。
   *
   * @param graph 一个表示单词关系的图，其中键是单词，值是一个映射，表示与该单词相邻的单词及其权重。
   * @param startWord 要开始搜索的单词。
   * @return 一个包含从起始单词到图中所有其他单词的最短路径的列表。
   */
  public static List<String> oneNodefindShortestPath(Map<String,
          Map<String, Integer>> graph, String startWord) {
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

  /**.
   * 从图中随机选择一个节点，然后进行随机游走，直到遇到已访问过的边或没有相邻节点为止。
   * 将遍历结果写入名为"traversalResult.txt"的文件中。

   * @param graph 一个表示单词关系的图，其中键是单词，值是一个映射，表示与该单词相邻的单词及其权重。
   * @throws IOException 如果写入文件时发生错误。
  */
  public static void randomWalk(Map<String, Map<String, Integer>> graph) throws IOException {
    SecureRandom random = new SecureRandom();
    List<String> nodes = new ArrayList<>(graph.keySet());
    String currentNode = nodes.get(random.nextInt(nodes.size()));
    Set<String> visitedEdges = new HashSet<>();
    List<String> traversalResult = new ArrayList<>();

    while (true) {
      traversalResult.add(currentNode);
      Map<String, Integer> edges = graph.get(currentNode);
      if (edges == null || edges.isEmpty()) {//判断边是否为空或没有边；
        break;
      }

      List<String> nextNodes = new ArrayList<>(edges.keySet());
      String nextNode = nextNodes.get(random.nextInt(nextNodes.size()));
      String edge = currentNode + " -> " + nextNode;

      if (visitedEdges.contains(edge)) {//判断边是否已经被访问过；
        break;
      }

      visitedEdges.add(edge);
      currentNode = nextNode;

    }

    try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("traversalResult.txt"), StandardCharsets.UTF_8)) {
      BufferedWriter writer = new BufferedWriter(osw);
      for (String node : traversalResult) {//判断遍历结果列表是否为空
        System.out.println(node);
      }
      writer.close();
    }
    System.out.println("\n");
  }

  /**.
   * 主函数，用于执行以下操作：
   * 1. 读取文本文件并创建图。
   * 2. 将图转换为DOT格式并保存为文件。
   * 3. 将DOT文件转换为图像文件。
   * 4. 根据用户输入的两个单词，查找它们之间的桥接词。
   * 5. 根据用户输入的新文本，处理新文本并输出结果。
   * 6. 根据用户输入的单词数量，执行以下操作：
   *    a. 如果输入了两个单词，查找它们之间的所有最短路径。
   *    b. 如果输入了一个单词，计算并输出从该单词到图中所有其他单词的最短路径。
   * 7. 从图中随机选择一个节点，然后进行随机游走，直到遇到已访问过的边或没有相邻节点为止。
   *
   * @param args 命令行参数（未使用）。
   * @throws IOException 如果读取文件或写入文件时发生错误。
   */
  public static void main(String[] args) throws IOException {

    //文件预处理
    String filePath = "text.txt"; // 文件路径
    processFile(filePath);
    Map<String, Map<String, Integer>> graph = createGraphAndReturn(filePath); // 修改这里，直接返回创建的图

    writeDot(graph);

    String dotFilePath = "output.dot"; // 输入的dot文件路径
    String imageFilePath = "image.png"; // 输出的图像文件路径
    showDirectedGraph(dotFilePath, imageFilePath);

    //获取用户输入
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    System.out.println("Please enter the first word:");
    String word1 = scanner.nextLine().toLowerCase();
    System.out.println("Please enter the second word:");
    String word2 = scanner.nextLine().toLowerCase();

    //查询桥接词
    //List<String> bridgeWords = queryBridgeWords(word1, word2, graph);
    queryBridgeWords(word1, word2, graph);

    // 获取用户输入的新文本
    System.out.println("Please enter the new text:");
    Scanner scanner1 = new Scanner(System.in, StandardCharsets.UTF_8);
    String newText = scanner1.nextLine();

    // 处理新文本并输出结果
    String result = generateNewText(newText, graph);
    System.out.println("The processed text is: " + result);

    // 获取用户输入的单词
    System.out.println("Please enter the number of words:");
    int numWords = scanner.nextInt();
    scanner.nextLine(); // consume newline

    if (numWords == 2) {
      System.out.println("Please enter the first word:");
      String word3 = scanner.nextLine().toLowerCase();
      System.out.println("Please enter the second word:");
      String word4 = scanner.nextLine().toLowerCase();

      // 查找两个单词之间的所有最短路径
      List<List<String>> allShortestPaths = calcShortestPath(word3, word4, graph);
      if (allShortestPaths.isEmpty()) {
        System.out.println("No path from " + word3 + " to " + word4 + "!");
      } else {
        for (List<String> path : allShortestPaths) {
          System.out.println("One of the shortest paths from "
                  + word3 + " to " + word4 + " is: " + String.join(" -> ", path));
          System.out.println("The length of the path is: " + (path.size() - 1));
        }
      }
    } else if (numWords == 1) {
      System.out.println("Please enter a word:");
      String word = scanner.nextLine().toLowerCase();

      // 如果用户输入了一个单词，计算并输出最短路径
      if (!word.isEmpty()) {
        List<String> shortestPaths = oneNodefindShortestPath(graph, word);
        for (String path : shortestPaths) {
          System.out.println(path);
        }
      } else {
        System.out.println("No word entered.");
      }
    } else {
      System.out.println("Invalid number of words entered.");
    }

    //开始随机游走
    System.out.println("Begin random walk:");
    randomWalk(graph);
    scanner.close();
    scanner1.close();
  }
}
