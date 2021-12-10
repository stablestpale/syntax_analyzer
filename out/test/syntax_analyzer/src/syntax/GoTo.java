package syntax;

/**
 * @author zzy
 * @description: ×´Ì¬×ª»»±í
 * @date 2021/12/10 21:59
 */


class GoTo {
    private Integer startIndex;
    private String token;
    private Integer endIndex;

    GoTo(Integer startIndex, String token, Integer endIndex) {
        this.startIndex = startIndex;
        this.token = token;
        this.endIndex = endIndex;
    }

    @Override
    public String toString() {
        return  "from " + startIndex +
                " to " + endIndex +
                ",  " + token;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public String getToken() {
        return token;
    }

    public Integer getEndIndex() {
        return endIndex;
    }
}
