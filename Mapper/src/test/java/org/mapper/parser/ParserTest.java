package org.mapper.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.test.CDAlbum;
import org.test.Student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
Parser parser;
    @BeforeEach
    void setUp() {
       parser = new Parser();
    }

    @Test
    void read() {
        String str="[";
        str+="{\"a\"=\"1\",\"b\"=[\"123\",\"456\"]},";
        str+="[\"1\",\"2\"],";
        str+="\"abc\"]";
        try {
            DataElement el = parser.read(str);
            System.out.println(el);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRead() {
        ArrayList<String> tracks=new ArrayList<String>();
        tracks.add("Unforgiven 2");
        tracks.add("Enter Sandman");
        tracks.add("Nothing Else matters");
        CDAlbum alb = new CDAlbum("Black Album", "Metallica",
                LocalDateTime.of(1991,8,12,0,0,0),
                tracks);
    }

    @Test
    void testRead1() {
        String s1="Maria";
        List<String> strlist=new ArrayList<String>();
        strlist.add(s1);
        strlist.add(s1);
        s1+=" Ivanovna";
        System.out.println(s1);
        System.out.println(strlist.get(0)+","+strlist.get(1));

    }

    @Test
    void testRead2() {

    }

    @Test
    void unescape() {
    }
}