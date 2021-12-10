package syntax;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zzy
 * @description: 记录终结符、非终结符等重复项，以便closure等中使用
 * @date 2021/12/10 22:10
 */


class Grammar {
    private Set<String> terminals;
    private Set<String> nonTerminals;
    private Map<String, Set<String>> firsts;
    private Map<String, Set<String>> follows;
    private List<Production> productions;

    public Grammar(Set<String> terminals, Set<String> nonTerminals, Map<String, Set<String>> firsts, Map<String, Set<String>> follows, List<Production> productions) {
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.firsts = firsts;
        this.follows = follows;
        this.productions = productions;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public void setTerminals(Set<String> terminals) {
        this.terminals = terminals;
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public void setNonTerminals(Set<String> nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public Map<String, Set<String>> getFirsts() {
        return firsts;
    }

    public void setFirsts(Map<String, Set<String>> firsts) {
        this.firsts = firsts;
    }

    public Map<String, Set<String>> getFollows() {
        return follows;
    }

    public void setFollows(Map<String, Set<String>> follows) {
        this.follows = follows;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public void setProductions(List<Production> productions) {
        this.productions = productions;
    }
}
