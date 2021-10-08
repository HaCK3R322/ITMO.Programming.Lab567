package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.pojo.Product;
import com.androsov.server.products.ProductBuilder;
import com.androsov.server.products.exceptions.ContentException;
import com.androsov.server.products.exceptions.ManagementException;

import java.util.List;

public class Add extends CommandImpl {
    private final ProductDao<Product, String>  dao;
    private final ProductBuilder productBuilder;

    public Add(ProductDao<Product, String> dao, ProductBuilder productBuilder) {
        this.dao = dao;
        this.productBuilder = productBuilder;

        name = "add";
        description = "product creation from arguments: <name><coordinate x><coordinate y><price><part number><manufacture cost><unit of measure><owner name><owner height><owner eye color><owner hair color><nationality>.";
        argumentFormat = "String";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        Response response = new ResponseImpl(request.getUser());

        StringBuilder stringBuilder = new StringBuilder();

        try {
            final Product product = productBuilder.buildProduct(args);
            dao.add(product, request.getUser().getNickname());

            stringBuilder.append("Product ");
            stringBuilder.append(product.getName());
            stringBuilder.append(" was added!");
        } catch (ContentException | ManagementException e) {
            stringBuilder.append(e.getMessage());
        }

        response.setMessage(stringBuilder.toString());
        return response;
    }

