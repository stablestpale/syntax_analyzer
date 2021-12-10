import utils.FileUtil;

import java.io.StringWriter;
import java.util.*;

/**
 * @author zzy
 * @description: �﷨����
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
        System.out.println("\n\n******�﷨����******");
        readProductions();
        getFirst();
        getFollow();
        System.out.println("******�﷨����******");
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
        Set<Map.Entry<String, Set<String>>> entries = firsts.entrySet();
        for (Map.Entry entry: entries) {
            // ��չʾ�ս����first��
            if(nonTerminals.contains(entry.getKey().toString())) System.out.println("   " + entry.getKey() + ":  " + entry.getValue().toString());
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
        Set<Map.Entry<String, Set<String>>> entries = follows.entrySet();
        for (Map.Entry entry: entries) {
            // ��չʾ�ս����first��
            if(nonTerminals.contains(entry.getKey().toString())) System.out.println("   " + entry.getKey() + ":  " + entry.getValue().toString());
        }
    }

}
