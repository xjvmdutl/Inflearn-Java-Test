package com.example.InflearnJavaTest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;


@AnalyzeClasses(packagesOf = InflearnJavaTestApplication.class)//읽어들일 클래스
public class ArchTests {

    @ArchTest
    ArchRule controllerClassRule = classes().that().haveSimpleNameEndingWith("Controller")
            .should().accessClassesThat().haveSimpleNameEndingWith("Service")
            .orShould().accessClassesThat().haveSimpleNameEndingWith("Repository");

    @ArchTest
    ArchRule domainPackageRule = classes().that().resideInAPackage("..domain..")
            .should().onlyBeAccessed().byClassesThat() //접근이 가능한데
            .resideInAnyPackage("..study..","..member..","..domain..");

    @ArchTest
    ArchRule memberPackageRule = noClasses().that().resideInAPackage("..domain..")
            .should().accessClassesThat().resideInAPackage("..member..");

    @ArchTest
    ArchRule studyPackageRule = noClasses().that().resideOutsideOfPackage("..study..")
            .should().accessClassesThat().resideInAnyPackage("..study..");


    @ArchTest
    ArchRule freeOfCycle = slices().matching("..InflearnJavaTest.(*)..")
            .should().beFreeOfCycles();

    @Test
    public void packageDependencyTests(){
        JavaClasses classes = new ClassFileImporter().importPackages("com.example.InflearnJavaTest");
        /**
         * domain.. 패키지에 있는 클래스는 ..study.., ..member.., ..domain에서 참조 가능.
         * ..member.. 패키지에 있는 클래스는 ..study..와 ..member..에서만 참조 가능.
         *  (반대로) ..domain.. 패키지는 ..member.. 패키지를 참조하지 못한다.
         * ..study.. 패키지에 있는 클래스는 ..study.. 에서만 참조 가능.
         * 순환 참조 없어야 한다.
         */
        /*
        ArchRule domainPackageRule = classes().that().resideInAPackage("..domain..") //도메인 클래스 밑에 있는 클래스는
                .should().onlyBeAccessed().byClassesThat() //접근이 가능한데
                .resideInAnyPackage("..study..","..member..","..domain.."); //해당 클래스에서만
        domainPackageRule.check(classes);


        ArchRule memberPackageRule = noClasses().that().resideInAPackage("..domain..") //도메인 안에 모든 클래스도
        .should().accessClassesThat().resideInAPackage("..member..");
        memberPackageRule.check(classes);

        ArchRule studyPackageRule = noClasses().that().resideOutsideOfPackage("..study..") //스터디 이외 패키지에서는
                .should().accessClassesThat().resideInAnyPackage("..study.."); //스터디 패키지에 접근할수 없다.
        studyPackageRule.check(classes);

        ArchRule freeOfCycle = slices().matching("..InflearnJavaTest.(*)..") //순환 참조 확인
                .should().beFreeOfCycles();
        freeOfCycle.check(classes);
         */
    }
}
