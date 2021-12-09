import java.util.ArrayList;
import java.util.List;

/**
 * @author zzy
 * @description: ²úÉúÊ½
 * @date 2021/12/9 19:10
 */


public class Production {
    private String left;
    private String[] right;
    private List<String> select;

    public Production(String left, String[] right) {
        this.left = left;
        this.right = right;
        this.select = new ArrayList<>();
    }

    public String getLeft() {
        return left;
    }

    public String[] getRight() {
        return right;
    }
}
