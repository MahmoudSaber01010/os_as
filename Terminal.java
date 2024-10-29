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
    List<String> history;

    // Singleton Terminal Constructor
    private Terminal() {
        parser = new Parser();
        commands = new Commands();
        currentDirectory = new File(System.getProperty("user.dir"));
        homeDirectory = new File(System.getProperty("user.home"));
        history = new ArrayList<>();
    }

    public static Terminal getInstance() {
        if (terminal == null) {
            terminal = new Terminal();
        }
        return terminal;
    }

    public void chooseCommandAction() {
        switch (parser.getCommandName()) {
            case "rmdir":
                commands.rmdir(parser.getArgs());
                break;
            case "pwd":
                commands.pwd();
                break;
            case "ls-a":
                commands.ls_a();
                break;
            case "cd":
                commands.cd(parser.getArgs());
                break;
            case "ls":
                commands.ls();
                break;
            case "ls-r":
                commands.ls_r();
                break;
            case "mkdir":
                commands.mkdir(parser.getArgs());
                break;
            case "touch":
                commands.touch(parser.getArgs());
                break;
            case "rm":
                commands.rm(parser.getArgs());
                break;
            case "cat":
                commands.cat(parser.getArgs());
                break;
            case "help":
                commands.displayHelp();
                break;
            case "exit":
                exit(0);
                break;
        }

    }


    public void main() {
        while (true) {
            String s;
            Scanner scanner = new Scanner(System.in);
            s = scanner.nextLine();
            parser.parse(s);
            history.add(s);
            chooseCommandAction();
        }
    }

    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }
}
