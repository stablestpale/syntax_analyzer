package lexical_analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author zzy
 * @description: ��ʶ����
 * @date 2021/12/4 21:03
 */


class Type {
    private static final String[] keyword = {"main", "return",
            "for", "while", "do", "cin", "cout",
            "int", "double", "float", "char", "long"};
    private static final String[] operator = {"=", "+", "-", "*", "/", "<<", ">>"};
    private static final String[] delimiter = {"(", ")", "{", "}", ";"};

    /*
     * @description:  �ж��Ƿ�Ϊ�ؼ��֡���������ֽ�������ֺͱ���
     * @param: [word]
     * @return: boolean
     * @date: 18:46 2021/12/6
     */
    static boolean isLegal(String word) {
        if (Type.isKeyword(word)) {
            System.out.printf("%-10s �ؼ���", word);
        } else if (Type.isOperator(word)) {
            System.out.printf("%-10s �����", word);
        } else if (Type.isDelimiter(word)) {
            System.out.printf("%-10s �ֽ��", word);
        } else if (Type.isInteger(word)) {
            System.out.printf("%-10s   ����", word);
        } else if (Type.isVariable(word)) {
            System.out.printf("%-10s   ����", word);
        } else {
            System.out.printf("%-10s  ERROR", word);
            return false;
        }
        return true;
    }

    /*
     * @description: �ж��Ƿ�Ϊ�ؼ���
     * @param: [word]
     * @return: boolean
     * @date: 18:20 2021/12/6
     */
    static boolean isKeyword(String word) {
        Set<String> keywordSet = new HashSet<>(Arrays.asList(keyword));
        return keywordSet.contains(word);
    }

    /*
     * @description: �ж��Ƿ�Ϊ�����
     * @param: [word]
     * @return: boolean
     * @date: 18:21 2021/12/6
     */
    static boolean isOperator(String word) {
        Set<String> operatorSet = new HashSet<>(Arrays.asList(operator));
        return operatorSet.contains(word);
    }

    /*
     * @description: �ж��Ƿ�Ϊ�ֽ��
     * @param: [word]
     * @return: boolean
     * @date: 18:21 2021/12/6
     */
    static boolean isDelimiter(String word) {
        Set<String> delimiterSet = new HashSet<>(Arrays.asList(delimiter));
        return delimiterSet.contains(word);
    }

    /*
     * @description: �ж��Ƿ�Ϊ����
     * @param: [word]
     * @return: boolean
     * @date: 18:32 2021/12/6
     */
    static boolean isInteger(String word) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(word).matches();
    }

    /*
     * @description: �ж��Ƿ�Ϊ���������������ֿ�ͷ��ֻ�ܺ�����ĸ�����ֻ��»���
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
