package com.atguigu.com;

import junit.framework.TestCase;
import org.junit.Test;
import java.util.*;

public class QueryBridgeWordsTest extends TestCase {
        @Test
        public void testcase1(){
            // 测试用例1：word1在图中出现word2不在图中出现
            Map<String, Map<String, Integer>> graph1 = new HashMap<>();
            graph1.put("apple", new HashMap<String, Integer>());
            graph1.get("apple").put("banana", 1);
            graph1.put("banana", new HashMap<String, Integer>());
            graph1.get("banana").put("orange", 1);
            graph1.put("orange", new HashMap<String, Integer>());
            List<String> result1 = App.queryBridgeWords("apple", "grape", graph1);
            assertTrue(result1.isEmpty());
        }

        @Test
        public void testcase2(){
            // 测试用例2：word2在图中出现word1不在图中出现
            Map<String, Map<String, Integer>> graph2 = new HashMap<>();
            graph2.put("apple", new HashMap<String, Integer>());
            graph2.get("apple").put("banana", 1);
            graph2.put("banana", new HashMap<String, Integer>());
            graph2.get("banana").put("orange", 1);
            graph2.put("orange", new HashMap<String, Integer>());
            List<String> result2 = App.queryBridgeWords("grape", "apple", graph2);
            assertTrue(result2.isEmpty());
        }

        @Test
        public void testcase3(){
            // 测试用例3：word1和word2都在图中出现，word1和word2相同，不存在桥接词
            Map<String, Map<String, Integer>> graph3 = new HashMap<>();
            graph3.put("apple", new HashMap<String, Integer>());
            graph3.get("apple").put("banana", 1);
            graph3.put("banana", new HashMap<String, Integer>());
            graph3.get("banana").put("orange", 1);
            graph3.put("orange", new HashMap<String, Integer>());
            List<String> result3 = App.queryBridgeWords("apple", "apple", graph3);
            assertTrue(result3.isEmpty());
        }

        @Test
        public void testcase4(){
            // 测试用例4：word1和word2都在图中出现，word1和word2不同，不存在桥接词
            Map<String, Map<String, Integer>> graph4 = new HashMap<>();
            graph4.put("apple", new HashMap<String, Integer>());
            graph4.get("apple").put("banana", 1);
            graph4.put("banana", new HashMap<String, Integer>());
            graph4.get("banana").put("orange", 1);
            graph4.put("orange", new HashMap<String, Integer>());
            List<String> result4 = App.queryBridgeWords("banana", "orange", graph4);
            assertTrue(result4.isEmpty());
        }

        @Test
        public void testcase5(){
            // 测试用例5：word1和word2都不在图中出现
            Map<String, Map<String, Integer>> graph5 = new HashMap<>();
            graph5.put("apple", new HashMap<String, Integer>());
            graph5.get("apple").put("banana", 1);
            graph5.put("banana", new HashMap<String, Integer>());
            graph5.get("banana").put("orange", 1);
            graph5.put("orange", new HashMap<String, Integer>());
            List<String> result5 = App.queryBridgeWords("lemon", "grape", graph5);
            assertTrue(result5.isEmpty());
        }

        @Test
        public void testcase6(){
            // 测试用例6：word1和word2不同，存在一个桥接词
            Map<String, Map<String, Integer>> graph6 = new HashMap<>();
            graph6.put("apple", new HashMap<String, Integer>());
            graph6.get("apple").put("banana", 1);
            graph6.put("banana", new HashMap<String, Integer>());
            graph6.get("banana").put("orange", 1);
            graph6.put("orange", new HashMap<String, Integer>());
            List<String> result6 = App.queryBridgeWords("apple", "orange", graph6);
            // 验证结果是否符合预期
            List<String> expectedBridgeWords1 = new ArrayList<>();
            expectedBridgeWords1.add("banana");
            assertEquals(expectedBridgeWords1, result6);
        }

        @Test
        public void testcase7(){
            // 测试用例7：word1和word2不同,存在多个桥接词
            Map<String, Map<String, Integer>> graph7 = new HashMap<>();
            graph7.put("apple", new HashMap<String, Integer>());
            graph7.get("apple").put("banana", 1);
            graph7.get("apple").put("lemon", 1);
            graph7.put("banana", new HashMap<String, Integer>());
            graph7.get("banana").put("orange", 1);
            graph7.put("lemon", new HashMap<String, Integer>());
            graph7.get("lemon").put("orange", 1);
            graph7.put("orange", new HashMap<String, Integer>());
            List<String> result7 = App.queryBridgeWords("apple", "orange", graph7);
            // 验证结果是否符合预期
            List<String> expectedBridgeWords2 = new ArrayList<>();
            expectedBridgeWords2.add("banana");
            expectedBridgeWords2.add("lemon");
            assertEquals(expectedBridgeWords2, result7);
        }

        @Test
        public void testcase8(){
            //测试用例8：graph为空
            Map<String, Map<String, Integer>> graph8 = new HashMap<>();
            List<String> result8 = App.queryBridgeWords("apple", "orange", graph8);
            assertTrue(result8.isEmpty());
        }

        @Test
        public void testcase9(){
            //测试用例9：输入单词个数不为2
            Map<String, Map<String, Integer>> graph9 = new HashMap<>();
            graph9.put("apple", new HashMap<String, Integer>());
            graph9.get("apple").put("banana", 1);
            graph9.put("banana", new HashMap<String, Integer>());
            graph9.get("banana").put("orange", 1);
            graph9.put("orange", new HashMap<String, Integer>());
            List<String> result9 = App.queryBridgeWords("apple", "", graph9);
            assertTrue(result9.isEmpty());
        }
}
