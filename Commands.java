import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

import static java.lang.System.*;

public class Commands {


    // Mohamed Alaa's Commands
    public String displayHelp() {
        return "Available commands:\n" + "exit         - Exit the CLI\n" + "help         - Display this help message\n" + "pwd          - Print working directory\n" + // works with pipe
                "cd <dir>     - Change directory\n" + "ls           - List files\n" + // works with pipe
                "ls -a        - List all files, including hidden files\n" + // works with pipe
                "ls -r        - List files in reversed order\n" + // works with pipe
                "mkdir <dir>  - Create a new directory\n" + "rmdir <dir>  - Remove an empty directory\n" + "touch <file> - Create an empty file\n" + "mv <src> <dst> - Move a file to another destination\n" + "rm <file>    - Remove a file\n" + "cat <file>   - Display file content\n" + // works with pipe
                "> <file>     - Redirect output to a file\n" + ">> <file>    - Append output to a file\n" + "| <cmd>      - Pipe output to another command";
    }

    public String pwd() {
        Terminal terminal = Terminal.getInstance();
        return terminal.getCurrentDirectory().getAbsolutePath();
    }

    public String ls_a() {
        File currentDir = new File(".");

        String[] files = currentDir.list();
        StringBuilder s = new StringBuilder();
        if (files != null) {
            for (String file : files) {
                s.append(file);
                s.append('\n');
            }
        } else {
            System.out.println("The directory is empty or an I/O error occurred.");
        }
        return s.toString();
    }

    public void cd(List<String> args) {
        Terminal terminal = Terminal.getInstance();

        if (args.size() > 1) {
            Parser.combineToOneString(args);
        }

        // if the command is "cd" only, then we will return to the home directory
        if (args.isEmpty()) {
            terminal.setCurrentDirectory(new File(System.getProperty("user.home")));
        } else {
            // short path
            File firstDirectory = new File(terminal.getCurrentDirectory(), args.get(0));
            // full path
            File secondDirectory = new File(args.get(0));
            // handle short path first
            if (firstDirectory.exists() && firstDirectory.isDirectory()) {
                terminal.setCurrentDirectory(firstDirectory);
            }
            // then handle full path
            else if (secondDirectory.exists() && secondDirectory.isDirectory()) {
                terminal.setCurrentDirectory(secondDirectory);
            }
        }
        out.println(pwd());
    }

    public void cdBack() {
        Terminal terminal = Terminal.getInstance();
        File parentDirectory = terminal.getCurrentDirectory().getParentFile();
        if (parentDirectory != null && parentDirectory.isDirectory()) {
            terminal.setCurrentDirectory(parentDirectory);
            pwd();
        }
    }

    public String ls() {
        Terminal terminal = Terminal.getInstance();
        String[] files = terminal.getCurrentDirectory().list();
        StringBuilder s = new StringBuilder();
        if (files != null) {
            for (String file : files) {
                s.append(file);
                s.append('\n');
            }
        }
        return s.toString();
    }

    // remove file -should be Used in removing directory (rmdir)-
    public void rm(List<String> args) {
        Terminal terminal = Terminal.getInstance();
        for (String arg : args) {
            File file = new File(terminal.getCurrentDirectory(), arg);
            if (file.exists() && file.isFile()) {
                if (file.delete()) System.out.println("File : " + arg + " deleted successfully");
                else System.out.println("Failed to remove this file: " + arg);
            }
        }
    }

