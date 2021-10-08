package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.localization.Messenger;
import com.androsov.server.pojo.Product;

import java.util.List;

public class CountByPrice extends CommandImpl {
    private final ProductDao<Product, String> dao;

    public CountByPrice(ProductDao<Product, String> dao) {
        this.dao = dao;

        name = "count_by_price";
        description = "Shows how many products have that price.";
        argumentFormat = "Integer";
        userAccessible = true;
    }

    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        Response response = new ResponseImpl(request.getUser());

        StringBuilder result = new StringBuilder();
        if(args.size() > 0)
            try {
                Integer price = (Integer) args.get(0);
                int count = 0;

                count += dao.getList().stream()
                        .map(Product::getPrice)
                        .filter(i -> i.equals(price))
                        .mapToInt(i -> i)
                        .count();

                result.append(count);
            } catch (NumberFormatException e) {
                result.append(Messenger.rb.getString("countByPrice.countByPrice.Wrong_number_format"));
            }
        else
            result.append(Messenger.rb.getString("countByPrice.Please_enter_argument"));

        response.setMessage(result.toString());
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
