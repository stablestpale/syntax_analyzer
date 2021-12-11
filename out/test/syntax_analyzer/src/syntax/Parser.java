package syntax;

import utils.FileUtil;

import java.util.*;

/**
 * @author zzy
 * @description: 语法分析
 * @date 2021/12/4 21:00
 */


public class Parser {
    private FileUtil fileUtil;
    private Set<String> terminals;
    private Set<String> nonTerminals;
    private Map<String, Set<String>> firsts;
    private Map<String, Set<String>> follows;
    private List<Production> productions;
    private List<Closure> closures;
    private Grammar grammar;
    private List<GoTo> goToList;
    private Map<Integer, Map<String, Integer>> table;
    private List<String> tokens;

    public Parser(String filePath, List<String> tokens) {
        this.fileUtil = new FileUtil(filePath, "|");
        this.productions = new ArrayList<>();
        this.terminals = new HashSet<>();
        this.nonTerminals = new HashSet<>();
        this.firsts = new HashMap<>();
        this.follows = new HashMap<>();
        this.closures = new ArrayList<>();
        this.grammar = new Grammar(terminals, nonTerminals, firsts, follows, productions);
        this.goToList = new ArrayList<>();
        this.tokens = tokens;
    }

    public void analyze() {
        System.out.println("\n\n******语法分析******");
        fileUtil.write("\n\n******语法分析******");
        readProductions();
        getFirst();
        getFollow();
        getClosure();
        getSLRTable();
        parse();
        System.out.println("******语法分析******");
        fileUtil.write("******语法分析******");
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
        fileUtil.write("终结符：  " + terminals.toString());
        fileUtil.write("非终结符: " + nonTerminals.toString());
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
        fileUtil.write("First集:");
        Set<Map.Entry<String, Set<String>>> entries = firsts.entrySet();
        for (Map.Entry entry: entries) {
            // 不展示终结符的first集
            if(nonTerminals.contains(entry.getKey().toString())) {
                System.out.println("   " + entry.getKey() + ":  " + entry.getValue().toString());
                fileUtil.write("   " + entry.getKey() + ":  " + entry.getValue().toString());
            }
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
        fileUtil.write("Follow集:");
        Set<Map.Entry<String, Set<String>>> entries = follows.entrySet();
        for (Map.Entry entry: entries) {
            // 不展示终结符的first集
            if(nonTerminals.contains(entry.getKey().toString())) {
                System.out.println("   " + entry.getKey() + ":  " + entry.getValue().toString());
                fileUtil.write("   " + entry.getKey() + ":  " + entry.getValue().toString());
            }
        }
    }

    /*
     * @description: 生成状态转换闭包
     * @param: []
     * @return: void
     * @date: 21:20 2021/12/10
     */
    private void getClosure() {
        // 记录闭包存在状态
        Map<Closure, Integer> state = new HashMap<>();
        Closure firstClosure = new Closure(grammar);
        Map<Production, Integer> originSet = new HashMap();
        // 将开始符号产生式I0加入到closure中
        firstClosure.insert(productions.get(0).getLeft(), productions.get(0).getRight(), 0);
        firstClosure.expand();
        closures.add(firstClosure);
        state.put(firstClosure, 0);

        // 使用当前闭包计算剩余闭包
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        while(!queue.isEmpty()) {
            int index = queue.poll();
            // 待拓展闭包
            Closure oldClosure = closures.get(index);
            // 若当前闭包没有可以扩展的表达式，则继续
            if(oldClosure.gotoMap.isEmpty()) continue;
            Set<Map.Entry<String, List<Production>>> entries = oldClosure.gotoMap.entrySet();
            for(Map.Entry entry: entries) {
                String key = entry.getKey().toString();
                if("e".equals(key)) continue; // 若当前key为e，则跳过
                List<Production> values = oldClosure.gotoMap.get(key);
                Closure newClosure = new Closure(grammar);
                for(Production production: values) {
                    Production newProduction = new Production(production.getLeft(), production.getRight(), production.getPosition() + 1);
                    newClosure.insert(production.getLeft(), production.getRight(), production.getPosition() + 1);
                }
                //检查当前状态是否已存在
                if(state.containsKey(newClosure)) {
                    // 如果已存在当前状态，则更新边
                     goToList.add(new GoTo(index, key, state.get(newClosure)));
                } else if(newClosure.originProductions.size() != 0){
                    newClosure.expand();
                    closures.add(newClosure);
                    int newIndex = closures.size() - 1;
                    queue.add(newIndex);
                    state.put(newClosure, newIndex);
                    goToList.add(new GoTo(index, key, newIndex));
                }
            }
        }
        outputClosure();
        outputGoTo();
    }

    /*
     * @description: 生成SLR分析表
     * @param: []
     * @return: void
     * @date: 1:52 2021/12/11
     */
    private void getSLRTable() {
        table = new HashMap<>();
        for(GoTo goTo: goToList) {
            Map<String, Integer> current = table.getOrDefault(goTo.getStartIndex(), new HashMap<>());
            current.put(goTo.getToken(), goTo.getEndIndex());
            table.put(goTo.getStartIndex(), current);
        }
        for(int i = 0; i < closures.size(); ++i) {
            Closure closure = closures.get(i);
            List<Production> origin = closure.originProductions;
            for(Production production: origin) {
                if(production.getRight().length != production.getPosition()) continue;
                // follow集符合即可规约
                Set<String> follow = follows.get(production.getLeft());
                for(String string: follow) {
                    if("S".equals(production.getLeft())) {
                        Map<String, Integer> current = table.getOrDefault(i, new HashMap<>());
                        current.put(string, Integer.MIN_VALUE); // 使用最小值代替acc
                        table.put(i, current);
                    } else {
                        Map<String, Integer> current = table.getOrDefault(i, new HashMap<>());
                        // 使用负值记录待规约表达式
                        for(int j = 0; j < productions.size(); ++j) {
                            Production inputProduction = productions.get(j);
                            if(!inputProduction.getLeft().equals(production.getLeft()) || !Arrays.deepEquals(inputProduction.getRight(), production.getRight())) continue;
                            current.put(string, -j);
                            break;
                        }
                        table.put(i, current);
                    }
                }
            }
        }
        outputTable();
    }

    /*
     * @description: 进行语法分析
     * @param: []
     * @return: void
     * @date: 3:06 2021/12/11
     */
    private void parse() {
        System.out.println("\n");
        Deque<Integer> stack = new LinkedList<>();
        Deque<String> input = new LinkedList<>();
        int index = 0;
        stack.push(0);
        input.push("#");
        tokens.add("#");
        System.out.println("tokens:" + tokens);
        while(true) {
            Integer s = stack.peek();
            if(!table.containsKey(s) || !table.getOrDefault(s, new HashMap<>()).containsKey(tokens.get(index))) {
                System.out.println(s + " " + tokens.get(index) + " " + table.containsKey(s) + " " + table.getOrDefault(s, new HashMap<>()).containsKey(tokens.get(index)) + " ERROR");
                return;
            }
            Integer next = table.get(s).get(tokens.get(index));
            // 判别结束
            if(next == Integer.MIN_VALUE) {
                System.out.println("success!");
                return;
            } else if(next < 0) {
                // 此时按照第next个表达式进行归约
                next = -next;
                Production production = productions.get(next);
                String[] right = production.getRight();
                for(int i = right.length - 1; i >= 0; --i) {
                    if(!input.peek().equals(right[i])) {
                        System.out.printf("ERROR");
                        return;
                    }
                    input.pop();
                    stack.pop();
                }
                // 将A和goto[栈顶， A]压入栈中
                input.push(production.getLeft());
                stack.push(table.get(stack.peek()).get(production.getLeft()));
                System.out.println("规约后: " + stack.toString() + "  " + input.toString());
            } else {
                stack.push(next);
                input.push(tokens.get(index));
                ++index;
                System.out.println("移进后: " + stack.toString() + "  " + input.toString());
            }
        }
    }

    private void outputClosure() {
        // 输出闭包
        System.out.println("\n GRAPH:");
        for(int i = 0; i < closures.size(); ++i) {
            System.out.println("  I" + i + ":");
            Closure closure = closures.get(i);
            for(Production production: closure.allProductions) {
                System.out.print(production.getLeft() + " -> ");
                for(int j = 0; j < production.getRight().length; ++j) {
                    if(j == production.position) System.out.print(".");
                    System.out.print(production.getRight()[j] + " ");
                }
                if(production.getPosition() == production.getRight().length) System.out.print(".");
                System.out.println("");
            }
            System.out.println("");
        }
    }

    private void outputGoTo() {
        // 输出闭包间的边
        System.out.println("\n EDGES:");
        for(GoTo goTo: goToList) {
            System.out.println(goTo.toString());
        }
    }

    private void outputTable() {
        // 输出SLR分析表
        System.out.println("\n TABLE:");
        System.out.println("state                       actions & goto");
        System.out.print("    #    ");
        for(String terminal: terminals) {
            System.out.printf("%-5s", terminal);
        }
        for(String nonTerminal: nonTerminals) {
            System.out.printf("%-5s", nonTerminal);
        }
        System.out.println();
        for(int state = 0; state < closures.size(); ++state) {
            Map<String, Integer> map = table.getOrDefault(state, new HashMap<>());
            System.out.printf("%-2d  ", state);
            if(map.containsKey("#")) {
                if(map.get("#") == Integer.MIN_VALUE) System.out.print("acc  ");
                else if(map.get("#") > 0) System.out.printf("s%-4d", map.get("#"));
                else System.out.printf("r%-4d", -map.get("#"));
            } else System.out.print("-    ");
            for(String string: terminals) {
                if(map.containsKey(string)) {
                    if(map.get(string) > 0) System.out.printf("s%-4d", map.get(string));
                    else System.out.printf("r%-4d", -map.get(string));
                } else System.out.print("-    ");
            }
            for(String string: nonTerminals) {
                if(map.containsKey(string))  System.out.printf("%-5d", map.get(string));
                else System.out.print("-    ");
            }
            System.out.println();
        }
    }
}
