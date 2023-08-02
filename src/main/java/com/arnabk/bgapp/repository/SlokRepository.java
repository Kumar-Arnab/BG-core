package com.arnabk.bgapp.repository;

import com.arnabk.bgapp.entity.Sloka;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlokRepository extends JpaRepository<Sloka, String> {

    @Query("""
            select s from Sloka s where s.chapterId like %:bgChId% order by s.verseNo
            """)
    List<Sloka> findByChapterIdOrderByVerseNo(String bgChId);

    Sloka findBySlokaId(String slokaId);

}
