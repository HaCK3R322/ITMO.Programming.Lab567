package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.dao.ProductStorageDao;
import com.androsov.server.pojo.Product;

public class LoginUser extends CommandImpl {
    private final ProductDao<Product, String> dao;

    public LoginUser(ProductDao<Product, String> dao) {
        this.dao = dao;

        name = "login";
        description = "Login in system";
        argumentFormat = "String";
        userAccessible = false;
    }

    @Override
    public Response execute(Request request) {
        Response response = new ResponseImpl(request.getUser());

        final String nickname = request.getUser().getNickname();
        final String password = request.getUser().getPassword();

        if(ProductStorageDao.getInstance().hasUser(nickname)) {
            if(ProductStorageDao.getInstance().passwordIsRight(nickname, password)) {
                response.setMessage("Welcome back " + nickname +  " !");
                response.addData(true);
            } else {
                response.setMessage("Wrong password!");
                response.addData(false);
            }
        } else {
            response.setMessage("Nickname " + nickname + " is not registered yet.");
            response.addData(false);
        }

        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
