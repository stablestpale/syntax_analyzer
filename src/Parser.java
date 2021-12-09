import utils.FileUtil;

import java.util.List;
import java.util.Map;

/**
 * @author zzy
 * @description: 语法分析相关
 * @date 2021/12/4 21:00
 */


public class Parser {
    private FileUtil fileUtil;
    private List<Production> productions;
    private List<String> terminals;
    private List<String> nonTerminals;
    private Map<String, List<String>> firsts;
    private Map<String, List<String>> follows;

    public Parser(String filePath) {
        this.fileUtil = new FileUtil(filePath, "*");

    }

    


}
