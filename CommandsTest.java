import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CommandsTest {
    private ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    Terminal terminal = Terminal.getInstance();

    @Test
    public void testPWD() {
        String s = terminal.commands.pwd();
        assertEquals(s, terminal.getCurrentDirectory().toString());
    }

    @Test
    public void testCDShortPath() {
        String file = "TestFolder";
        terminal.commands.mkdir(List.of(file));
        terminal.commands.cd(List.of(file));
        String currDir = terminal.getCurrentDirectory().toString();
        String pwd = terminal.commands.pwd();
        assertEquals(currDir, pwd);
        terminal.commands.rmdir(List.of(currDir));

    }

    @Test
    public void testCDFullPath() {
        String file = terminal.getHomeDirectory() + "\\TestFolder";
        terminal.commands.mkdir(List.of(file));
        terminal.commands.cd(List.of(file));
        String currDir = terminal.getCurrentDirectory().toString();
        String pwd = terminal.commands.pwd();
        assertEquals(currDir, pwd);
        terminal.commands.rmdir(List.of(currDir));
    }

    @Test
    public void testLS() {
        String s = terminal.commands.ls();

        File currentDir = terminal.getCurrentDirectory();
        String[] expectedFiles = currentDir.list();
        if (expectedFiles != null) {
            for (String file : expectedFiles) {
                assertTrue(s.contains(file), "Expected file or directory not listed: " + file);
            }
        }
    }

    @Test
    public void testLS_a() {
        String s = terminal.commands.ls_a();
        File currentDir = new File(".");
        String[] expectedFiles = currentDir.list();
        if (expectedFiles != null) {
            for (String file : expectedFiles) {
                assertTrue(s.contains(file), "Expected file or directory not listed: " + file);
            }
        }
    }


    @Test
    public void testLS_r() {
        String s = terminal.commands.ls_r();
        StringBuilder filesString = new StringBuilder();

        String[] files = terminal.getCurrentDirectory().list();
        if (files != null) {
            // Sort the list in reverse order
            for (int i = files.length - 1; i >= 0; i--) {
                filesString.append(files[i]);
                filesString.append('\n');
            }
        }
        assertEquals(s, filesString.toString());
    }

    @Test
    public void testMkdirShortPath() {
        String file = "TestFolder";
        String folderName = terminal.getCurrentDirectory() + "\\" + file;
        terminal.commands.mkdir(List.of(file));
        terminal.commands.cd(List.of(terminal.getCurrentDirectory().toString()));
        String s = terminal.commands.ls();
        assertTrue(s.contains(file));
        terminal.commands.rmdir(List.of(folderName));
    }

    @Test
    public void testMkdirFullPath() {
        String folderName = "TestFolder";
        String folderDir = terminal.getHomeDirectory() + "\\" + folderName;
        terminal.commands.mkdir(List.of(folderDir));
        terminal.commands.cd(List.of(terminal.getHomeDirectory().toString()));
        String s = terminal.commands.ls();
        assertTrue(s.contains(folderName));
        terminal.commands.rmdir(List.of(folderDir));
    }

    @Test
    public void testRmdirShortpath() {
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));
        // now we are in C:\Users\<Current User>
        terminal.commands.mkdir(List.of("TestFolder"));
        String s = terminal.commands.ls();
        assertTrue(s.contains("TestFolder"));
        terminal.commands.rmdir(List.of("TestFolder"));
        s = terminal.commands.ls();
        assertFalse(s.contains("TestFolder"));
    }

    @Test
    public void testRmdirFullpath() {
        // go to home
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));
        // make directory
        String fullDir = homeDir + "\\TestFolder";
        terminal.commands.mkdir(List.of(fullDir));
        String s = terminal.commands.ls();
        assertTrue(s.contains("TestFolder"));
        terminal.commands.rmdir(List.of(fullDir));
        s = terminal.commands.ls();
        assertFalse(s.contains("TestFolder"));
    }

    @Test
    public void testTouch() {
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));

        String[] args = {"test1.txt", "test2.txt", "test3.txt"};
        terminal.commands.touch(List.of(args));

        String s = terminal.commands.ls();
        for (String arg : args) {
            assertTrue(s.contains(arg));
            terminal.commands.rm(List.of(arg));
        }
    }

    @Test
    public void testRm() {
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));

        String[] args = {"test1.txt", "test2.txt", "test3.txt"};
        terminal.commands.touch(List.of(args));

        String s = terminal.commands.ls();
        for (String arg : args) {
            assertTrue(s.contains(arg));
            terminal.commands.rm(List.of(arg));
        }

        s = terminal.commands.ls();
        for (String arg : args) {
            assertFalse(s.contains(arg));
        }
    }

    @Test
    public void testMv() {
        // move to home
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));

        // make file
        terminal.commands.touch(List.of("test1.txt"));
        String s = terminal.commands.ls();

        // check that the file has been created
        assertTrue(s.contains("test1.txt"));

        // make directory
        terminal.commands.mkdir(List.of("TestFolder"));

        // move file to directory
        terminal.commands.mv(List.of("test1.txt", "TestFolder"));
        s = terminal.commands.ls();

        // check that file has been moved
        assertFalse(s.contains("test1.txt"));

        // remove folder again
        terminal.commands.rm(List.of("TestFolder\\test1.txt"));
        terminal.commands.rmdir(List.of("TestFolder"));

    }

    @Test
    public void testCat() {
        // move to home
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));

        Path filePath = Paths.get(homeDir, "alphabet.txt");

        try {
            // Write the alphabet to the file
            Files.write(filePath, "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + e.getMessage());
        }
        String s = terminal.commands.cat(List.of("alphabet.txt"));
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ\n", s);

        // remove file
        terminal.commands.rm(List.of("alphabet.txt"));
    }

    @Test
    public void testRedirection() {
        // move to home
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));

        // put text into file then read the file
        terminal.commands.redirect("Ana Wad Gamed Awy", homeDir + "\\Redirection.txt");

        String data = terminal.commands.cat(List.of("Redirection.txt"));

        assertEquals("Ana Wad Gamed Awy\n", data);

        // remove file

        terminal.commands.rm(List.of("Redirection.txt"));
    }

    @Test
    public void testRedirectionAppend() {
        // move to home
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));

        // put text into file then read the file
        terminal.commands.redirectAppend("Ana Wad Gamed Awy ", homeDir + "\\Redirection.txt");

        terminal.commands.redirectAppend("Ana Wad Gamed Awy ", homeDir + "\\Redirection.txt");

        String data = terminal.commands.cat(List.of("Redirection.txt"));

        assertEquals("Ana Wad Gamed Awy Ana Wad Gamed Awy \n", data);

        // remove file

        terminal.commands.rm(List.of("Redirection.txt"));
    }

    @Test
    public void testPipePwdIntoFile() {
        // move to home
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));
        String command = "pwd | xargs > " + homeDir + "\\test.txt";
        terminal.commands.pipe(command);// should return homeDir in test.txt

        String data = terminal.commands.cat(List.of("test.txt"));

        assertEquals(homeDir+'\n', data);

        terminal.commands.rm(List.of("test.txt"));

    }

    @Test
    public void testPipeLsIntoFile() {
        // move to home
        String homeDir = terminal.getHomeDirectory().toString();
        terminal.commands.cd(List.of(homeDir));
        String s = terminal.commands.ls();
        String command = "ls | xargs > " + homeDir + "\\test.txt";
        terminal.commands.pipe(command);// should return homeDir in test.txt
        String data = terminal.commands.cat(List.of("test.txt"));

        assertEquals(s, data);

        terminal.commands.rm(List.of("test.txt"));

    }


    @Test
    public void testHelp() {
        String helpString = "Available commands:\n" +
                "exit         - Exit the CLI\n" +
                "help         - Display this help message\n" +
                "pwd          - Print working directory\n" + // works with pipe
                "cd <dir>     - Change directory\n" +
                "ls           - List files\n" + // works with pipe
                "ls -a        - List all files, including hidden files\n" + // works with pipe
                "ls -r        - List files in reversed order\n" + // works with pipe
                "mkdir <dir>  - Create a new directory\n" +
                "rmdir <dir>  - Remove an empty directory\n" +
                "touch <file> - Create an empty file\n" +
                "mv <src> <dst> - Move a file to another destination\n" +
                "rm <file>    - Remove a file\n" +
                "cat <file>   - Display file content\n" + // works with pipe
                "> <file>     - Redirect output to a file\n" +
                ">> <file>    - Append output to a file\n" +
                "| <cmd>      - Pipe output to another command";
        String s = terminal.commands.displayHelp();
        assertEquals(s, helpString);
    }
}