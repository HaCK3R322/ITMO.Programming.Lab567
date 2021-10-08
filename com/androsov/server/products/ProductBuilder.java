package com.androsov.server.products;

import com.androsov.server.pojo.*;
import com.androsov.server.products.exceptions.ContentException;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides several methods that allows to validate and create {@link Product}.
 */
public class ProductBuilder {
    public static ProductBuilder instance;
    public static ProductBuilder getInstance() {
        if (instance == null)
            instance = new ProductBuilder();

        return instance;
    }

    private ProductBuilder() {
        idToAssign = 1;
        usedPartNumbers = new LinkedList<>();
    }

    private long idToAssign;
    public List<String> usedPartNumbers;

    public static class ProductImitator {
        public String name;
        public Coordinates coordinates;
        public Integer price;
        public String partNumber;
        public Float manufactureCost;
        public UnitOfMeasure unitOfMeasure;
        public Person owner;
    }

    public Product buildProduct(String name,
                                Coordinates coordinates,
                                Integer price,
                                String partNumber,
                                Float manufactureCost,
                                UnitOfMeasure unitOfMeasure,
                                Person owner) throws ContentException {
        return buildProduct(idToAssign, name, coordinates, LocalDateTime.now(), price, partNumber, usedPartNumbers, manufactureCost, unitOfMeasure, owner);
    }

    public Product buildProduct(long id,
                       String name,
                       Coordinates coordinates,
                       java.time.LocalDateTime creationDate,
                       Integer price,
                       String partNumber,
                       List<String> usedPartNumbers,
                       Float manufactureCost,
                       UnitOfMeasure unitOfMeasure,
                       Person owner) throws ContentException {

        ProductValidator.validateProduct(id, name, coordinates, creationDate, price, partNumber, usedPartNumbers, manufactureCost, unitOfMeasure, owner);

        idToAssign = id + 1;
        usedPartNumbers.add(partNumber);

        return new Product(id, name, coordinates, creationDate, price, partNumber, manufactureCost, unitOfMeasure, owner);
    }

    public Product buildProduct(ProductImitator productImitator, long id) throws ContentException {
        Product product;

        product = buildProduct(id,
                productImitator.name,
                productImitator.coordinates,
                LocalDateTime.now(),
                productImitator.price,
                productImitator.partNumber,
                usedPartNumbers,
                productImitator.manufactureCost,
                productImitator.unitOfMeasure,
                productImitator.owner);

        return product;
    }

    public Product buildProduct(ProductImitator productImitator) throws ContentException {
        Product product;

        product = buildProduct(productImitator, idToAssign);

        return product;
    }

    public Product buildProduct(String[] args) throws ContentException {
        ProductImitator productImitator = new ProductImitator();

        Person person;
        try {
            String name = args[7];
            long height = Long.parseLong(args[8]);
            Color eyeColor = Color.valueOf(args[9].toUpperCase());
            Color hairColor = Color.valueOf(args[10].toUpperCase());
            Country country = Country.valueOf(args[11].toUpperCase());

            person = new Person((int)idToAssign, name, height, eyeColor, hairColor, country);

            productImitator.name = args[0];
            productImitator.coordinates = new Coordinates(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
            productImitator.price = Integer.parseInt(args[3]);
            productImitator.partNumber = args[4];
            productImitator.manufactureCost = Float.parseFloat(args[5]);
            productImitator.unitOfMeasure = UnitOfMeasure.valueOf(args[6]);
            productImitator.owner = person;
        } catch (NullPointerException | IllegalArgumentException  e) {
            throw new ContentException(e.getMessage());
        }

        return buildProduct(productImitator);
    }

    public Product buildProduct(List<Object> args) throws ContentException {
        if(args.size() == 1) {
            return buildProduct((ProductImitator) args.get(0));
        } else {
            String[] strArgs = new String[args.size()];
            int index = 0;
            for (Object object : args) {
                strArgs[index] = object.toString();
                index++;
            }

            return buildProduct(strArgs);
        }
    }

    public void destroy(Product product) {
       try {
           usedPartNumbers.remove(product.getPartNumber());
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
}
