package com.example.InflearnJavaTest.service;

import com.example.InflearnJavaTest.Repository.StudyRepository;
import com.example.InflearnJavaTest.domain.Member;
import com.example.InflearnJavaTest.domain.Study;
import com.example.InflearnJavaTest.domain.StudyStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//@Mock Anotation을 처리해줄 확장팩필요
class StudyServiceTest {

    //@Mock MemberService memberService;

    //@Mock StudyRepository studyRepository;



    @Test
    void createStudyService(
            @Mock MemberService memberService //이렇게 파라미터로 받아 메소드 내에서 생성할 수도 있다.
            , @Mock StudyRepository studyRepository){
        //이렇게 만드는 일을 Mockito가 한다.
        /*
        MemberService memberService = new MemberService() {
            @Override
            public Optional<Member> findById(Long memberId) {
                return Optional.empty();
            }
        };
        */
        //MemberService memberService = mock(MemberService.class);
        //StudyRepository studyRepository = mock(StudyRepository.class);
        StudyService studyService = new StudyService(memberService,studyRepository);
        //repository 와, memberService가  어떻게 동작할지 가정을 해 주어야된다.
        assertNotNull(studyService);
    }

    @Test
    void stubbing(
            @Mock MemberService memberService
            , @Mock StudyRepository studyRepository){
        StudyService studyService = new StudyService(memberService,studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("KKK");

        //Optional<Member> member = memberService.findById(1L); //비어있는 값이 나온다.
        //memberService.validation(2L); //아무런 동작도 하지 않는다.

        //1이라는 값으로 findById(1L)을 하게 된다면 Member를 반환한다.
        //stubbing
        //파라미터를 ArgumentMatcher를 활용하면 더욱 다양하게 사용할 수가 있다.
        //리턴 타입이 있는 메소드
        //when(memberService.findById(2L)).thenReturn(Optional.of(member));
        when(memberService.findById(any())).thenReturn(Optional.of(member));
        Study study = new Study(10,"java");

        assertEquals("KKK",memberService.findById(1L).get().getEmail());
        assertEquals("KKK",memberService.findById(2L).get().getEmail());
        
        //when(memberService.findById(1L)).thenThrow(new RuntimeException()); //예외를 던지도록 할수도 있다.
        doThrow(new IllegalArgumentException()).when(memberService).validation(1L);
        assertThrows(IllegalArgumentException.class , ()->{
            memberService.validation(1L);
        });
        memberService.validation(2L);

        when(memberService.findById(any()))
                .thenReturn(Optional.of(member)) //처음 호출
                .thenThrow(new RuntimeException()) //두번째 호출
                .thenReturn(Optional.empty()); //3번쨰 호출
        Optional<Member> byId = memberService.findById(1L);
        assertEquals("KKK",byId.get().getEmail());

        assertThrows(RuntimeException.class , ()->{
            memberService.findById(2L);
        });
        assertEquals(Optional.empty(), memberService.findById(3L));
    }

    @Test
    void test(@Mock MemberService memberService, @Mock StudyRepository studyRepository){
        StudyService studyService = new StudyService(memberService,studyRepository);
        Study study = new Study(10, "테스트");
        Member member = new Member(1L,"KKK");
        // TODO memberService 객체에 findById 메소드를 1L 값으로 호출하면 Optional.of(member) 객체를 리턴하도록 Stubbing
        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        // TODO studyRepository 객체에 save 메소드를 study 객체로 호출하면 study 객체 그대로 리턴하도록 Stubbing
        when(studyRepository.save(isA(Study.class))).thenReturn(study);
        studyService.createNewStudy(1L, study);
        assertNotNull(study.getOwner());
        assertEquals(member, study.getOwner());
    }

    @Test
    void verification(@Mock MemberService memberService, @Mock StudyRepository studyRepository){
        StudyService studyService = new StudyService(memberService,studyRepository);
        Study study = new Study(10, "테스트");
        Member member = new Member(1L,"KKK");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(isA(Study.class))).thenReturn(study);

        studyService.createNewStudy(1L, study);

        assertNotNull(study.getOwner());
        //Notify가 몇번 호출 된지 확인 가능
        //해당 함수를 몇번 호출 되는지
        verify(memberService, times(1)).notify(study);

        verify(memberService, times(1)).notify(member);
        verify(memberService, never()).validation(any());


        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(study); //순서대로 적어주면 된다.
        inOrder.verify(memberService).notify(member);

        verifyNoMoreInteractions(memberService);
        //verifyNoMoreInteractions(memberService); //어떤한 interactions 도 일어나면 안된다.

        //TimeOut 과 같은 시간 제약을 걸수도 있다.
    }
    @Test
    void BDD(@Mock MemberService memberService, @Mock StudyRepository studyRepository){
        //GIVEN
        StudyService studyService = new StudyService(memberService,studyRepository);
        Study study = new Study(10, "테스트");
        Member member = new Member(1L,"KKK");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(isA(Study.class))).thenReturn(study);
        //Given에 해당하는데 when이 써있다(given으로 변경 가능)
        given(memberService.findById(1L)).willReturn(Optional.of(member));
        given(studyRepository.save(study)).willReturn(study);

        //WHEN
        studyService.createNewStudy(1L, study);

        //THEN
        assertNotNull(study.getOwner());
        //verify(memberService, times(1)).notify(member);
        //verifyNoMoreInteractions(memberService);
        then(memberService).should(times(1)).notify(study);
        then(memberService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
    void test2(@Mock MemberService memberService, @Mock StudyRepository studyRepository){
        //Given
        StudyService studyService = new StudyService(memberService,studyRepository);
        Study study = new Study(10,"더 자바, 테스트");
        assertNull(study.getOpenedDataTime());
        // TODO studyRepository Mock 객체의 save 메소드를호출 시 study를 리턴하도록 만들기.
        given(studyRepository.save(study)).willReturn(study);

        // When
        studyService.openStudy(study);

        // Then
        // TODO study의 status가 OPENED로 변경됐는지 확인
        assertEquals(study.getStatus(), StudyStatus.OPENED);
        // TODO study의 openedDataTime이 null이 아닌지 확인
        assertNotNull(study.getOpenedDataTime());
        // TODO memberService의 notify(study)가 호출 됐는지 확인.
        then(memberService).should(times(1)).notify(study);

    }
}