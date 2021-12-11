package syntax;

import utils.FileUtil;

import java.util.*;

/**
 * @author zzy
 * @description: �﷨����
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
        System.out.println("\n\n******�﷨����******");
        fileUtil.write("\n\n******�﷨����******");
        readProductions();
        getFirst();
        getFollow();
        getClosure();
        getSLRTable();
        parse();
        System.out.println("******�﷨����******");
        fileUtil.write("******�﷨����******");
    }

    /*
     * @description: ����ս������ս��
     * @param: [productions, fileUtil]
     * @return: void
     * @date:  2021/12/10
     */
    private void readProductions() {
        String[] productionStrings = fileUtil.read().split("\\|");
        for(String productionString: productionStrings) {
            String left = productionString.split("->")[0].trim();
            String[] right = productionString.split("->")[1].trim().split(" ");
            terminals.addAll(Arrays.asList(right)); // �������Ҳ��ַ������ս����
            nonTerminals.add(left);
            Production production = new Production(left, right);
            productions.add(production);
        }
        terminals.removeAll(nonTerminals); //�޳����з��ս��
        //System.out.println(productions.toString());
        System.out.println("�ս����  " + terminals.toString());
        System.out.println("���ս��: " + nonTerminals.toString());
        fileUtil.write("�ս����  " + terminals.toString());
        fileUtil.write("���ս��: " + nonTerminals.toString());
    }

    /*
     * @description: ��ȡFirst��
     * @param: []
     * @return: void
     * @date: 17:39 2021/12/10
     */
    private void getFirst() {
        // �ս��First��Ϊ����
        for(String terminal: terminals) {
            firsts.put(terminal, new HashSet<String>(){{add(terminal);}});
        }
        // ����ս��first����ֱ��û�п��Ը��µ�Ԫ��
        boolean flag = true;
        while (flag) {
            flag = false;
            for(Production production: productions) {
                String left = production.getLeft();
                String[] rights = production.getRight();
                for(String right: rights) {
                    // �����ǰΪe�������������򽫵�ǰ���м��뵽left��first���У����ҽ���ѭ��
                    if("e".equals(right)) continue;
                    for(String string: firsts.getOrDefault(right, new HashSet<>())) {
                        if(firsts.getOrDefault(left, new HashSet<>()).contains(string)) continue;
                        // �����µ�flag��Ϊtrue��֤���Ѹ��£�whileѭ������
                        flag = true;
                        Set<String> leftFirst = firsts.getOrDefault(left, new HashSet<>());
                        leftFirst.add(string);
                        firsts.put(left, leftFirst);
                    }
                    break;
                }
            }
        }
        System.out.println("First��:");
        fileUtil.write("First��:");
        Set<Map.Entry<String, Set<String>>> entries = firsts.entrySet();
        for (Map.Entry entry: entries) {
            // ��չʾ�ս����first��
            if(nonTerminals.contains(entry.getKey().toString())) {
                System.out.println("   " + entry.getKey() + ":  " + entry.getValue().toString());
                fileUtil.write("   " + entry.getKey() + ":  " + entry.getValue().toString());
            }
        }
    }

    /*
     * @description: ��ȡFollow��
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
                // ���ҷ�����ӽ�����#
                Set<String> lastFollow = follows.getOrDefault(rights[len - 1], new HashSet<>());
                lastFollow.add("#");
                // �ٸ��������ַ�Ϊ���ս�����������ʱ follow(B) += follow(A)��Ҫ�� A != B
                if(nonTerminals.contains(rights[len - 1])) {
                    for(String string: follows.getOrDefault(left, new HashSet<>())) {
                        // �����Ѱ���
                        if(lastFollow.contains(string)) continue;
                        //�����и��£�whileѭ������
                        flag = true;
                        lastFollow.add(string);
                    }
                }
                follows.put(rights[len - 1], lastFollow);

                // �ҳ� A -> aBb ����е�B
                for(int i = 0; i < rights.length - 1; ++i) {
                    if(!nonTerminals.contains(rights[i])) continue;
                    Set<String> bFollow = follows.getOrDefault(rights[i], new HashSet<>());
                    // �� b != e����follow(B) += First(b)
                    if(!"e".equals(rights[i + 1])) {
                        for(String string: firsts.getOrDefault(rights[i + 1], new HashSet<>())) {
                            // ȥ��first(b)�е�e���������Ѱ���
                            if("e".equals(string) || follows.getOrDefault(rights[i], new HashSet<>()).contains(string)) continue;
                            //�����и��£�whileѭ������
                            flag = true;
                            bFollow.add(string);
                        }
                    } else if(i == len - 2){
                        // b == e����follow(B) += follow(A)
                        for(String string: follows.getOrDefault(left, new HashSet<>())) {
                            // �����Ѱ���
                            if(follows.getOrDefault(rights[i], new HashSet<>()).contains(string)) continue;
                            //�����и��£�whileѭ������
                            flag = true;
                            bFollow.add(string);
                        }
                    }
                    follows.put(rights[i], bFollow);
                }
            }
        }
        System.out.println("Follow��:");
        fileUtil.write("Follow��:");
        Set<Map.Entry<String, Set<String>>> entries = follows.entrySet();
        for (Map.Entry entry: entries) {
            // ��չʾ�ս����first��
            if(nonTerminals.contains(entry.getKey().toString())) {
                System.out.println("   " + entry.getKey() + ":  " + entry.getValue().toString());
                fileUtil.write("   " + entry.getKey() + ":  " + entry.getValue().toString());
            }
        }
    }

    /*
     * @description: ����״̬ת���հ�
     * @param: []
     * @return: void
     * @date: 21:20 2021/12/10
     */
    private void getClosure() {
        // ��¼�հ�����״̬
        Map<Closure, Integer> state = new HashMap<>();
        Closure firstClosure = new Closure(grammar);
        Map<Production, Integer> originSet = new HashMap();
        // ����ʼ���Ų���ʽI0���뵽closure��
        firstClosure.insert(productions.get(0).getLeft(), productions.get(0).getRight(), 0);
        firstClosure.expand();
        closures.add(firstClosure);
        state.put(firstClosure, 0);

        // ʹ�õ�ǰ�հ�����ʣ��հ�
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        while(!queue.isEmpty()) {
            int index = queue.poll();
            // ����չ�հ�
            Closure oldClosure = closures.get(index);
            // ����ǰ�հ�û�п�����չ�ı��ʽ�������
            if(oldClosure.gotoMap.isEmpty()) continue;
            Set<Map.Entry<String, List<Production>>> entries = oldClosure.gotoMap.entrySet();
            for(Map.Entry entry: entries) {
                String key = entry.getKey().toString();
                if("e".equals(key)) continue; // ����ǰkeyΪe��������
                List<Production> values = oldClosure.gotoMap.get(key);
                Closure newClosure = new Closure(grammar);
                for(Production production: values) {
                    Production newProduction = new Production(production.getLeft(), production.getRight(), production.getPosition() + 1);
                    newClosure.insert(production.getLeft(), production.getRight(), production.getPosition() + 1);
                }
                //��鵱ǰ״̬�Ƿ��Ѵ���
                if(state.containsKey(newClosure)) {
                    // ����Ѵ��ڵ�ǰ״̬������±�
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
     * @description: ����SLR������
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
                // follow�����ϼ��ɹ�Լ
                Set<String> follow = follows.get(production.getLeft());
                for(String string: follow) {
                    if("S".equals(production.getLeft())) {
                        Map<String, Integer> current = table.getOrDefault(i, new HashMap<>());
                        current.put(string, Integer.MIN_VALUE); // ʹ����Сֵ����acc
                        table.put(i, current);
                    } else {
                        Map<String, Integer> current = table.getOrDefault(i, new HashMap<>());
                        // ʹ�ø�ֵ��¼����Լ���ʽ
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
     * @description: �����﷨����
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
            // �б����
            if(next == Integer.MIN_VALUE) {
                System.out.println("success!");
                return;
            } else if(next < 0) {
                // ��ʱ���յ�next�����ʽ���й�Լ
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
                // ��A��goto[ջ���� A]ѹ��ջ��
                input.push(production.getLeft());
                stack.push(table.get(stack.peek()).get(production.getLeft()));
                System.out.println("��Լ��: " + stack.toString() + "  " + input.toString());
            } else {
                stack.push(next);
                input.push(tokens.get(index));
                ++index;
                System.out.println("�ƽ���: " + stack.toString() + "  " + input.toString());
            }
        }
    }

    private void outputClosure() {
        // ����հ�
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
        // ����հ���ı�
        System.out.println("\n EDGES:");
        for(GoTo goTo: goToList) {
            System.out.println(goTo.toString());
        }
    }

    private void outputTable() {
        // ���SLR������
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
