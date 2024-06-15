package com.atguigu.com;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest{
    @Test
    public void test1() {
        //测试用例1
        Map<String, Map<String, Integer>> graph1 = new HashMap<>();
        try {
            System.out.println("test1:");
            App.randomWalk(graph1);
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            // Test passed if an exception is thrown
        }
    }

    @Test
    public void test2() {
        //测试用例2
        Map<String, Map<String, Integer>> graph2 = new HashMap<>();
        graph2.put("node1", new HashMap<>());
        try {
            System.out.println("test2:");
            App.randomWalk(graph2);
        } catch (IOException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void test3() {
        //测试用例3
        // 创建一个名为graph的HashMap，用于存储图中的节点和边
        Map<String, Map<String, Integer>> graph3 = new HashMap<>();
        // 在graph中添加一个名为"node1"的节点，并为其添加一个空的HashMap作为其邻接表
        graph3.put("node1", new HashMap<>());
        // 在"node1"的邻接表中添加一个名为"node2"的节点，权重为1
        graph3.get("node1").put("node2", 1);
        // 在graph中添加一个名为"node2"的节点，并为其添加一个空的HashMap作为其邻接表
        graph3.put("node2", new HashMap<>());
        try {
            System.out.println("test3:");
            // 调用App类的randomWalk方法，传入graph作为参数进行随机游走
            App.randomWalk(graph3);
        } catch (IOException e) {
            // 如果发生IOException异常，则测试失败，输出错误信息
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void test4() {
        //测试用例4
        Map<String, Map<String, Integer>> graph4 = new HashMap<>();
        graph4.put("node1", new HashMap<>());
        graph4.get("node1").put("node1", 1);
        try {
            System.out.println("test4:");
            App.randomWalk(graph4);
        } catch (IOException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void test5() {
        //测试用例5
        Map<String, Map<String, Integer>> graph5 = new HashMap<>();
        graph5.put("node1", new HashMap<>());
        graph5.get("node1").put("",0);
        try {
            System.out.println("test5:");
            App.randomWalk(graph5);
        } catch (IOException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}

