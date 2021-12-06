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


}
