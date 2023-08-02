package com.arnabk.bgapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chapters")
public class Chapter {

    @Id
    @Column(name = "chapter_number")
    private Integer chapterNumber;

    @Column(name = "bg_ch_id")
    private String chapterId;

    @Column(name = "no_of_verses")
    private Integer noOfVerses;

    @Column(name = "hnd_name")
    private String hindiName;

    @Column(name = "eng_translation")
    private String engTranslation;

    @Column(name = "eng_transliteration")
    private String engTransliteration;

    @Column(name = "hnd_meaning")
    private String hindiMeaning;

    @Column(name = "eng_meaning")
    private String engMeaning;

    @Column(name = "eng_summary")
    private String engSummary;

    @Column(name = "hnd_summary")
    private String hindiSummary;

}