    @Override
    public String getDescription() {
        //TODO сделать по аналогии во всех командах
        return description;
    }

//    public ProductBuilder.ProductImitator createProductManually(ProductBuilder productBuilder, ServerIOOLD io) throws IOException, ContentException {
//        ProductBuilder.ProductImitator product = new ProductBuilder.ProductImitator();
//
//        boolean statementNotPicked = true;
//
//        String productName = null;
//        io.sendResponse( messenger.Add().Creation_of_new_product + "\n----------\n" + messenger.Add().enter_name);
//
//        while(statementNotPicked) {
//            productName = io.getCommandLine();
//            if(productName.replaceAll("\\s", "").equals("") || productName == null)
//                io.sendResponse(messenger.Add().Name_can_not_be_empty_try_again);
//            else
//                statementNotPicked = false;
//        }
//
//        double productX = 0; Double productY = null;
//        statementNotPicked = true;
//        io.sendResponse(messenger.Add().Enter_coordinate_x);
//        while(statementNotPicked) {
//            try {
//                productX = Double.parseDouble(io.getCommandLine());
//                if(productX > 653)
//                    io.sendResponse(messenger.Add().X_coordinate_can_not_be_bigger_than_653_Try_again);
//                else
//                    statementNotPicked = false;
//            } catch (NumberFormatException e) {
//                io.sendResponse(messenger.Add().Please_enter_double_format_number);
//            }
//        }
//
//        io.sendResponse(messenger.Add().Enter_coordinate_y);
//        statementNotPicked = true;
//        while(statementNotPicked) {
//            try {
//                productY = Double.parseDouble(io.getCommandLine());
//                if(productY == null)
//                    io.sendResponse(messenger.Add().y_is_NULL);
//                else
//                    statementNotPicked = false;
//            } catch (NumberFormatException e) {
//                io.sendResponse(messenger.Add().Please_enter_double_format_number);
//            }
//        }
//
//        Integer productPrice = null;
//        io.sendResponse(messenger.Add().Enter_price);
//        statementNotPicked = true;
//        while(statementNotPicked) {
//            try {
//                productPrice = Integer.parseInt(io.getCommandLine());
//                if(productPrice == 0)
//                    io.sendResponse(messenger.Add().Price_can_not_be_0_Try_again);
//                else
//                    statementNotPicked = false;
//            } catch (NumberFormatException e) {
//                io.sendResponse(messenger.Add().Please_enter_int_format_number);
//            }
//        }
//
//        String productPartNumber = null;
//        List<String> usedPartNumbers = productBuilder.usedPartNumbers;
//        io.sendResponse(messenger.Add().Enter_part_number);
//        statementNotPicked = true;
//        while(statementNotPicked) {
//            productPartNumber = io.getCommandLine();
//            boolean partNumberAlreadyUsed = false;
//            for (String usedPartNumber : usedPartNumbers) {
//                if (productPartNumber.equals(usedPartNumber)) {
//                    partNumberAlreadyUsed = true;
//                    break;
//                }
//            }
//            if(productPartNumber.replaceAll("\\s", "").equals("") || productPartNumber == null || partNumberAlreadyUsed)
//                io.sendResponse(messenger.Add().Part_number_is_empty_or_already_used_Try_again);
//            else
//                statementNotPicked = false;
//        }
//
//
//        Float productManufactureCost = null;
//        statementNotPicked = true;
//        io.sendResponse(messenger.Add().Enter_manufacture_cost);
//        while(statementNotPicked) {
//            try {
//                productManufactureCost = Float.parseFloat(io.getCommandLine());
//                if(productManufactureCost == null)
//                    io.sendResponse(messenger.Add().Manufacture_cost_can_not_be_NULL_Try_again);
//                else
//                    statementNotPicked = false;
//            } catch (NumberFormatException e) {
//                io.sendResponse(messenger.Add().Please_enter_float_format_number);
//            }
//        }
//
//        UnitOfMeasure productUnitOfMeasure = null;
//        io.sendResponse(messenger.Add().Enter_unit_of_measure);
//        statementNotPicked = true;
//        while(statementNotPicked) {
//            try {
//                productUnitOfMeasure = UnitOfMeasure.valueOf(io.getCommandLine().toUpperCase());
//                statementNotPicked = false;
//            } catch (IllegalArgumentException e) {
//                io.sendResponse(messenger.Add().That_type_of_measure_doesnt_supports_Supported_units_GRAMS_KILOGRAMS_SQUARE_METERS_Try_again);
//            }
//        }
//
//        String ownerName = null;
//        long ownerHeight = 0;
//        Color ownerEyeColor = null;
//        Color ownerHairColor = null;
//        Country ownerNationality = null;
//
//        io.sendResponse(messenger.Add().Enter_owners_name);
//        statementNotPicked = true;
//        while(statementNotPicked) {
//            ownerName = io.getCommandLine();
//            if(ownerName == null || ownerName.replaceAll("\\s", "").equals("")) {
//                io.sendResponse(messenger.Add().Owners_name_can_not_be_empty_Try_again);
//            } else {
//                statementNotPicked = false;
//            }
//        }
//        io.sendResponse(messenger.Add().Enter_owners_height);
//        statementNotPicked = true;
//        while(statementNotPicked) {
//            try {
//                ownerHeight = Long.parseLong(io.getCommandLine());
//                if(ownerHeight <= 0) {
//                    io.sendResponse(messenger.Add().Owners_height_cant_be_zero_or_negative_Try_again);
//                } else {
//                    statementNotPicked = false;
//                }
//            } catch (NumberFormatException e) {
//                io.sendResponse(messenger.Add().Please_enter_long_format_number);
//            }
//        }
//        io.sendResponse(messenger.Add().Enter_owner_eye_color);
//        statementNotPicked = true;
//        while(statementNotPicked) {
//            try {
//                ownerEyeColor = Color.valueOf(io.getCommandLine().toUpperCase());
//                statementNotPicked = false;
//            } catch (IllegalArgumentException e) {
//                io.sendResponse(messenger.Add().That_type_of_color_doesnt_supports_Supported_colors_BLUE_GREEN_BLACK_ORANGE_WHITE_BROWN_Try_again);
//            }
//        }
//        io.sendResponse(messenger.Add().Enter_owner_hair_color);
//        statementNotPicked = true;
//        while(statementNotPicked) {
//            try {
//                ownerHairColor = Color.valueOf(io.getCommandLine().toUpperCase());
//                statementNotPicked = false;
//            } catch (IllegalArgumentException e) {
//                io.sendResponse(messenger.Add().That_type_of_color_doesnt_supports_Supported_colors_BLUE_GREEN_BLACK_ORANGE_WHITE_BROWN_Try_again);
//            }
//        }
//        io.sendResponse(messenger.Add().Enter_owner_nationality);
//        statementNotPicked = true;
//        while(statementNotPicked) {
//            try {
//                ownerNationality = Country.valueOf(io.getCommandLine().toUpperCase());
//                statementNotPicked = false;
//            } catch (IllegalArgumentException e) {
//                io.sendResponse(messenger.Add().That_type_of_nationality_doesnt_supports_Supported_nationality_GERMANY_THAILAND_JAPAN_Try_again);
//            }
//        }
//
//        product.name = productName;
//        product.coordinates = new Coordinates(productX, productY);
//        product.price = productPrice;
//        product.partNumber = productPartNumber;
//        product.manufactureCost = productManufactureCost;
//        product.unitOfMeasure = productUnitOfMeasure;
//        product.owner = new Person(ownerName, ownerHeight, ownerEyeColor, ownerHairColor, ownerNationality);
//
//        return product;
//    }
}