import lexical_analyzer.Scanner;

/**
 * @author zzy
 * @description:
 * @date 2021/12/7 18:35
 */



public class Test {



    public static void main(String[] args) {
        Scanner scanner = new Scanner("src/utils/1.txt");
        scanner.analyze();
        System.out.println("tokens:  " + scanner.getTokens());
    }
}
