package cn.tsign.hz.comm;

public class SignFieldPosition {
    private String keyword;
    private boolean searchResult;

    private int positionPage;
    private double positionX;
    private double positionY;

    // getters and setters

    public int getPositionPage() {
        return positionPage;
    }

    public void setPositionPage(int positionPage) {
        this.positionPage = positionPage;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isSearchResult() {
        return searchResult;
    }

    public void setSearchResult(boolean searchResult) {
        this.searchResult = searchResult;
    }
}
