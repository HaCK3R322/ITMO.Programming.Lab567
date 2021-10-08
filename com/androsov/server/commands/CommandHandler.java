package com.androsov.server.commands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.server.commands.pcommands.*;
import com.androsov.server.dao.ProductDao;
import com.androsov.server.pojo.Product;
import com.androsov.server.products.ProductBuilder;
import com.androsov.server.products.exceptions.CommandBuildError;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that regist and executes {@link Command}s.
 */
public class CommandHandler {
    public final HashMap<String, Command> commandMap;
    public final List<String> history;

    public CommandHandler() {
        commandMap = new HashMap<>();
        history = new LinkedList<>();
    }

    /**
     * Allows to regist {@link Command}.
     * @param command {@link Command}
     * @throws CommandBuildError If one of the fields of command that we trying to regist is {@code null}.
     */
    public void registryCommand(Command command) throws CommandBuildError {
        CommandStructureChecker.check(command);
        commandMap.put(command.getName(), command);
    }

    /**
     * Regist standard {@link Command}s.
     * @param dao {@link com.androsov.server.dao.ProductStorageDao}
     * @param productBuilder {@link ProductBuilder}
     * @param initializationTime - time of initialization lol
     */
    public void init(ProductDao<Product, String> dao , ProductBuilder productBuilder, LocalDateTime initializationTime) {
        registryCommand(new ChangeLanguage());
        registryCommand(new Add(dao, productBuilder));
        registryCommand(new AverageOfManufactureCost(dao));
        registryCommand(new Clear(dao));
        registryCommand(new CountByPrice(dao));
        registryCommand(new ExecuteScript(this));
        registryCommand(new Help(this));
        registryCommand(new History(this));
        registryCommand(new Info(dao, initializationTime));
        registryCommand(new RemoveById(dao));
        registryCommand(new RemoveByManufactureCost(dao));
        registryCommand(new RemoveFirst(dao));
        registryCommand(new Save(dao));
        registryCommand(new Show(dao));
        registryCommand(new Sort(dao));
        registryCommand(new UpdateById(dao, productBuilder));
        registryCommand(new Exit());
        registryCommand(new GetCommandsFormats(this));
        registryCommand(new RegistUser(dao));
        registryCommand(new LoginUser(dao));
    }

    /**
     * Executes {@link Command} and returns {@link Response}
     * @param request {@link Request}
     * @return {@link Response}
     * @throws NullPointerException In case of request contains a non-existent command
     * @see Response
     * @see Request
     */
    public Response executeCommand(Request request) throws NullPointerException {
        history.add(request.getCommandName());
        return commandMap.get(request.getCommandName()).execute(request);
    }

    public Command getCommand(String name) {
        return commandMap.get(name);
    }
}
