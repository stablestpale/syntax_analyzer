package syntax;

import java.util.*;

/**
 * @author zzy
 * @description: ����ת���հ�
 * @date 2021/12/10 21:52
 */


class Closure {
    private Grammar grammar;
    List<Production> originProductions;
    List<Production> allProductions;
    Map<String, List<Production>> gotoMap;

    Closure(Grammar grammar) {
        this.grammar = grammar;
        this.originProductions = new ArrayList<>();
        this.allProductions = new ArrayList<>();
        this.gotoMap = new HashMap<>();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Closure)
            return originProductions.equals(((Closure) obj).originProductions);
        return false;
    }

    @Override
    public int hashCode() {
        return  originProductions.hashCode();
    }

    /*
     * @description: ����ǰ�հ��ĵ�һ������ʽ�������
     * @param: [left, right, position]
     * @return: void
     * @date: 23:13 2021/12/10
     */
    void insert(String left, String[] right, int position) {
        Production production = new Production(left, right, position);
        originProductions.add(production);
        // �����ǰ����ʽΪ���һ������ʽ����ֱ�ӷ��أ�����ȴ�����
        if(position > right.length - 1) return;
        String current = right[position];
        List<Production> currents = gotoMap.getOrDefault(current, new ArrayList<>());
        currents.add(production);
        gotoMap.put(current, currents);
    }

    /*
     * @description: ���䵱ǰ�հ�
     * @param: []
     * @return: void
     * @date: 23:17 2021/12/10
     */
    void expand() {
        // ��¼����չ���ײ�
        Queue<String> queue = new LinkedList<>();
        Set<String> usedPre = new HashSet<>();
        Set<Production> productionSet = new HashSet<>();
        for(Production production: originProductions) {
            allProductions.add(production);
            productionSet.add(production);
            // ����ʽ�ַ�����ֹ��
            if(production.getPosition() < production.getRight().length) {
                String pre = production.getRight()[production.getPosition()];
                if(grammar.getNonTerminals().contains(pre) && !usedPre.contains(pre)) {
                    usedPre.add(pre);
                    queue.add(pre);
                }
            }
        }
        while(!queue.isEmpty()) {
            String pre = queue.poll();
            for(Production production: grammar.getProductions()) {
                if(!production.getLeft().equals(pre)) continue;
                String[] rights = production.getRight();
                for(int i = 0; i < rights.length; ++i) {
                    Production current = new Production(pre, rights, i);
                    if(grammar.getNonTerminals().contains(rights[i]) && !usedPre.contains(rights[i])) {
                        queue.add(rights[i]);
                        usedPre.add(rights[i]);
                    }
                    if(!productionSet.contains(production)) {
                        productionSet.add(production);
                        allProductions.add(production);
                    }
                    List<Production> currents = gotoMap.getOrDefault(rights[i], new ArrayList<>());
                    currents.add(current);
                    gotoMap.put(rights[i], currents);
                }

/*                if(grammar.getNonTerminals().contains(rights[0]) && !usedPre.contains(rights[0])) {
                    queue.add(rights[0]);
                    usedPre.add(rights[0]);
                }
                if(!productionSet.contains(production)) {
                    productionSet.add(production);
                    allProductions.add(production);
                }
                List<Production> currents = gotoMap.getOrDefault(rights[0], new ArrayList<>());
                currents.add(current);
                gotoMap.put(rights[0], currents);*/

            }
        }
    }
}
