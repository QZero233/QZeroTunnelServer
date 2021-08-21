package com.qzero.tunnel.server.command.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutor {

    private static CommandExecutor instance;

    private Logger log= LoggerFactory.getLogger(getClass());

    private Map<String, ServerCommand> commandMap=new HashMap<>();

    public static CommandExecutor getInstance() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        if(instance==null)
            instance=new CommandExecutor();
        return instance;
    }

    private CommandExecutor() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        loadCommands();
    }

    public void loadCommands() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        loadCommandsFor(ClientCommandProcessor.class);
    }

    private void loadCommandsFor(Class cls) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Object instance=cls.getDeclaredConstructor().newInstance();
        Method[] methods=cls.getDeclaredMethods();
        for(Method method:methods){
            CommandMethod commandMethodAnnotation=method.getAnnotation(CommandMethod.class);
            if(commandMethodAnnotation==null)
                continue;

            String commandName=commandMethodAnnotation.commandName();
            if(commandName==null)
                throw new IllegalArgumentException(String.format("Command name for class %s can not be empty", cls.getName()));

            if(commandMap.containsKey(commandName))
                throw new IllegalArgumentException(String.format("Command %s has more than one implements", commandName));

            Type[] parameters=method.getParameterTypes();
            if(parameters.length!=2)
                throw new IllegalArgumentException(String.format("Command method %s for command %s does not have 2 parameters(actually it's %d)",
                        method.getName(),commandName,parameters.length));

            if(!(parameters[0].equals(String[].class) &&
                    parameters[1].equals(String.class)))
                throw new IllegalArgumentException(String.format("Command method %s for command %s does not have matched parameter types",
                        method.getName(),commandName));

            if(!method.getReturnType().equals(String.class))
                throw new IllegalArgumentException(String.format("Command method %s for command %s does not take String as return value",
                        method.getName(),commandName));


            commandMap.put(commandName, new ServerCommand() {
                @Override
                public int getCommandParameterCount() {
                    return commandMethodAnnotation.parameterCount();
                }

                @Override
                public String execute(String[] commandParts, String fullCommand) {
                    try {
                        method.setAccessible(true);
                        return (String) method.invoke(instance,commandParts,fullCommand);
                    } catch (Exception e){
                        log.error(String.format("Failed to invoke command method %s for command %s", method.getName(),commandName),e);
                        return "Failed to execute command, please contact admin to see logs for detail";
                    }
                }
            });
        }
    }

    private String[] splitCommand(String commandLine){
        byte[] buf=commandLine.getBytes();
        List<String> commandParts=new ArrayList<>();


        ByteArrayOutputStream current=new ByteArrayOutputStream();
        //ByteOutputStream current=new ByteOutputStream();
        boolean whole=false;
        boolean escape=false;
        for(byte b:buf) {
            if (escape) {
                current.write(b);
                escape = false;
                continue;
            }

            if (b == '\\') {
                escape = true;
                continue;
            }

            if (b == '\"') {
                whole = !whole;
                continue;
            }

            if (b == ' ') {
                if (whole) {
                    current.write(b);
                } else {
                    commandParts.add(new String(current.toByteArray(),0, current.size()));
                    current = new ByteArrayOutputStream();
                }
                continue;
            }

            current.write(b);
        }

        commandParts.add(new String(current.toByteArray(),0, current.size()));

        return commandParts.toArray(new String[]{});
    }

    public String executeCommand(String commandLine){
        String[] parts=splitCommand(commandLine);
        String commandName=parts[0];
        if(!commandMap.containsKey(commandName))
            return "Unknown command called "+commandName;

        ServerCommand consoleCommand=commandMap.get(commandName);

        int parameterCount=consoleCommand.getCommandParameterCount();
        if(parts.length-1<parameterCount)
            return String.format("Command %s need as least %d parameters, but there are only %d", commandName,
                    parameterCount,parts.length-1);

        return consoleCommand.execute(parts,commandLine);
    }

}
