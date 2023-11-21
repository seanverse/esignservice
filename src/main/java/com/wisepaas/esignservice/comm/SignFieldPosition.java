package com.wisepaas.esignservice.comm;

import java.util.List;

public class SignFieldPosition {
    private String keyword;
    private boolean searchResult;

    private List<PosPoint> poslist;


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

    public List<PosPoint> getPoslist() {
        return poslist;
    }

    public void setPoslist(List<PosPoint> poslist) {
        this.poslist = poslist;
    }

    public static class PosPoint {
        private int page;
        private double x;
        private double y;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
        }
    }
}
