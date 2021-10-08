package com.androsov.server.scripting;

import com.androsov.server.products.exceptions.SelfCycledScriptChainException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Script class that collects all commands from file and checks if this script self-cycled.
 */
public class Script {
    public Script(String filePath) throws IOException, SelfCycledScriptChainException {
        commands = readScriptFromFile(filePath);
        if(checkSelfCycling(commands, new HashSet<>()))
            throw new SelfCycledScriptChainException("Script contains self-cycled chain of scripts.");
        nextLine = "";
        commandLine = 0;
        this.filePath = filePath;
    }

    public Script(String filePath, List<String> commands) {
        this.commands = commands;
        nextLine = "";
        commandLine = 0;
        this.filePath = filePath;
    }

    public List<String> commands;
    public String nextLine;
    public int commandLine;
    public String filePath;

    public int getSize() {
        return commands.size();
    }

    /**
     * Reads script from file.
     * @param filePath file pass, okay?
     * @return {@link List} of commands in String format.
     * @throws IOException
     */
    public static List<String> readScriptFromFile(String filePath) throws IOException {

        List<String> scriptCommands = new ArrayList<>();

        FileReader reader = new FileReader(filePath);

        BufferedReader bufferedReader = new BufferedReader(reader);

        String line;
        while((line = bufferedReader.readLine()) != null) {
            scriptCommands.add(line);
        }

        bufferedReader.close();
        reader.close();

        return scriptCommands;
    }

    /**
     * Checks if input List of commands self-cycled.
     * @param scriptCommands commands in {@link String} format
     * @param previousNames {@link Set} of previous scripts.
     * @return {@code true} if self-cycled and {@code false} if not
     * @throws IOException If there is some File reading exception occurred
     */
    public boolean checkSelfCycling(List<String> scriptCommands, Set<String> previousNames) throws IOException {
        boolean cycled = false;

        List<String> singleScriptReferences = new LinkedList<>(getSingleScriptReferences(scriptCommands));

        if(!(singleScriptReferences.size() == 0)) {

            if(previousNames.add(filePath)) {
                for (String singleScriptReference : singleScriptReferences) {
                    if (checkSelfCycling(readScriptFromFile(singleScriptReference), previousNames)) {
                        cycled = true;
                    }
                }
            } else {
                cycled = true;
            }

        }

        return cycled;
    }

    /**
     * Get only scripts references from list of commands.
     * @param commandLines commands
     * @return set of scripts
     */
    protected static Set<String> getSingleScriptReferences(List<String> commandLines) {
        List<String> non_single = new LinkedList<>();

        //парсим имена всех скриптов
        for (String line : commandLines) {
            if (line.split(" ")[0].equals("execute_script")) {     // разделяем строку на слова, смотрим равно ли первое слово execute_script
                if (line.split(" ").length > 1) {                 // смотрим есть ли имя у скрипта
                    non_single.add(line.split(" ")[1]);          // добавляем в SET всех ссылок на скрипты
                }
            }
        }


        return new HashSet<>(non_single);
    }
}
