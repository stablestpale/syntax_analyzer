import lexical_analyzer.Scanner;

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
        Parser parser = new Parser("src/utils/CFG.txt");
        parser.analyze();
    }
}
