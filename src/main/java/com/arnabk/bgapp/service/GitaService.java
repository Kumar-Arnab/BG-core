package com.arnabk.bgapp.service;

import com.arnabk.bgapp.entity.Chapter;
import com.arnabk.bgapp.entity.Sloka;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GitaService {
    
    List<Chapter> listAllChapters();

    Chapter getChapterById(String chapterId);

    List<Sloka> listSlokaByChapterId(String chapterId);

    Sloka findSlokById(String slokId);
}
