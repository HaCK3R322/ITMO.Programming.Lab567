package com.androsov.server.pojo;

import java.time.LocalDateTime;

public class Product implements Comparable<Product> {
    public Product(long id,
                    String name,
                    Coordinates coordinates,
                    java.time.LocalDateTime creationDate,
                    Integer price,
                    String partNumber,
                    Float manufactureCost,
                    UnitOfMeasure unitOfMeasure,
                    Person owner) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.partNumber = partNumber;
        this.manufactureCost = manufactureCost;
        this.unitOfMeasure = unitOfMeasure;
        this.owner = owner;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id; //g/Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private final String name; //f/Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates; //f/Поле не может быть null
    private final java.time.LocalDateTime creationDate; //g/Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private final Integer price; //f/Поле может быть null, Значение поля должно быть больше 0
    private final String partNumber; //f/Строка не может быть пустой, Значение этого поля должно быть уникальным, Поле не может быть null
    private final Float manufactureCost; //f/Поле может быть null
    private final UnitOfMeasure unitOfMeasure; //f/Поле не может быть null
    private final Person owner; //f + g/Поле может быть null

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Integer getPrice() {
        return price;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public Float getManufactureCost() {
        return manufactureCost;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Person getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return ("product id: " + id + "\n" +
                "   name: " + name + "\n" +
                "   coordinates: " + coordinates.getX() + " , " + coordinates.getY() + "\n" +
                "   creation date: " + creationDate.toString() + "\n" +
                "   price: " + price + "\n" +
                "   part number: " + partNumber + "\n" +
                "   manufacture cost: " + manufactureCost + "\n" +
                "   unit of measure: " + unitOfMeasure + "\n" +
                "   owner info: " + "\n" +
                owner.toString()
        );
    }

    @Override
    public int compareTo(Product p) {
        return name.compareTo(p.getName()); // сортировка имени
    }
}
