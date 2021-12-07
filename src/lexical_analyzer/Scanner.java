package lexical_analyzer;

import utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzy
 * @description: �ʷ�������
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
     * @description: �������ɵ�token
     * @param: []
     * @return: List<String>
     * @date: 20:08 2021/12/5
     */
    public List<String> getTokens() {
        return tokens;
    }

    /*
     * @description: �ʷ�����
     * @param: []
     * @return: void
     * @date: 18:05 2021/12/6
     */
    public void analyze() {
        System.out.println("******�ʷ�����******");
        //Ϊ���򻯣����ʶ���Կո�ָ������жϱ�ʶ���Ƿ�Ϸ������ٶԱ�ʶ�����зָ�
        String bufferString = buffer.toString();
        String[] words = bufferString.split(" ");
        for(String word: words) {
            //�ж��Ƿ�Ϊ�ؼ��֡���������ֽ�������ֺͱ���
            Type.isLegal(word, tokens);
        }
        System.out.println("******�ʷ�����******");
    }

}
