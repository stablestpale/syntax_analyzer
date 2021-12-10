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

    public Parser(String filePath) {
        this.fileUtil = new FileUtil(filePath, "|");
        this.productions = new ArrayList<>();
        this.terminals = new HashSet<>();
        this.nonTerminals = new HashSet<>();
        this.firsts = new HashMap<>();
        this.follows = new HashMap<>();
        this.closures = new ArrayList<>();
        this.grammar = new Grammar(terminals, nonTerminals, firsts, follows, productions);
        this.goToList = new ArrayList<>();
    }

    public void analyze() {
        System.out.println("\n\n******�﷨����******");
        fileUtil.write("\n\n******�﷨����******");
        readProductions();
        getFirst();
        getFollow();
        getClosure();
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
                // ���ҷ������ӽ�����#
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
            // ����ǰ�հ�û�п�����չ�ı���ʽ�������
            if(oldClosure.gotoMap.isEmpty()) continue;
            Set<Map.Entry<String, List<Production>>> entries = oldClosure.gotoMap.entrySet();
            for(Map.Entry entry: entries) {
                String key = entry.getKey().toString();
                if("e".equals(key)) continue; // ����ǰkeyΪe��������
                List<Production> values = oldClosure.gotoMap.get(key);
                Closure newClosure = new Closure(grammar);
                for(Production production: values) {
                    newClosure.insert(production.getLeft(), production.getRight(), production.getPosition() + 1);
                }
                //��鵱ǰ״̬�Ƿ��Ѵ���
                if(state.containsKey(newClosure)) {
                    // ����Ѵ��ڵ�ǰ״̬������±�
                    goToList.add(new GoTo(index, key, state.get(newClosure)));
                } else {
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

    private void outputClosure() {
        // ����հ�
        System.out.println("\n TABLE:");
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

}