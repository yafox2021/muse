package org.yafox.muse.dto;

import java.util.List;

public class Book {

    private String name;

    private Person author;

    private List<Student> readers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public List<Student> getReaders() {
        return readers;
    }

    public void setReaders(List<Student> readers) {
        this.readers = readers;
    }

}
