import lexical.Scanner;
import syntax.Parser;
import syntax.Production;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zzy
 * @description: ≤‚ ‘¿‡
 * @date 2021/12/7 18:35
 */



public class Test {



    public static void main(String[] args) {
        Scanner scanner = new Scanner("src/utils/input.txt");
        scanner.analyze();
        System.out.println("tokens:  " + scanner.getTokens());
        Parser parser = new Parser("src/utils/CFG.txt", scanner.getTokens());
        parser.analyze();

/*        Set<Production> productionSet = new HashSet<>();
        Production productionA = new Production("e", new String[]{"A", "B"}, 0);
        Production productionB = new Production("e", new String[]{"A", "B"}, 0);
        System.out.println(productionA.equals(productionB));
        System.out.println(productionA.hashCode());
        System.out.println(productionB.hashCode());
        System.out.println(productionA.hashCode() == productionB.hashCode());
        productionSet.add(productionA);
        System.out.println(productionSet.contains(productionB));*/

    }
}
