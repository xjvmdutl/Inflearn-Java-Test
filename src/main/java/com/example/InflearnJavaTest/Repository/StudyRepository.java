package com.example.InflearnJavaTest.Repository;

import com.example.InflearnJavaTest.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study,Long> {

}
