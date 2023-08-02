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
@Table(name = "sloka")
public class Sloka {

    @Id
    @Column(name = "sloka_id")
    private String slokaId;

    @Column(name = "bg_ch_id")
    private String chapterId;

    @Column(name = "verse_no")
    private Integer verseNo;

    @Column(name = "slok_org")
    private String slokOriginal;

    @Column(name = "eng_transliteration")
    private String engTransliteration;

    @Column(name = "hnd_meaning")
    private String hindiMeaning;

    @Column(name = "eng_meaning")
    private String engMeaning;

    @Column(name = "wtwm")
    private String wordToWordMeaning;

}
