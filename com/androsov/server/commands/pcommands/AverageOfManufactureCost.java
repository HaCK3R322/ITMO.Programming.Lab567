package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.Command;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.pojo.Product;

public class AverageOfManufactureCost extends CommandImpl implements Command {
    private final ProductDao<Product, String> dao;

    public AverageOfManufactureCost(ProductDao<Product, String> dao) {
        this.dao = dao;

        name = "average_of_manufacture_cost";
        description = "show average of manufacture cost.";
        argumentFormat = "void";
        userAccessible = true;
    }

    public Response execute(Request request) {
        Response response = new ResponseImpl(request.getUser());
        response.addData((float)dao.getList().stream()
                .map(Product::getManufactureCost)
                .mapToDouble(cost -> cost)
                .sum());
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}