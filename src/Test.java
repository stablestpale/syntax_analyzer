import lexical_analyzer.Scanner;
import utils.FileUtil;

/**
 * @author zzy
 * @description:
 * @date 2021/12/7 18:35
 */



public class Test {



    public static void main(String[] args) {
        FileUtil fileUtil = new FileUtil("src/1.txt");
        Scanner scanner = new Scanner(fileUtil);
        scanner.analyze();
        System.out.println("tokens:  " + scanner.getTokens());

    }
}
