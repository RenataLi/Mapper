package org.test;

import org.mapper.DateFormat;
import org.mapper.Exported;
import org.mapper.Ignored;
import org.mapper.PropertyName;

import java.time.LocalDate;

@Exported
public class Student {
    public String name;

    @DateFormat("d LLLL y GG")
    public LocalDate birthday;

    @PropertyName("пол")
    public char sex;

    //@Ignored
    public double rating;
    public Student() {

    }
    public Student(String name, char sex, double rating) {
        this.name=new String(name);
        //this.birthday=birthday;
        this.sex=sex;
        this.rating=rating;
    }
}
