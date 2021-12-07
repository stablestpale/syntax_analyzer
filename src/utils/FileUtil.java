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
    private StringBuffer buffer;

    /*
     * @description: ��ʼ��������
     * @param: [path]
     * @return: null
     * @date: 20:54 2021/12/4
     */
    public FileUtil(String path) {
        try {
            reader = new BufferedReader(new FileReader(path));
            buffer = new StringBuffer();
            File file = new File("src/output.txt");
            if(!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * @description: �����ļ���������
     * @param: []
     * @return: StringBuffer
     * @date: 20:56 2021/12/4
     */
    public StringBuffer read() {
        String current = "";
        try {
            while((current = reader.readLine()) != null) {
                buffer.append(current);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /*
     * @description: д���ַ���
     * @param: [current]
     * @return: void
     * @date: 20:57 2021/12/4
     */
    public void write(String current) {
        try {
            writer.write(current);
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
