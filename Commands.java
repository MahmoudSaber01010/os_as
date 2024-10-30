import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.*;

public class Commands {


    // Mohamed Alaa's Commands
    public void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  exit         - Exit the CLI");
        System.out.println("  help         - Display this help message");
        System.out.println("  pwd          - Print working directory"); // works with pipe
        System.out.println("  cd <dir>     - Change directory");
        System.out.println("  ls           - List files");// works with pipe
        System.out.println("  ls-a         - List all files, including hidden files");// works with pipe
        System.out.println("  ls-r         - List files in reversed order");// works with pipe
        System.out.println("  mkdir <dir>  - Create a new directory");
        System.out.println("  rmdir <dir>  - Remove an empty directory");
        System.out.println("  touch <file> - Create an empty file");
        System.out.println("  mv <src> <dst> - Move a file to another destination");
        System.out.println("  rm <file>    - Remove a file");
        System.out.println("  cat <file>   - Display file content");// works with pipe
        System.out.println("  > <file>     - Redirect output to a file");
        System.out.println("  >> <file>    - Append output to a file");
        System.out.println("  | <cmd>      - Pipe output to another command");
    }

    public void pwd() {
        Terminal terminal = Terminal.getInstance();
        System.out.println(terminal.getCurrentDirectory().getAbsolutePath());
    }

    public void ls_a() {
        File currentDir = new File(".");

        String[] files = currentDir.list();

        if (files != null) {
            for (String file : files) {
                System.out.println(file);
            }
        } else {
            System.out.println("The directory is empty or an I/O error occurred.");
        }
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
        pwd();
    }

    public void cdBack() {
        Terminal terminal = Terminal.getInstance();
        File parentDirectory = terminal.getCurrentDirectory().getParentFile();
        if (parentDirectory != null && parentDirectory.isDirectory()) {
            terminal.setCurrentDirectory(parentDirectory);
            pwd();
        }
    }

    public void ls() {
        Terminal terminal = Terminal.getInstance();
        String[] files = terminal.getCurrentDirectory().list();
        if (files != null) {
            for (String file : files) {
                System.out.println(file);
            }
        }
    }

    // remove file -should be Used in removing directory (rmdir)-
    public void rm(List<String> args) {
        Terminal terminal = Terminal.getInstance();
        for (String arg : args) {
            File file = new File(terminal.getCurrentDirectory(), arg);
            if (file.exists() && file.isFile()) {
                if (file.delete())
                    System.out.println("File : " + arg + " deleted successfully");
                else
                    System.out.println("Failed to remove this file: " + arg);
            }
        }
    }

    public void cat(List<String> args) {
        Terminal terminal = Terminal.getInstance();
        for (String arg : args) {
            File file = new File(terminal.getCurrentDirectory(), arg);
            if (file.exists() && file.isFile()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(arg));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                    br.close();
                } catch (IOException e) {
                    System.err.println("An error occurred while reading the file: " + e.getMessage());
                }
            }
        }
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


    public void ls_r() {
        Terminal terminal = Terminal.getInstance();
        String[] files = terminal.getCurrentDirectory().list();

        if (files != null) {
            // Sort the list in reverse order
            for (int i = files.length - 1; i >= 0; i--) {
                System.out.println(files[i]);
            }
        }
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
                if (rmDir.list().length == 0) {
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
                Files.move(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Moved file: " + source.getAbsolutePath() + " to " + destination.getAbsolutePath());
        }
    }

    public void redirect(String input) {
        PrintStream originalOut = out;
        Parser.input = "";
        Terminal terminal = Terminal.getInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        terminal.chooseCommandAction(terminal.parser.commandName);
        setOut(originalOut);

        StringBuilder fileName = new StringBuilder();
        var args = terminal.parser.getArgs();
        for (int i = 1; i < args.size(); i++) {
            fileName.append(args.get(i));
            if (i != args.size() - 1)
                fileName.append(' ');
        }

        File file = new File(fileName.toString());

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
                writer.write(outputStream.toString());
                System.out.println("Text successfully written to " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    public void redirectAppend(String input) {
        PrintStream originalOut = out;
        Parser.input = "";
        Terminal terminal = Terminal.getInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        terminal.chooseCommandAction(terminal.parser.commandName);
        setOut(originalOut);

        StringBuilder fileName = new StringBuilder();
        var args = terminal.parser.getArgs();
        for (int i = 1; i < args.size(); i++) {
            fileName.append(args.get(i));
            if (i != args.size() - 1)
                fileName.append(' ');
        }

        File file = new File(fileName.toString());

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
                writer.append(outputStream.toString());
                System.out.println("Text successfully appended to " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

    }

    public void pipe(String input) {
        int index = input.indexOf('>');

    }
}