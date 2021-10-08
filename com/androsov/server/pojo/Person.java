package com.androsov.server.pojo;

import com.androsov.server.products.exceptions.ContentException;

public class Person {
    public Person(int id, String name, long height, Color eyeColor, Color hairColor, Country nationality) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private final String name; //Поле не может быть null, Строка не может быть пустой
    private final long height; //Значение поля должно быть больше 0
    private final Color eyeColor; //Поле не может быть null
    private final Color hairColor; //Поле может быть null
    private final Country nationality; //Поле не может быть null

    public void CheckParams() throws ContentException {
        if(name == null || name.equals(""))
            throw new ContentException("Name cannot be null or empty.");
        if (height <= 0)
            throw new ContentException("Height cannot be zero.");
        if (eyeColor == null)
            throw new ContentException("Eye color cannot be null.");
        if (hairColor == null)
            throw new ContentException("Hair color cannot be null.");
        if (nationality == null)
            throw new ContentException("Nationality cannot be null.");
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return ("       name: " + name + "\n" +
                "       height: " + height + "\n" +
                "       eye color: " + eyeColor.toString() + "\n" +
                "       hair color: " + hairColor.toString() + "\n" +
                "       country: " + nationality);
    }

    public String getName() {
        return name;
    }

    public long getHeight() {
        return height;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Country getNationality() {
        return nationality;
    }
}
