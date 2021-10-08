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

public class RemoveByManufactureCost extends CommandImpl {
    
    private final ProductDao<Product, String> dao;

    public RemoveByManufactureCost(ProductDao<Product, String> dao) {
        this.dao = dao;
        

        name = "remove_any_by_manufacture_cost";
        description = "-_-";
        argumentFormat = "Float";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        final Response response = new ResponseImpl(request.getUser());
        String result = "";
        if(args.size() > 0) {
            try {
                Float cost = (Float)args.get(0);
                for (Product product : dao.getList()) {
                    if (product.getManufactureCost().equals(cost)) {
                        dao.remove(product, request.getUser().getNickname());
                        result = Messenger.rb.getString("removeById.Product_with_id") + product.getId() + Messenger.rb.getString("removeById.was_removed");
                    }
                }
            } catch (NumberFormatException e) {
                result = Messenger.rb.getString("removeByManufactureCost.Wrong_id_format_Please_enter_long_format_argument");
            } catch (ManagementException e) {
                result = e.getMessage();
            }
        } else {
            result = Messenger.rb.getString("removeByManufactureCost.Please_enter_cost");
        }
        response.setMessage(result);
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
