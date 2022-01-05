package com.example.InflearnJavaTest.service;

import com.example.InflearnJavaTest.domain.Member;
import com.example.InflearnJavaTest.domain.Study;

import java.util.Optional;

public interface MemberService {
    Optional<Member> findById(Long memberId);
    void validation(Long id);
    void notify(Study study);
    void notify(Member member);
}
