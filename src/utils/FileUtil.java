package utils;

import java.io.*;

/**
 * @author zzy
 * @description: �ļ��������
 * @date 2021/12/4 20:48
 */


public class FileUtil {
    private BufferedReader reader;
    private BufferedWriter writer;
    private String split;

    /*
     * @description: ��ʼ��������
     * @param: [path]
     * @return: null
     * @date: 20:54 2021/12/4
     */
    public FileUtil(String path, String split) {
        try {
            this.split = split;
            this.reader = new BufferedReader(new FileReader(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * @description: �ر��ļ�
     * @param: []
     * @return: void
     * @date: 20:59 2021/12/4
     */
    public void finish() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
