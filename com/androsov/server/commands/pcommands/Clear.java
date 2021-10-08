package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.dao.ProductStorageDao;
import com.androsov.server.localization.Messenger;
import com.androsov.server.pojo.Product;
import com.androsov.server.products.exceptions.ManagementException;

public class Clear extends CommandImpl {
    private final ProductDao<Product, String> dao;

    public Clear(ProductDao<Product, String> dao) {
        this.dao = dao;

        name = "clear";
        description = "deletes all products from collection.";
        argumentFormat = "void";
        userAccessible = true;
    }

    public Response execute(Request request) {
        Response response = new ResponseImpl(request.getUser());

        response.setMessage(Messenger.rb.getString("clear.result"));

        for (Product product : dao.getList()) {
            if (ProductStorageDao.getInstance().getProductUserMap().get(product).equals(request.getUser().getNickname())) {
                try {
                    dao.remove(product, request.getUser().getNickname());
                } catch (ManagementException e) {
                    e.printStackTrace();
                }
            }
        }

        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
