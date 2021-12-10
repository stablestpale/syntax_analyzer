package syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zzy
 * @description: 产生式
 * @date 2021/12/9 19:10
 */


public class Production {
    private String left;
    private String[] right;
    int position;  // 用于闭包中标记点的位置

    Production(String left, String[] right) {
        this.left = left;
        this.right = right;
    }

    public Production(String left, String[] right, int position) {
        this.left = left;
        this.right = right;
        this.position = position;
    }

    public String getLeft() {
        return left;
    }

    public String[] getRight() {
        return right;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Production){
            return ((Production) obj).left.equals(left) && ((Production) obj).position == position && ((Production) obj).right.equals(right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return left.hashCode() + right.hashCode() + position;
    }

    @Override
    public String toString() {
        return "syntax.Production{" +
                "left='" + left + '\'' +
                ", right=" + Arrays.toString(right) +
                '}';
    }
}
