package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.pojo.Product;

import java.util.List;

public class Show extends CommandImpl {
    
    private final ProductDao<Product, String> dao;

    public Show(ProductDao<Product, String> dao) {
        this.dao =dao;
        

        name = "show";
        description = "gives info about each product.";
        argumentFormat = "void";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        final Response response = new ResponseImpl(request.getUser());
        StringBuilder result = new StringBuilder();

        if(dao.getList().size() > 0) {
            for(int i = 0; i < dao.getList().size(); i++) {
                result.append(dao.getList().get(i).toString());
                if(i != dao.getList().size() - 1)
                    result.append("\n");
            }
        } else {
            result = new StringBuilder("|list is empty|");
        }

        response.setMessage(result.toString());
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
