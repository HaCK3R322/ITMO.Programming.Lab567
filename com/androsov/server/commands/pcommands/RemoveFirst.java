package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.localization.Messenger;
import com.androsov.server.pojo.Product;
import com.androsov.server.products.exceptions.ManagementException;

import java.util.List;

public class RemoveFirst extends CommandImpl {
    
    private final ProductDao<Product, String> dao;

    public RemoveFirst(ProductDao<Product, String> dao) {
        this.dao = dao;
        

        name = "remove_first";
        description = "removes first element of collection.";
        argumentFormat = "void";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        final Response response = new ResponseImpl(request.getUser());

        String result;

        if(dao.getList().size() > 0) {
            result = Messenger.rb.getString("removeFirst.Product_with_id") + " " + dao.getList().get(0).getId() + " " + Messenger.rb.getString("removeFirst.was_removed");
            for (Product product : dao.getList()) {
                try {
                    dao.remove(product, request.getUser().getNickname());
                } catch (ManagementException e) {
                    result = e.getMessage();
                }
                break;
            }
        } else {
            result = Messenger.rb.getString("removeFirst.List_is_already_empty");
        }

        response.setMessage(result);

        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
