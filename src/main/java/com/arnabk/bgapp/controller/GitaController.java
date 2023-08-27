package com.arnabk.bgapp.controller;

import com.arnabk.bgapp.entity.Chapter;
import com.arnabk.bgapp.entity.Sloka;
import com.arnabk.bgapp.service.GitaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
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
    public ResponseEntity<?> listAllChapters() {
        List<Chapter> chapters = gitaService.listAllChapters();
        if (!CollectionUtils.isEmpty(chapters)) {
            return ResponseEntity.ok(chapters);
        }
        return ResponseEntity.internalServerError().body("Error while fetching chapter list");
    }

    @GetMapping("/chapter")
    public ResponseEntity<?> findChapter(@RequestParam("ch_id") String chapterId) {
        Chapter chapter = gitaService.getChapterById(chapterId);
        if (chapter != null) {
            return ResponseEntity.ok(chapter);
        }
        return ResponseEntity.internalServerError().body("Error while fetching chapter with chapter id " + chapterId);
    }

    @GetMapping("/sloka")
    public ResponseEntity<?> listSlokaByChapterId(@RequestParam("ch_id") String chapterId) {
        if (!chapterId.isEmpty() && !chapterId.isBlank()) {
            List<Sloka> slokas = gitaService.listSlokaByChapterId(chapterId);
            if (!CollectionUtils.isEmpty(slokas)) {
                return ResponseEntity.ok(slokas);
            }
        }
        return ResponseEntity.internalServerError().body("Error fetching slokas list for chapter id " + chapterId);
    }

    @GetMapping("/slok")
    public ResponseEntity<?> findSlokById(@RequestParam("slok_id") String slokId) {
        Sloka sloka = gitaService.findSlokById(slokId);
        if (sloka != null) {
            return ResponseEntity.ok(sloka);
        }
        return ResponseEntity.internalServerError().body("Error fetching slok for id " + slokId);
    }
}
