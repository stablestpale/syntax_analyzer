package syntax;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zzy
 * @description: 生成转换闭包
 * @date 2021/12/10 21:52
 */


class Closure {
    private Grammar grammar;
    List<Production> originProductions;
    List<Production> allProductions;
    Map<String, List<Production>> gotoMap;
    private Set<Production> productionSet;

    private Closure() {
    }

    Closure(Grammar grammar) {
        this.grammar = grammar;
        this.originProductions = new ArrayList<>();
        this.allProductions = new ArrayList<>();
        this.gotoMap = new HashMap<>();
        this.productionSet = new HashSet<>();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Closure){
            return originProductions.equals(((Closure) obj).originProductions);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(originProductions.toArray());
    }

    /*
     * @description: 将当前闭包的第一个产生式加入进来
     * @param: [left, right, position]
     * @return: void
     * @date: 23:13 2021/12/10
     */
    void insert(String left, String[] right, int position) {
        Production production = new Production(left, right, position);
        if(productionSet.contains(production)) return;
        originProductions.add(production);
        allProductions.add(production);
        // 如果当前产生式为最后一个产生式，则直接返回，否则等待扩充
        if(position >= right.length) return;
        String current = right[position];
        List<Production> currents = gotoMap.getOrDefault(current, new ArrayList<>());
        currents.add(production);
        gotoMap.put(current, currents);
        grammar.usedProduction.add(production);
    }

    /*
     * @description: 扩充当前闭包
     * @param: []
     * @return: void
     * @date: 23:17 2021/12/10
     */
    void expand() {
        // 记录待扩展的首部
        Queue<String> queue = new LinkedList<>();
        Set<String> usedPre = new HashSet<>();
        for(Production production: originProductions) {
            // 产生式字符非终止符
            if(production.getPosition() < production.getRight().length) {
                String pre = production.getRight()[production.getPosition()];
                if(grammar.getNonTerminals().contains(pre) && !usedPre.contains(pre)) {
                    usedPre.add(pre);
                    queue.add(pre);
                }
            }
        }
        System.out.println(originProductions.toString() + " " + usedPre.toString());
        while(!queue.isEmpty()) {
            String pre = queue.poll();
            for(Production production: grammar.getProductions()) {
                if(!production.getLeft().equals(pre)) continue;
                String[] rights = production.getRight();

                // 如果首字符为非终结符且没有扩展过
                if(grammar.getNonTerminals().contains(rights[0]) && !usedPre.contains(rights[0])) {
                    queue.add(rights[0]);
                    usedPre.add(rights[0]);
                }
                if(productionSet.contains(production)) continue;
                productionSet.add(production);
                allProductions.add(production);
                Production current = new Production(pre, rights, 0);
                List<Production> currents = gotoMap.getOrDefault(rights[0], new ArrayList<>());
                currents.add(current);
                gotoMap.put(rights[0], currents);

/*                for(int i = 0; i < rights.length; ++i) {
                    Production current = new Production(pre, rights, i);
                    // 如果当前为非终结符且没有扩展过
                    if(grammar.getNonTerminals().contains(rights[i]) && !usedPre.contains(rights[i])) {
                        queue.add(rights[i]);
                        usedPre.add(rights[i]);
                    }
                    if(productionSet.contains(production)) continue;
                    productionSet.add(production);
                    allProductions.add(production);
                    grammar.usedProduction.add(current);
*//*                    if(!productionSet.contains(production)&&!grammar.usedProduction.contains(production)) {
                        productionSet.add(production);
                        allProductions.add(production);
                        grammar.usedProduction.add(current);
                    }*//*
                    List<Production> currents = gotoMap.getOrDefault(rights[i], new ArrayList<>());
                    currents.add(current);
                    gotoMap.put(rights[i], currents);
                }*/
            }
        }
        System.out.println(originProductions.toString() + " " + usedPre.toString() + "\n");
    }


    public static void main(String[] args) {
        Closure a = new Closure();
        Production productionA = new Production("e", new String[]{"A", "B"}, 0);
        Production productionB = new Production("f", new String[]{"A", "B"}, 0);
        a.originProductions = new ArrayList<>();
        a.originProductions.add(productionA);
        a.originProductions.add(productionB);
        Closure b = new Closure();
        b.originProductions = new ArrayList<>();
        b.originProductions.add(productionB);
        b.originProductions.add(productionA);
        Set<Closure> set = new HashSet<>();
        set.add(a);
        set.add(b);
        System.out.println(set.size());
    }
}
