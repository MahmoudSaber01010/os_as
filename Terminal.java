import java.io.File;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.System.*;

public class Terminal {
    Parser parser;
    Commands commands;
    // Singleton Terminal
    private static volatile Terminal terminal;
    File currentDirectory;
    File homeDirectory;
    String currentCommand;

    // Singleton Terminal Constructor
    private Terminal() {
        parser = new Parser();
        commands = new Commands();
        currentDirectory = new File(System.getProperty("user.dir"));
        homeDirectory = new File(System.getProperty("user.home"));
    }

    public static Terminal getInstance() {
        if (terminal == null) {
            terminal = new Terminal();
        }
        return terminal;
    }

    public String chooseCommandAction(String commandName) {
        if ("rmdir".equals(commandName)) {
            commands.rmdir(parser.getArgs());
        } else if ("pwd".equals(commandName)) {
            return commands.pwd();
        } else if ("mv".equals(commandName)) {
            commands.mv(parser.getArgs());
        } else if ("ls-a".equals(commandName)) {
            return commands.ls_a();
        } else if ("cd".equals(commandName)) {
            commands.cd(parser.getArgs());
        } else if ("ls".equals(commandName)) {
            return commands.ls();
        } else if ("ls-r".equals(commandName)) {
            return commands.ls_r();
        } else if ("mkdir".equals(commandName)) {
            commands.mkdir(parser.getArgs());
        } else if ("touch".equals(commandName)) {
            commands.touch(parser.getArgs());
        } else if ("rm".equals(commandName)) {
            commands.rm(parser.getArgs());
        } else if ("cat".equals(commandName)) {
            return commands.cat(parser.getArgs());
        } else if ("help".equals(commandName)) {
            return commands.displayHelp();
        } else if ("exit".equals(commandName)) {
            exit(0);
        }
        return "";
    }

    public void trimStrings(String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
    }

    public void pipeAndRedirection() {
        String s = Parser.input;
        if (s.contains(" | ")) {
            commands.pipe(Parser.input);
        } else if (s.contains(" >> ")) {
            String[] args = Parser.input.split(" >> ");
            trimStrings(args);
            if (isCommand(args[0])) {
                commands.redirectAppend(chooseCommandAction(args[0]), args[1]);
            } else
                commands.redirectAppend(args[0], args[1]);
        } else if (s.contains(" > ")) {
            String[] args = Parser.input.split(" > ");
            trimStrings(args);
            if (isCommand(args[0])) {
                commands.redirect(chooseCommandAction(args[0]), args[1]);
            } else
                commands.redirect(args[0], args[1]);
        }
    }

    private final String[] VALID_COMMANDS = {"pwd", "cd", "ls", "ls-a", "ls-r", "mkdir", "rmdir", "touch", "mv", "rm", "cat", "help"};

    public boolean isCommand(String command) {
        for (var cmd : VALID_COMMANDS) {
            if (Objects.equals(command, cmd)) return true;
        }
        return false;
    }

    public void main() {
        while (true) {
            String s;
            Scanner scanner = new Scanner(System.in);
            s = scanner.nextLine();
            parser.parse(s);
            pipeAndRedirection();
            out.println(chooseCommandAction(parser.commandName));
        }
    }

    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }
}
