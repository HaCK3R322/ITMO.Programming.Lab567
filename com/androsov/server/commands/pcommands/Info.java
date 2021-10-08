package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.localization.Messenger;
import com.androsov.server.pojo.Product;

import java.time.LocalDateTime;
import java.util.List;

public class Info extends CommandImpl {
    private final LocalDateTime initializationTime;
    private final ProductDao<Product, String> dao;

    public Info(ProductDao<Product, String> dao, LocalDateTime initializationTime) {
        this.dao = dao;
        this.initializationTime = initializationTime;

        name = "info";
        description = "gives some info about the hole collection.";
        argumentFormat = "void";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        final Response response = new ResponseImpl(request.getUser());

        response.setMessage((Messenger.rb.getString("info.Collection_info") + ":" + "\n" +
                "   " + Messenger.rb.getString("info.number_of_elements") + ": " + dao.getList().size()
        ));

        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
