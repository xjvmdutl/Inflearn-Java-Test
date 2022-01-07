package com.example.InflearnJavaTest.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Study {

    //속성에도 예약어가 있다면 생성 안된다.

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private StudyStatus status = StudyStatus.DRAFT;

    private int limits;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
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

    public Study(int limits, String name) {
        this.limits = limits;
        this.name = name;
    }

    public Study(int limits) {
        if(limits < 0)
            throw  new IllegalArgumentException("limit는 0보다 커야합니다");
        this.limits = limits;
    }

    protected Study() {
    }

    public int getLimit() {
        return limits;
    }

    public void setLimit(int limits) {
        this.limits = limits;
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
