package com.example.InflearnJavaTest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.*;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

//@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) //클래스 단위에 이름 전략을 정할 수 있다
//@TestInstance(TestInstance.Lifecycle.PER_CLASS) //클래스 마다 인스턴스를 생성 //하나의 클래스에 인스턴스를 공유한다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //구현체를 정해 순서를 결정해 줄 수 있다/
@ExtendWith(FindSlowTestExtension.class) //선언적 등록방법 //이인스턴스를 확장 불가능(생성자를 주거나 할수 없다)
class StudyTest {

    @RegisterExtension
    static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(1000);


    @Test
    @DisplayName("스터디 만들기 \uD83D\uDE33") //공백, 이모지등 여러가지를 사용할 수가 있다.
    void create(){ //JUNIT5 부터는 public 제약이 사라졋다


        //매개변수의 위치는 정해져 있다(기대값, 실제 나오는값, 메시지) 이런식으로 위치에 맞게 사용해야 한다.

        Study study = new Study(10);
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
        /*
        assertAll( //한번에 모두 실행 해준다.
                ()-> assertNotNull(study),
                ()-> assertEquals(StudyStatus.DRAFT, study.getStatus(),
                        () -> "스터디를 처음 만들면 상태값이 "+ StudyStatus.DRAFT + "상태다"),
                ()-> assertTrue(study.getLimit() > 0 , "스터디 최대 참석 가능 인원은 0보다 커야 합니다.")
        );
        assertTrue(study.getLimit() > 0 , "스터디 최대 참석 가능 인원은 0보다 커야 합니다.");


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit는 0보다 커야합니다",exception.getMessage());

        assertTimeout(Duration.ofMillis(100), () ->{
            new Study(10);
            Thread.sleep(300);
        });
         */
        assertTimeoutPreemptively(Duration.ofMillis(400), () ->{
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

    //@Test
    //@Tag("fast") //tag 기능을 달아 줄수 있다.
    @FastTest
    void fast(){
        System.out.println("fast");
    }

    //@Test
    //@Tag("slow")
    @SlowTest
    void slow(){
        System.out.println("slow");
    }

    @DisplayName("스터디 만들기")
    @RepeatedTest(value = 10,name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeatTest(RepetitionInfo repetitionInfo){
        System.out.println("test : " + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions()); //현재 몇번참조,총 횟수등 정보를 얻을 수 있다
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message={0}") //JUnit5 에서는 기본으로 제공
    @ValueSource(strings = {"날씨가","많이","추워지고","있네요"}) //글자의 수를 가지고 테스트를 수행
    void ParameterizedTest(String message){
        System.out.println(message);
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message={0}") //JUnit5 에서는 기본으로 제공
    @ValueSource(strings = {"날씨가","많이","추워지고","있네요"}) //여러가지 자료형을 인자로 넘겨줄수 있다.
    //@EmptySource //빈어있는 테스트 가능
    //@NullSource //null이 있는 테스트
    @NullAndEmptySource //비어있고 null이 존쟂
    void ParameterizedTest2(String message){
        System.out.println(message);
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(ints = {10,20,40})
    void valueSourceTest(@ConvertWith(StudyConverter.class) Study study){
        System.out.println(study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter{
        //하나의 매개변수에 대한것이다.
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class,targetType,"Can Only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '자바'","20, 스프링"})
    void csvSourceTest(Integer limit,String name){
        System.out.println(new Study(limit,name));
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '자바'","20, 스프링"})
    void csvSourceTest2(ArgumentsAccessor argumentsAccessor){
        System.out.println(new Study(argumentsAccessor.getInteger(0)
                ,argumentsAccessor.getString(1)));

    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '자바'","20, 스프링"})
    void csvSourceTest3(@AggregateWith(StudyAggregator.class) Study study){
        System.out.println(study);
    }

    static class StudyAggregator implements ArgumentsAggregator{
        //반드시 static inner class이거나 public class여야 한다.
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            Study study = new Study(argumentsAccessor.getInteger(0)
                    ,argumentsAccessor.getString(1));
            return study;
        }
    }

    int value = 1;
    @BeforeAll //더이상 static 일 필요가 없다
    void beforeAll(){
        System.out.println("before all");
    }

    @AfterAll
    void afterAll(){
        System.out.println("after all");
    }

    /*
    @BeforeAll //private X, default O, return Type 존재 X
    static void beforeAll(){ //테스트를 실행할때 딱 1번만 실행된다.
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll(){
        System.out.println("after all");
    }
     */

    @Test
    @Order(2) //낮은 값일수록 더 높은 우선순위가 있다, //값이 같다면 자기가 알아서 정해진 값으로 실행
    void instance1(){
        //test 인스턴스는 테스트 갯수만큼 인스턴스가 생성된다.
        //왜 인스턴스를 계속 만드느냐? 테스트간의 의존성을 없애기 위해(테스트의 순서에 따라 동작시키는 것이 아니기때문)
        System.out.println(this);
        System.out.println(value++);
    }

    @Test
    @Order(1) 
    void instance2(){
        System.out.println(this);
        System.out.println(value++);
    }

    //테스트는 내부적으로 실행순서가 정해져 있지만 해당 순서에 의존하면 안된다.
    //어떤 순서대로 실행하거나, 테스트는 독립적으로 동작해야한다.

    @Test
    @Disabled
    public void disabledOnCondition(){
        System.out.println("create1");
    }

    @Test
    void extensionTest() throws InterruptedException {
        Thread.sleep(1100L);
    }



    @Test
    @Disabled //실행하고 싶지 않은 테스트에 표기한다.
    public void create_new_study_again(){
        System.out.println("create1");
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