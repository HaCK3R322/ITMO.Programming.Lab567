package com.androsov.server;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.server.commands.CommandHandler;
import com.androsov.server.dao.ProductStorageDao;
import com.androsov.server.io.ServerIO;
import com.androsov.server.localization.Messenger;
import com.androsov.server.logging.LogToFile;
import com.androsov.server.products.ProductBuilder;
import com.androsov.server.products.exceptions.ContentException;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Server {
    public static void main(String[] args) throws IOException, SQLException, ContentException {
        LogToFile.configureLogger("C:/Users/dictator_zx/IdeaProjects/ITMO/Programming/lab5678/logs/lastLog.txt");
        Logger.getLogger("LOGGER").info("Server starts to work.");
        new Messenger(new Locale("eng"));

        LocalDateTime initializationTime;
        initializationTime = LocalDateTime.now();

        ServerIO serverIO = new ServerIO();

        ProductBuilder productBuilder = ProductBuilder.getInstance();
        try (ProductStorageDao dao = ProductStorageDao.createInstance(productBuilder)) {

            CommandHandler commandHandler = new CommandHandler();
            commandHandler.init(dao, productBuilder, initializationTime);

            LinkedList<Request> requestsList = new LinkedList<>();
            LinkedList<Response> responsesList = new LinkedList<>();

            class GetRequestThread implements Runnable {
                @Override
                public void run() {
                    final Request request = serverIO.get();
                    if(request != null)
                        requestsList.add(request);
                }
            }
            GetRequestThread getRequestThread = new GetRequestThread();

            class CreateResponseThread implements Runnable {
                private final Request request;

                public CreateResponseThread(Request request) {
                    this.request = request;
                }

                @Override
                public void run() {
                    try {
                        final Response response = commandHandler.executeCommand(request);
                        responsesList.add(response);
                    } catch (NullPointerException ignored) {
                    }
                }
            }

            ExecutorService executorService = Executors.newCachedThreadPool();

            // get fist request
            Future<?> requestGettingStatus = executorService.submit(getRequestThread);
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    serverIO.acceptAll();   // accept all clients

                    // --- ADD ONE REQUEST TO REQUESTS LIST ---
                    if (serverIO.hasRequest()) { // if server has one or more requests
                        if (requestGettingStatus.isDone())
                            requestGettingStatus = executorService.submit(getRequestThread);
                    }

                    // --- SEND ALL REQUEST TO EXECUTION ---
                    final LinkedList<Request> notSentToExecutionRequests = new LinkedList<>();  // creating list of requests, each will have new thread for execution
                    if (requestsList.size() != 0) {  // if we have some not executed requests
                        notSentToExecutionRequests.addAll(requestsList); // fill list of not sent to execution requests
                        requestsList.removeAll(notSentToExecutionRequests); // removing sent requests
                    }
                    for (Request request : notSentToExecutionRequests) { // for each request not sent to execution request
                        executorService.execute(new CreateResponseThread(request)); // create own execution-thread, that generates responses and fills them to response list
                    }


                    // -- SEND ALL RESPONSES TO USERS ---
                    final LinkedList<Response> notSentToSendResponses = new LinkedList<>();
                    if (responsesList.size() != 0) {
                        notSentToSendResponses.addAll(responsesList);
                        responsesList.removeAll(notSentToSendResponses);
                    }
                    for (Response response : notSentToSendResponses) {
                        Thread sendThread = new Thread(() -> serverIO.send(response)); // creating own send-thread for each response
                        sendThread.start();
                    }

                    dao.commit(); // commit changes produced in execution threads
                } catch (IOException | ConcurrentModificationException | NullPointerException | NoSuchElementException e) {
                    Logger.getLogger("LOGGER").warning(e.getMessage());
                } catch (CancelledKeyException ignored) {
                }
            }
        }
    }
}

