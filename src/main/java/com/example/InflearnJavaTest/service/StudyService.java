package com.example.InflearnJavaTest.service;

import com.example.InflearnJavaTest.Repository.StudyRepository;
import com.example.InflearnJavaTest.domain.Member;
import com.example.InflearnJavaTest.domain.Study;

import java.util.Optional;


public class StudyService {
    private final MemberService memberService;

    private final StudyRepository studyRepository;

    public StudyService(MemberService memberService, StudyRepository studyRepository){
        assert memberService != null;
        assert studyRepository != null;
        this.memberService = memberService;
        this.studyRepository = studyRepository;
    }

    public Study createNewStudy(Long memberId,Study study){
        Optional<Member> member = memberService.findById(memberId);
        study.setOwner(member.orElseThrow(
                () -> new IllegalArgumentException("Member doesn't exist for id: '"+memberId + "'")
        ));
        Study newStudy = studyRepository.save(study);
        //만약 순차적으로 호출할 경우?
        memberService.notify(newStudy);
        memberService.notify(member.get());
        return newStudy;
    }

    public Study openStudy(Study study) {
        study.open();
        Study openedStudy = studyRepository.save(study);
        memberService.notify(openedStudy);
        return openedStudy;
    }
}
