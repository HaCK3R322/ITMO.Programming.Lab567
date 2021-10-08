package com.androsov.server.commands.pcommands;

import com.androsov.general.User;
import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.dao.ProductStorageDao;
import com.androsov.server.pojo.Product;

public class RegistUser extends CommandImpl {
    private final ProductDao<Product, String> dao;

    public RegistUser(ProductDao<Product, String> dao) {
        this.dao = dao;

        name = "regist";
        description = "Registration in system";
        argumentFormat = "String";
        userAccessible = false;
    }

    @Override
    public Response execute(Request request) {
        Response response = new ResponseImpl(request.getUser());

        final String nickname = request.getUser().getNickname();
        final String password = request.getUser().getPassword();

        if(ProductStorageDao.getInstance().hasUser(nickname)) {
            response.setMessage("User with nickname " + nickname + " already exists.");
            response.addData(false);
        } else {
            ProductStorageDao.getInstance().createUser(new User(null, nickname, password));
            response.setMessage("Thanks for registration, + " + nickname + "!");
            response.addData(true);
        }

        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
