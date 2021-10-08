package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.localization.Messenger;
import com.androsov.server.pojo.Product;
import com.androsov.server.products.ProductBuilder;
import com.androsov.server.products.exceptions.ContentException;
import com.androsov.server.products.exceptions.ManagementException;

import java.util.List;

public class UpdateById extends CommandImpl {
    private final ProductBuilder productBuilder;
    
    private final ProductDao<Product, String> dao;

    private final Add add;

    public UpdateById(ProductDao<Product, String> dao, ProductBuilder productBuilder) {
        this.dao = dao;
        this.productBuilder = productBuilder;
        

        add = new Add(dao, productBuilder);

        name = "update_by_id";
        description = "Manual product update with given id.";
        argumentFormat = "Long";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        final Response response = new ResponseImpl(request.getUser());
        String result = null;

        if(args.size() > 0) {
            try {
                Long id = (Long)args.get(0);
                args.remove(0);
                String partNumber = null;
                for (Product product : dao.getList()) {
                    if (product.getId() == id) {
                        partNumber = product.getPartNumber();
                        break;
                    }
                }
                if(partNumber != null)
                    productBuilder.usedPartNumbers.remove(partNumber);

                for(Product product : dao.getList()) {
                    if (product.getId() == id) {
                        dao.remove(product, request.getUser().getNickname());
                        dao.add(productBuilder.buildProduct(args), request.getUser().getNickname());
                        break;
                    }
                }

                result = Messenger.rb.getString("updateById.Product_was_updated");
            } catch (NumberFormatException e) {
                result = Messenger.rb.getString("updateById.Wrong_id_format_Enter_long");
            } catch (ContentException e) {
                result = e.getMessage();
            } catch (ManagementException e) {
                e.printStackTrace();
            }

        } else {
            result = Messenger.rb.getString("updateById.Please_enter_id");
        }

        response.setMessage(result);
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
