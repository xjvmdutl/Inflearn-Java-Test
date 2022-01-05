package com.example.InflearnJavaTest.domain;

import java.time.LocalDateTime;

public class Study {
    private StudyStatus status = StudyStatus.DRAFT;

    private int limit;

    private String name;

    private Member owner;

    private LocalDateTime openedDataTime;

    public LocalDateTime getOpenedDataTime() {
        return openedDataTime;
    }

    public void setOpenedDataTime(LocalDateTime openedDataTime) {
        this.openedDataTime = openedDataTime;
    }

    public Member getOwner() {
        return owner;
    }

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

    public void setOwner(Member owner) {
        this.owner = owner;
    }

    public void open() {
        this.status=StudyStatus.OPENED;
        this.openedDataTime = LocalDateTime.now();
    }
}
