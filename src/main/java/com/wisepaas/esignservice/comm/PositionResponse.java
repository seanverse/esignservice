package com.wisepaas.esignservice.comm;

import java.util.List;

public class PositionResponse extends ESignResponse<PositionResponse.RespData> {
    public static class RespData {
        private List<KeywordPosition> keywordPositions;

        // getters and setters

        public List<KeywordPosition> getKeywordPositions() {
            return keywordPositions;
        }

        public void setKeywordPositions(List<KeywordPosition> keywordPositions) {
            this.keywordPositions = keywordPositions;
        }
    }

    public static class KeywordPosition {
        private String keyword;
        private boolean searchResult;
        private List<Position> positions;

        // getters and setters

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

        public List<Position> getPositions() {
            return positions;
        }

        public void setPositions(List<Position> positions) {
            this.positions = positions;
        }
    }

    public static class Position {
        private int pageNum;
        private List<Coordinate> coordinates;

        // getters and setters

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public List<Coordinate> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Coordinate> coordinates) {
            this.coordinates = coordinates;
        }
    }

    public static class Coordinate {
        private double positionX;
        private double positionY;

        // getters and setters

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
    }
}
