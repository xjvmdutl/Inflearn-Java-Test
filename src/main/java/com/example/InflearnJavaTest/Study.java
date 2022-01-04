package com.example.InflearnJavaTest;

public class Study {
    private StudyStatus status = StudyStatus.DRAFT;

    private int limit;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Study(int limit, String name) {
        this.limit = limit;
        this.name = name;
    }

    public Study(int limit) {
        if(limit < 0)
            throw  new IllegalArgumentException("limit는 0보다 커야합니다");
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public StudyStatus getStatus() {
        return status;
    }

    public void setStatus(StudyStatus status) {
        this.status = status;
    }
}
