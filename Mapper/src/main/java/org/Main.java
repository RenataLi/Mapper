package org;

import org.mapper.parser.DataElement;
import org.mapper.parser.Parser;
import org.test.CDAlbum;
import org.test.Student;
import ru.hse.homework4.Mapper;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Parser p = new Parser();
        String str="[";
        str+="{\"a\"=\"1\",\"b\"=[\"123\",\"456\"]},";
        str+="[\"1\",\"2\"],";
        str+="\"abc\"]";

        try {
            DataElement el = p.read(str);
            System.out.println(el);
        } catch (Exception e) {
            e.printStackTrace();
        }



        ArrayList<String> tracks=new ArrayList<String>();
        tracks.add("Unforgiven 2");
        tracks.add("Enter Sandman");
        tracks.add("Nothing Else matters");
        CDAlbum alb = new CDAlbum("Black Album", "Metallica",
                LocalDateTime.of(1991,8,12,0,0,0),
                tracks);

        Student st1=new Student("Vasya",'M',99.5);
        //st1.birthday= LocalDate.of(1979,12,11);
        List<Student> lst=new ArrayList<Student>();
        lst.add(st1);
        lst.add(st1);
        st1.name="Petya";
        String s1,s2,s3;

        Mapper mm=new org.mapper.Mapper(true);
        try {
            s1=mm.writeToString(st1);
            System.out.println(s1);
            s2=mm.writeToString(lst);
            System.out.println(s2);
            s3=mm.writeToString(alb);
            System.out.println(s3);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            Student st__1=mm.readFromString(Student.class,s1);
            List<Student> lsr__1=mm.readFromString( ArrayList.class, s2);
            if (lsr__1.get(0).equals(lsr__1.get(1))) {
                System.out.println("They match!\n");
            } else {
                System.out.println("They are clones but individuals!\n");
            }
            CDAlbum alb__1 = mm.readFromString(CDAlbum.class, s3);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            File file = new File("file.txt");
            if (file.exists()) file.delete();
            mm.write(lst,file);
            mm.write(st1,file);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File("file.txt");
            List<Student> lsr__1=mm.read(ArrayList.class,file);
            Student st__1=mm.read(Student.class,file); // должен быть Null - file начался читаться с 0 !
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
            List<Student> lsr__1=mm.read(ArrayList.class,in);
            if (lsr__1.get(0).equals(lsr__1.get(1))) {
                System.out.println("They match!\n");
            } else {
                System.out.println("They are clones but individuals!\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mm=new org.mapper.Mapper(false);
        try {
            Student st__1=mm.readFromString(Student.class,s1);
            List<Student> lsr__1=mm.readFromString( ArrayList.class, s2);
            if (lsr__1.get(0).equals(lsr__1.get(1))) {
                System.out.println("They match!\n");
            } else {
                System.out.println("They are clones but individuals!\n");
            }
            CDAlbum alb__1 = mm.readFromString(CDAlbum.class, s3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mm=new org.mapper.Mapper(false);

        try {
            String ss1=mm.readFromString(String.class," 0 ");
            Student st__1=mm.readFromString(Student.class," {org.test.Student \"name\"=0, \"пол\"=c\"М\", \"rating\"=\"12\"} ");
            List<Student> lsr__1=mm.readFromString( ArrayList.class, "0");
            List<Student> lsr__2=mm.readFromString( ArrayList.class, "[0, 0]");
            CDAlbum alb__1 = mm.readFromString(CDAlbum.class, "0");
            System.out.println("1");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
