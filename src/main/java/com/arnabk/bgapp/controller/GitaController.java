package com.arnabk.bgapp.controller;

import com.arnabk.bgapp.entity.Chapter;
import com.arnabk.bgapp.entity.Sloka;
import com.arnabk.bgapp.service.GitaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bg/api")
@RequiredArgsConstructor
@Slf4j
public class GitaController {

    @Autowired
    private final GitaService gitaService;

    @GetMapping("/chapters")
    public ResponseEntity<List<Chapter>> listAllChapters() {
        return ResponseEntity.ok(gitaService.listAllChapters());
    }

    @GetMapping("/chapter")
    public ResponseEntity<Chapter> findChapter(@RequestParam("ch_id") String chapterId) {
        return ResponseEntity.ok(gitaService.getChapterById(chapterId));
    }

    @GetMapping("/sloka")
    public ResponseEntity<List<Sloka>> listSlokaByChapterId(@RequestParam("ch_id") String chapterId) {
        return ResponseEntity.ok(gitaService.listSlokaByChapterId(chapterId));
    }

    @GetMapping("/slok")
    public ResponseEntity<Sloka> findSlokById(@RequestParam("slok_id") String slokId) {
        return ResponseEntity.ok(gitaService.findSlokById(slokId));
    }
}
