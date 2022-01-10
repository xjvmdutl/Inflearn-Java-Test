package com.example.InflearnJavaTest.controller;

import com.example.InflearnJavaTest.Repository.StudyRepository;
import com.example.InflearnJavaTest.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StudyController {
    final StudyRepository studyRepository;

    @GetMapping("/study/{id}")
    public Study getStudy(@PathVariable Long id){
        return studyRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Study not found for '" + id + "'"));
    }

    @PostMapping("/study")
    public Study createStudy(@RequestBody Study study){
        return studyRepository.save(study);
    }
}
