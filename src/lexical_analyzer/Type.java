package lexical_analyzer;

/**
 * @author zzy
 * @description: ±êÊ¶·ûµÈ
 * @date 2021/12/4 21:03
 */


public class Type {
    public static final String[] keyword = {"main", "return",
            "for", "while", "do", "cin", "cout",
            "int", "double", "float", "char", "long"};
    public static final String[] operator = {"=", "+", "-", "*", "/", "<<", ">>"};
    public static final String[] delimiter = {"(", ")", "{", "}", ";"};
}
