package lexical;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author zzy
 * @description: 标识符等
 * @date 2021/12/4 21:03
 */


class Type {
    private static final String[] keyword = {"main", "return",
            "for", "while", "do", "cin", "cout", "if", "then", "else",
            "int", "double", "float", "char", "long"};
    private static final String[] operator = {"=", "+", "-", "*", "/", "<<", ">>", "==", ">", "<", ">=", "&&"};
    private static final String[] delimiter = {"(", ")", "{", "}", ";"};

    /*
     * @description:  判断是否为关键字、运算符、分界符、数字和变量
     * @param: [word]
     * @return: boolean
     * @date: 18:46 2021/12/6
     */
    static void isLegal(String word, List<String> tokens) {
        if (Type.isKeyword(word)) {
            System.out.printf("%-13s 关键字\n", word);
            tokens.add(word);
        } else if (Type.isOperator(word)) {
            System.out.printf("%-13s 运算符\n", word);
            tokens.add(word);
        } else if (Type.isDelimiter(word)) {
            System.out.printf("%-13s 分界符\n", word);
            tokens.add(word);
        } else if (Type.isInteger(word)) {
            System.out.printf("%-13s   数字\n", word);
            tokens.add("number");
        } else if (Type.isVariable(word)) {
            System.out.printf("%-13s   变量\n", word);
            tokens.add("id");
        } else {
            System.out.printf("%-13s  ERROR\n", word);
        }
    }

    /*
     * @description: 判断是否为关键字
     * @param: [word]
     * @return: boolean
     * @date: 18:20 2021/12/6
     */
    static boolean isKeyword(String word) {
        Set<String> keywordSet = new HashSet<>(Arrays.asList(keyword));
        return keywordSet.contains(word);
    }

    /*
     * @description: 判断是否为运算符
     * @param: [word]
     * @return: boolean
     * @date: 18:21 2021/12/6
     */
    static boolean isOperator(String word) {
        Set<String> operatorSet = new HashSet<>(Arrays.asList(operator));
        return operatorSet.contains(word);
    }

    /*
     * @description: 判断是否为分界符
     * @param: [word]
     * @return: boolean
     * @date: 18:21 2021/12/6
     */
    static boolean isDelimiter(String word) {
        Set<String> delimiterSet = new HashSet<>(Arrays.asList(delimiter));
        return delimiterSet.contains(word);
    }

    /*
     * @description: 判断是否为整数
     * @param: [word]
     * @return: boolean
     * @date: 18:32 2021/12/6
     */
    static boolean isInteger(String word) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(word).matches();
    }

    /*
     * @description: 判断是否为变量，不能以数字开头，只能含有字母、数字或下划线
     * @param: [word]
     * @return: boolean
     * @date: 18:34 2021/12/6
     */
    static boolean isVariable(String word) {
        if(Character.isDigit(word.charAt(0))) return false;
        for(int i = 1; i < word.length(); ++i) {
            if(!Character.isLetter(word.charAt(i)) && !Character.isDigit(word.charAt(i)) && word.charAt(i) != '_') {
                return false;
            }
        }
        return true;
    }
}