    public String cat(List<String> args) {
        Terminal terminal = Terminal.getInstance();
        StringBuilder s = new StringBuilder();
        for (String arg : args) {
            File file = new File(terminal.getCurrentDirectory(), arg);
            if (file.exists() && file.isFile()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        s.append(line);
                        s.append('\n');
                    }
                    br.close();
                } catch (IOException e) {
                    return "An error occurred while reading the file: " + e.getMessage();
                }
            }
        }
        return s.toString();
    }

    private void copyDirectory(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyDirectory(srcFile, destFile);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;

            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (Exception e) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    public String ls_r() {
        Terminal terminal = Terminal.getInstance();
        StringBuilder s = new StringBuilder();
        String[] files = terminal.getCurrentDirectory().list();
        if (files != null) {
            // Sort the list in reverse order
            for (int i = files.length - 1; i >= 0; i--) {
                s.append(files[i]);
                s.append('\n');
            }
        }
        return s.toString();
    }

    public void mkdir(List<String> directories) {
        //create new folder
        Terminal terminal = Terminal.getInstance();
        for (String dirName : directories) {
            File newDirectory;
            // Check if the directory argument is a full path or a directory name
            if (dirName.contains(File.separator)) {
                // Full path provided
                newDirectory = new File(dirName);
            } else {
                // Directory name provided, create in the current working directory
                newDirectory = new File(terminal.getCurrentDirectory(), dirName);
            }

            if (!newDirectory.exists() && newDirectory.mkdirs()) {
                System.out.println("Directory created: " + newDirectory.getAbsolutePath());
            }
        }
    }

    public void touch(List<String> args) {
        //create new file
        Terminal terminal = Terminal.getInstance();
        for (String s : args) {
            File newFile;
            if (s.contains(File.separator)) {
                newFile = new File(s);
            } else {
                newFile = new File(terminal.getCurrentDirectory(), s);
            }
            try {
                if (newFile.createNewFile()) {
                    System.out.println("File created: " + newFile.getAbsolutePath());
                } else {
                    System.out.println("File already exists: " + newFile.getAbsolutePath());
                }
            } catch (IOException e) {
                System.err.println("Failed to create the file: " + newFile.getAbsolutePath());
            }
        }
    }

    public void rmdir(List<String> directories) {
        Terminal terminal = Terminal.getInstance();
        for (String dirName : directories) {
            File rmDir;
            // Check if the directory argument is a full path or a directory name
            if (dirName.contains(File.separator)) {
                // Full path provided
                rmDir = new File(dirName);
            } else {
                // Directory name provided, create in the current working directory
                rmDir = new File(terminal.getCurrentDirectory(), dirName);
            }

            if (rmDir.exists() && rmDir.isDirectory()) {
                if (Objects.requireNonNull(rmDir.list()).length == 0) {
                    if (rmDir.delete()) {
                        System.out.println("Directory deleted: " + rmDir.getAbsolutePath());
                    } else {
                        System.out.println("Failed to delete directory: " + rmDir.getAbsolutePath());
                    }
                } else {
                    System.out.println("Cannot delete this directory as it is not empty: " + rmDir.getAbsolutePath());
                }
            } else {
                System.out.println("Directory does not exist: " + rmDir.getAbsolutePath());
            }
        }

    }

    public void mv(List<String> args) {
        Terminal terminal = Terminal.getInstance();
        File source = new File(terminal.getCurrentDirectory(), args.get(0));
        File destination = new File(terminal.getCurrentDirectory(), args.get(1));

        if (!source.exists()) {
            System.out.println("Source file does not exist: " + source.getAbsolutePath());
        } else {
            try {
                Path s = Paths.get(source.getAbsolutePath());
                Path d = Paths.get(destination.getAbsolutePath());
                Files.move(s, d.resolve(s.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Moved file: " + source.getAbsolutePath() + " to " + destination.getAbsolutePath());
        }
    }

    public void redirect(String textToBeWritten, String fileName) {
        File file = new File(fileName);

        try {
            // Check if parent directory exists or needs to be created
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs(); // Create parent directories if necessary
            }

            // Create the file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }

            // Write text into the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textToBeWritten);
                System.out.println("Text successfully written to " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    public void redirectAppend(String textToBeAppended, String fileName) {
        File file = new File(fileName);
        try {
            // Check if parent directory exists or needs to be created
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs(); // Create parent directories if necessary
            }

            // Create the file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }

            // Write text into the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.append(textToBeAppended);
                System.out.println("Text successfully appended to " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

    }

    public String pipe(String fullCommand) {
        Terminal terminal = Terminal.getInstance();
        String[] commands = fullCommand.split("\\|");

        terminal.trimStrings(commands);
        terminal.parser.parse(fullCommand);


        if (terminal.isCommand(commands[0])) {
            String out = terminal.chooseCommandAction(commands[0]);
            out = out.replaceAll("\\\\", "\\\\\\\\"); // Replace each backslash with two backslashes

            if (commands.length >= 2) {
                commands[1] = commands[1].replaceAll("xargs", out);
                StringBuilder s = new StringBuilder();
                for (int i = 1; i < commands.length; i++) {
                    s.append(commands[i]);
                    if (i != commands.length - 1) s.append(" | ");
                }
                return pipe(s.toString());
            }
        }
        terminal.pipeAndRedirection();
        return null;
    }
}