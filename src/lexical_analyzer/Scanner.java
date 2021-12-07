package lexical_analyzer;

import utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzy
 * @description: 词法分析器
 * @date 2021/12/4 21:03
 */


public class Scanner {
    private FileUtil fileUtil;
    private StringBuffer buffer;

    private List<String> tokens;

    public Scanner(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
        this.buffer = fileUtil.read();
        this.tokens = new ArrayList<>();
    }

    /*
     * @description: 返回生成的token
     * @param: []
     * @return: List<String>
     * @date: 20:08 2021/12/5
     */
    public List<String> getTokens() {
        return tokens;
    }

    /*
     * @description: 词法分析
     * @param: []
     * @return: void
     * @date: 18:05 2021/12/6
     */
    public void analyze() {
        System.out.println("******词法分析******");
        //为做简化，设标识符以空格分隔，仅判断标识符是否合法，不再对标识符进行分隔
        String bufferString = buffer.toString();
        String[] words = bufferString.split(" ");
        for(String word: words) {
            //判断是否为关键字、运算符、分界符、数字和变量
            Type.isLegal(word, tokens);
        }
        System.out.println("******词法分析******");
    }

}
