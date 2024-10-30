import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class Terminal {
    Parser parser;
    Commands commands;
    // Singleton Terminal
    private static volatile Terminal terminal;
    File currentDirectory;
    File homeDirectory;

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

    public void chooseCommandAction(String commandName) {
        if (Parser.input.contains(">>")) {
            commands.redirectAppend(Parser.input);
        } else if (Parser.input.contains(">")) {
            commands.redirect(Parser.input);
        } else if (Parser.input.contains("|")) {
            commands.pipe(Parser.input);
        } else if ("rmdir".equals(commandName)) {
            commands.rmdir(parser.getArgs());
        } else if ("pwd".equals(commandName)) {
            commands.pwd();
        } else if ("mv".equals(commandName)) {
            commands.mv(parser.getArgs());
        } else if ("ls-a".equals(commandName)) {
            commands.ls_a();
        } else if ("cd".equals(commandName)) {
            commands.cd(parser.getArgs());
        } else if ("ls".equals(commandName)) {
            commands.ls();
        } else if ("ls-r".equals(commandName)) {
            commands.ls_r();
        } else if ("mkdir".equals(commandName)) {
            commands.mkdir(parser.getArgs());
        } else if ("touch".equals(commandName)) {
            commands.touch(parser.getArgs());
        } else if ("rm".equals(commandName)) {
            commands.rm(parser.getArgs());
        } else if ("cat".equals(commandName)) {
            commands.cat(parser.getArgs());
        } else if ("help".equals(commandName)) {
            commands.displayHelp();
        } else if ("exit".equals(commandName)) {
            exit(0);
        }
    }


    public void main() {
        while (true) {
            String s;
            Scanner scanner = new Scanner(System.in);
            s = scanner.nextLine();
            parser.parse(s);
            chooseCommandAction(parser.commandName);
        }
    }

    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }
}
