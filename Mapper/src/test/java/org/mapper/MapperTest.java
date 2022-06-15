package org.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapper.parser.Parser;
import org.test.CDAlbum;
import org.test.Student;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {
    Mapper mapper;
    Mapper mapper2;

    @BeforeEach
    void setUp() {
        mapper = new org.mapper.Mapper(true);
    }

    @Test
    void readFromString() {
        //Record type
        ArrayList<String> tracks = new ArrayList<String>();
        tracks.add("Unforgiven 2");
        tracks.add("Enter Sandman");
        tracks.add("Nothing Else matters");
        CDAlbum alb = new CDAlbum("Black Album", "Metallica",
                LocalDateTime.of(1991, 8, 12, 0, 0, 0),
                tracks);
        // Object Student
        Student st1 = new Student("Vasya", 'M', 99.5);
        st1.birthday = LocalDate.of(1979, 12, 11);
        List<Student> lst = new ArrayList<Student>();
        lst.add(st1);
        lst.add(st1);
        st1.name = "Petya";
        String s1, s2, s3;
        try {
            s1 = mapper.writeToString(st1);
            System.out.println(s1);
            s2 = mapper.writeToString(lst);
            System.out.println(s2);
            s3 = mapper.writeToString(alb);
            System.out.println(s3);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            Student st__1 = mapper.readFromString(Student.class, s1);
            List<Student> lsr__1 = mapper.readFromString(ArrayList.class, s2);
            if (lsr__1.get(0).equals(lsr__1.get(1))) {
                System.out.println("They match!\n");
            } else {
                System.out.println("They are clones but individuals!\n");
            }
            CDAlbum alb__1 = mapper.readFromString(CDAlbum.class, s3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // File serialize/unserialize
        try {
            File file = new File("file.txt");
            if (file.exists()) file.delete();
            mapper.write(lst, file);
            mapper.write(st1, file);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File("file.txt");
            List<Student> lsr__1 = mapper.read(ArrayList.class, file);
            Student st__1 = mapper.read(Student.class, file); // должен быть Null - file начался читаться с 0 !
            if (lsr__1.get(0).equals(lsr__1.get(1))) {
                System.out.println("They match!\n");
            } else {
                System.out.println("They are clones but individuals!\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File file = new File("file.txt");
            FileInputStream in = new FileInputStream("file.txt");
            List<Student> lsr__1 = mapper.read(ArrayList.class, in);
            if (lsr__1.get(0).equals(lsr__1.get(1))) {
                System.out.println("They match!\n");
            } else {
                System.out.println("They are clones but individuals!\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mapper = new org.mapper.Mapper(false);
        try {
            Student st__1 = mapper.readFromString(Student.class, s1);
            List<Student> lsr__1 = mapper.readFromString(ArrayList.class, s2);
            if (lsr__1.get(0).equals(lsr__1.get(1))) {
                System.out.println("They match!\n");
            } else {
                System.out.println("They are clones but individuals!\n");
            }
            CDAlbum alb__1 = mapper.readFromString(CDAlbum.class, s3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapper = new org.mapper.Mapper(false);

        try {
            String ss1 = mapper.readFromString(String.class, " 0 ");
            Student st__1 = mapper.readFromString(Student.class, " {org.test.Student \"name\"=0, \"пол\"=c\"М\", \"rating\"=\"12\"} ");
            List<Student> lsr__1 = mapper.readFromString(ArrayList.class, "0");
            List<Student> lsr__2 = mapper.readFromString(ArrayList.class, "[0, 0]");
            CDAlbum alb__1 = mapper.readFromString(CDAlbum.class, "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void read() {
        //Record type
        ArrayList<String> tracks = new ArrayList<String>();
        tracks.add("Unforgiven 2");
        tracks.add("Enter Sandman");
        tracks.add("Nothing Else matters");
        CDAlbum alb = new CDAlbum("Black Album", "Metallica",
                LocalDateTime.of(1991, 8, 12, 0, 0, 0),
                tracks);
        // Object Student
        Student st1 = new Student("Vasya", 'M', 99.5);
        st1.birthday = LocalDate.of(1979, 12, 11);
        List<Student> lst = new ArrayList<Student>();
        lst.add(st1);
        lst.add(st1);
        st1.name = "Petya";
        String s1, s2, s3;
        try {
            s1 = mapper.writeToString(st1);
            System.out.println(s1);
            s2 = mapper.writeToString(lst);
            System.out.println(s2);
            s3 = mapper.writeToString(alb);
            System.out.println(s3);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        mapper2 = new org.mapper.Mapper(false);
        try {
            Student st__1 = mapper2.readFromString(Student.class, s1);
            List<Student> lsr__1 = mapper2.readFromString(ArrayList.class, s2);
            if (lsr__1.get(0).equals(lsr__1.get(1))) {
                System.out.println("They match!\n");
            } else {
                System.out.println("They are clones but individuals!\n");
            }
            CDAlbum alb__1 = mapper2.readFromString(CDAlbum.class, s3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRead() throws IOException {
        Mapper mapper = new Mapper(false);
        Student st1 = new Student("Vasya", 'M', 99.5);
        //st1.birthday = LocalDate.of(1979, 12, 11);
        st1.name = "Petya";
        String s1 = mapper.writeToString(st1);
        System.out.println(s1);
    }

    @Test
    void writeToString() {
        //Record type
        ArrayList<String> tracks = new ArrayList<String>();
        tracks.add("Unforgiven 2");
        tracks.add("Enter Sandman");
        tracks.add("Nothing Else matters");
        CDAlbum alb = new CDAlbum("Black Album", "Metallica",
                LocalDateTime.of(1991, 8, 12, 0, 0, 0),
                tracks);
        // Object Student
        Student st1 = new Student("Vasya", 'M', 99.5);
        st1.birthday = LocalDate.of(1979, 12, 11);
        List<Student> lst = new ArrayList<Student>();
        lst.add(st1);
        lst.add(st1);
        st1.name = "Petya";
        String s1, s2, s3;
        try {
            s1 = mapper.writeToString(st1);
            System.out.println(s1);
            s2 = mapper.writeToString(lst);
            System.out.println(s2);
            s3 = mapper.writeToString(alb);
            System.out.println(s3);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Test
    void read1() {
        Mapper mm = new org.mapper.Mapper(true);
        Student st = new Student("Vasya", 'M', 99.5);
        try {
            System.out.println(mm.writeToString(st));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void write() {
    }

    @Test
    void testWrite() {
    }
}