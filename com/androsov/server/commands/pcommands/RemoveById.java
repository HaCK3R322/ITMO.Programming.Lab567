package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.localization.Messenger;
import com.androsov.server.pojo.Product;
import com.androsov.server.products.ProductBuilder;
import com.androsov.server.products.exceptions.ManagementException;

import java.util.List;

public class RemoveById extends CommandImpl {
    ProductBuilder productBuilder;
    private final ProductDao<Product, String> dao;

    public RemoveById(ProductDao<Product, String> dao) {
        this.dao = dao;
        

        name = "remove_by_id";
        description = "Removes product with given id.";
        argumentFormat = "Long";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        final Response response = new ResponseImpl(request.getUser());
        String result;

        if(args.size() > 0) {
            try {
                Long id = (Long)args.get(0);
                for (Product product : dao.getList()) {
                    if (id.equals(product.getId())) {
                        dao.remove(product, request.getUser().getNickname());
                        break;
                    }
                }
                result = "Product was removed!";
            } catch (NumberFormatException e) {
                result = Messenger.rb.getString("removeById.Wrong_id_format_Please_enter_long_format_argument");
            } catch (ManagementException e) {
                result = e.getMessage();
            }
        } else {
            result = Messenger.rb.getString("removeById.Please_enter_id");
        }

        response.setMessage(result);
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}