package com.example.InflearnJavaTest.service;

import com.example.InflearnJavaTest.Repository.StudyRepository;
import com.example.InflearnJavaTest.domain.Member;
import com.example.InflearnJavaTest.domain.Study;
import com.example.InflearnJavaTest.domain.StudyStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Testcontainers //도커 테스트용 컨테이너
@Slf4j
class StudyServiceTest2 {

    @Mock MemberService memberService;

    @Autowired
    StudyRepository studyRepository;
    
    //여러 필드에서 공유 가능
    /*
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
       .withDatabaseName("studytest");
     */
    //로컬에 해당 이미지가 있으면 가지고 오고 없으면 원격에 해당 이미지가 있다면 다운받아 가지고 온다.
    @Container
    static GenericContainer postgreSQLContainer = new GenericContainer("postgres")
            .withExposedPorts(5432) //도커와 연결된 호스트 포트를 확인할 수 있다.
            .withEnv("POSTGRES_DB","studytest")
            //.waitingFor(Wait.forListeningPort()) //대기 시간을 알려줄 수 있다
            ;
    @BeforeAll
    public static void beforeAll(){
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log); //로그 보기
        postgreSQLContainer.followOutput(logConsumer);
    }

    /*
    @BeforeAll
    static void beforeAll(){
       postgreSQLContainer.start();
        System.out.println(postgreSQLContainer.getJdbcUrl());
    }

    @AfterAll
    static void afterAll(){
        postgreSQLContainer.stop();
    }
    */
    @BeforeEach
    void beforeEach(){
        System.out.println(postgreSQLContainer.getMappedPort(5432));
        //System.out.println(postgreSQLContainer.getLogs());
        //container를 static으로 하지 않고 실행하면 너무 느리기 때문에 BeforeEach로 데이터를 지우는 식으로 테스트하는것이 더 빠르다.
        studyRepository.deleteAll();
    }

    @Test
    void createStudyService(){
        StudyService studyService = new StudyService(memberService,studyRepository);
        assertNotNull(studyService);
    }

    @Test
    void stubbing(){
        StudyService studyService = new StudyService(memberService,studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("KKK");


        when(memberService.findById(any())).thenReturn(Optional.of(member));
        Study study = new Study(10,"java");

        assertEquals("KKK",memberService.findById(1L).get().getEmail());
        assertEquals("KKK",memberService.findById(2L).get().getEmail());
        
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
        verify(memberService, times(1)).notify(study);

        verify(memberService, times(1)).notify(member);
        verify(memberService, never()).validation(any());


        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(study); //순서대로 적어주면 된다.
        inOrder.verify(memberService).notify(member);

        verifyNoMoreInteractions(memberService);
    }
    @Test
    void BDD(@Mock MemberService memberService, @Mock StudyRepository studyRepository){
        //GIVEN
        StudyService studyService = new StudyService(memberService,studyRepository);
        Study study = new Study(10, "테스트");
        Member member = new Member(1L,"KKK");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(isA(Study.class))).thenReturn(study);
        given(memberService.findById(1L)).willReturn(Optional.of(member));
        given(studyRepository.save(study)).willReturn(study);

        //WHEN
        studyService.createNewStudy(1L, study);

        //THEN
        assertNotNull(study.getOwner());
        then(memberService).should(times(1)).notify(study);
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