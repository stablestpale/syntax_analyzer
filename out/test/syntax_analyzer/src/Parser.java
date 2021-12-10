import utils.FileUtil;

import java.io.StringWriter;
import java.util.*;

/**
 * @author zzy
 * @description: 语法分析
 * @date 2021/12/4 21:00
 */


class Parser {
    private FileUtil fileUtil;
    private Set<String> terminals;
    private Set<String> nonTerminals;
    private Map<String, Set<String>> firsts;
    private Map<String, Set<String>> follows;
    private List<Production> productions;

    Parser(String filePath) {
        this.fileUtil = new FileUtil(filePath, "|");
        this.productions = new ArrayList<>();
        this.terminals = new HashSet<>();
        this.nonTerminals = new HashSet<>();
        this.firsts = new HashMap<>();
        this.follows = new HashMap<>();
    }

    void analyze() {
        System.out.println("\n\n******语法分析******");
        readProductions();
        getFirst();
        getFollow();
        System.out.println("******语法分析******");
    }

    /*
     * @description: 获得终结符与非终结符
     * @param: [productions, fileUtil]
     * @return: void
     * @date:  2021/12/10
     */
    private void readProductions() {
        String[] productionStrings = fileUtil.read().split("\\|");
        for(String productionString: productionStrings) {
            String left = productionString.split("->")[0].trim();
            String[] right = productionString.split("->")[1].trim().split(" ");
            terminals.addAll(Arrays.asList(right)); // 将所有右侧字符加入终结符集
            nonTerminals.add(left);
            Production production = new Production(left, right);
            productions.add(production);
        }
        terminals.removeAll(nonTerminals); //剔除所有非终结符
        //System.out.println(productions.toString());
        System.out.println("终结符：  " + terminals.toString());
        System.out.println("非终结符: " + nonTerminals.toString());
    }

    /*
     * @description: 获取First集
     * @param: []
     * @return: void
     * @date: 17:39 2021/12/10
     */
    private void getFirst() {
        // 终结符First集为自身
        for(String terminal: terminals) {
            firsts.put(terminal, new HashSet<String>(){{add(terminal);}});
        }
        // 求非终结符first集，直至没有可以更新的元素
        boolean flag = true;
        while (flag) {
            flag = false;
            for(Production production: productions) {
                String left = production.getLeft();
                String[] rights = production.getRight();
                for(String right: rights) {
                    // 如果当前为e，则跳过；否则将当前所有加入到left的first集中，并且结束循环
                    if("e".equals(right)) continue;
                    for(String string: firsts.getOrDefault(right, new HashSet<>())) {
                        if(firsts.getOrDefault(left, new HashSet<>()).contains(string)) continue;
                        // 将更新的flag设为true，证明已更新，while循环继续
                        flag = true;
                        Set<String> leftFirst = firsts.getOrDefault(left, new HashSet<>());
                        leftFirst.add(string);
                        firsts.put(left, leftFirst);
                    }
                    break;
                }
            }
        }
        System.out.println("First集:");
        Set<Map.Entry<String, Set<String>>> entries = firsts.entrySet();
        for (Map.Entry entry: entries) {
            // 不展示终结符的first集
            if(nonTerminals.contains(entry.getKey().toString())) System.out.println("   " + entry.getKey() + ":  " + entry.getValue().toString());
        }
    }

    /*
     * @description: 获取Follow集
     * @param: []
     * @return: void
     * @date: 20:23 2021/12/10
     */
    private void getFollow() {
        follows.put("S", new HashSet<String>(){{add("#");}});
        boolean flag = true;
        while(flag) {
            flag = false;
            for(Production production: productions) {
                String left = production.getLeft();
                String[] rights = production.getRight();
                int len = rights.length;
                // 最右符号添加结束符#
                Set<String> lastFollow = follows.getOrDefault(rights[len - 1], new HashSet<>());
                lastFollow.add("#");
                // 再更新最右字符为非终结符的情况，此时 follow(B) += follow(A)，要求 A != B
                if(nonTerminals.contains(rights[len - 1])) {
                    for(String string: follows.getOrDefault(left, new HashSet<>())) {
                        // 跳过已包含
                        if(lastFollow.contains(string)) continue;
                        //本次有更新，while循环继续
                        flag = true;
                        lastFollow.add(string);
                    }
                }
                follows.put(rights[len - 1], lastFollow);

                // 找出 A -> aBb 情况中的B
                for(int i = 0; i < rights.length - 1; ++i) {
                    if(!nonTerminals.contains(rights[i])) continue;
                    Set<String> bFollow = follows.getOrDefault(rights[i], new HashSet<>());
                    // 若 b != e，则follow(B) += First(b)
                    if(!"e".equals(rights[i + 1])) {
                        for(String string: firsts.getOrDefault(rights[i + 1], new HashSet<>())) {
                            // 去除first(b)中的e，和跳过已包含
                            if("e".equals(string) || follows.getOrDefault(rights[i], new HashSet<>()).contains(string)) continue;
                            //本次有更新，while循环继续
                            flag = true;
                            bFollow.add(string);
                        }
                    } else if(i == len - 2){
                        // b == e，则follow(B) += follow(A)
                        for(String string: follows.getOrDefault(left, new HashSet<>())) {
                            // 跳过已包含
                            if(follows.getOrDefault(rights[i], new HashSet<>()).contains(string)) continue;
                            //本次有更新，while循环继续
                            flag = true;
                            bFollow.add(string);
                        }
                    }
                    follows.put(rights[i], bFollow);
                }
            }
        }
        System.out.println("Follow集:");
        Set<Map.Entry<String, Set<String>>> entries = follows.entrySet();
        for (Map.Entry entry: entries) {
            // 不展示终结符的first集
            if(nonTerminals.contains(entry.getKey().toString())) System.out.println("   " + entry.getKey() + ":  " + entry.getValue().toString());
        }
    }

}
