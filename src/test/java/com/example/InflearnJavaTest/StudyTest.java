package com.example.InflearnJavaTest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) //클래스 단위에 이름 전략을 정할 수 있다
class StudyTest {

    @Test
    @DisplayName("스터디 만들기 \uD83D\uDE33") //공백, 이모지등 여러가지를 사용할 수가 있다.
    void create(){ //JUNIT5 부터는 public 제약이 사라졋다


        //매개변수의 위치는 정해져 있다(기대값, 실제 나오는값, 메시지) 이런식으로 위치에 맞게 사용해야 한다.

        Study study = new Study(-10);
        //assertNotNull(study);
        /*
        assertEquals(StudyStatus.DRAFT, study.getStatus(), new Supplier<String>() {
            @Override
            public String get() {
                return "스터디를 처음 만들면 상태값이 "+ StudyStatus.DRAFT +" 상태다";
            }
        });
        //차이점 : 3번쨰 파라미터에 문자열 연산이 복잡할 경우 해당 방법은 테스트가 성공하든 실패하든 문자열 연산을 해주어야 되기 때문에 성능에 차이가 있다
        assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 "+ StudyStatus.DRAFT + "상태다");
        */

        assertAll( //한번에 모두 실행 해준다.
                ()-> assertNotNull(study),
                ()-> assertEquals(StudyStatus.DRAFT, study.getStatus(),
                        () -> "스터디를 처음 만들면 상태값이 "+ StudyStatus.DRAFT + "상태다"),
                ()-> assertTrue(study.getLimit() > 0 , "스터디 최대 참석 가능 인원은 0보다 커야 합니다.")
        );
        assertTrue(study.getLimit() > 0 , "스터디 최대 참석 가능 인원은 0보다 커야 합니다.");


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit는 0보다 커야합니다",exception.getMessage());
        /*
        assertTimeout(Duration.ofMillis(100), () ->{
            new Study(10);
            Thread.sleep(300);
        });
         */
        assertTimeoutPreemptively(Duration.ofMillis(100), () ->{
            //쓰레드와 관련 없는 코드를 동작시킬때 사용해라
            //300 millis까지 기달리지 않고, 100 millis 가 지나면 바로 종료
            new Study(10);
            Thread.sleep(300);
        });
    }

    @Test
    @EnabledOnOs({OS.MAC,OS.LINUX})
    @EnabledOnJre({JRE.JAVA_8,JRE.JAVA_9}) //java버젼에 맞는 테스트도 진행할 수도있다.
    @EnabledIfEnvironmentVariable(named = "TEST_ENV",matches = "LOCAL")
    void create2(){
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
        assumeTrue("LOCAL".equalsIgnoreCase(test_env));

        Study actual = new Study(10);
        assertThat(actual.getLimit()).isGreaterThan(0);

        assumingThat("LOCAL".equalsIgnoreCase(test_env),()->{ //해당 조건이 맞을 떄 다음 코드 실행해준다.

        });

    }


    @Test
    @Disabled //실행하고 싶지 않은 테스트에 표기한다.
    public void create_new_study_again(){
        System.out.println("create1");
    }

    @BeforeAll //private X, default O, return Type 존재 X
    static void beforeAll(){ //테스트를 실행할때 딱 1번만 실행된다.
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll(){
        System.out.println("after all");
    }

    @BeforeEach
    void beforeEach(){ //테스트를 실행 할 떄마다 실행된다.
        System.out.println("BeforeEach each");
    }

    @AfterEach
    void afterEach(){
        System.out.println("After each");
    }
}