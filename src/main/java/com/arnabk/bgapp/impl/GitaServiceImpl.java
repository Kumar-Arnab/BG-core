package com.arnabk.bgapp.impl;

import com.arnabk.bgapp.entity.Chapter;
import com.arnabk.bgapp.entity.Sloka;
import com.arnabk.bgapp.repository.ChapterRepository;
import com.arnabk.bgapp.repository.SlokRepository;
import com.arnabk.bgapp.service.GitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GitaServiceImpl implements GitaService {

    @Autowired
    private final ChapterRepository chapterRepository;

    @Autowired
    private final SlokRepository slokRepository;

    @Override
    public List<Chapter> listAllChapters() {
        return chapterRepository.findByChapterNumberOrderByChapterNumber();
    }

    @Override
    public Chapter getChapterById(String chapterId) {
        return chapterRepository.findByChapterId(chapterId);
    }

    @Override
    public List<Sloka> listSlokaByChapterId(String chapterId) {
        return slokRepository.findByChapterIdOrderByVerseNo(chapterId);
    }

    @Override
    public Sloka findSlokById(String slokId) {
        return slokRepository.findBySlokaId(slokId);
    }

}
