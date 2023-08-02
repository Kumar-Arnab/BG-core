package com.arnabk.bgapp.repository;

import com.arnabk.bgapp.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    @Query("""
            select c from Chapter c order by c.chapterNumber
            """)
    List<Chapter> findByChapterNumberOrderByChapterNumber();

    @Query("""
            select c from Chapter c where c.chapterId = :chId
            """)
    Chapter findByChapterId(String chId);

}
