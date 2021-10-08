package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.localization.Messenger;
import com.androsov.server.pojo.Product;

public class Save extends CommandImpl {
    
    private final ProductDao<Product, String> dao;

    public Save(ProductDao<Product, String> dao) {
        this.dao = dao;
        

        name = "save";
        description = "Saves collection.";
        argumentFormat = "void";
        userAccessible = false;
    }

    @Override
    public Response execute(Request request) {
        final Response response = new ResponseImpl(request.getUser());
        String result;

        dao.commit();

        result = Messenger.rb.getString("save.saved");

        response.setMessage(result);
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
