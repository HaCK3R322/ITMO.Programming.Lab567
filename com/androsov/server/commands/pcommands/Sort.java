package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.localization.Messenger;
import com.androsov.server.pojo.Product;

import java.util.List;

public class Sort extends CommandImpl {
    
    private final ProductDao<Product, String> dao;

    public Sort(ProductDao<Product, String> dao) {
        this.dao = dao;
        

        name = "sort";
        description = "sorts collection by product name.";
        argumentFormat = "void";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        final Response response = new ResponseImpl(request.getUser());
        //TODO client-side sort show
        response.setMessage(Messenger.rb.getString("sort.Sorted"));
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
