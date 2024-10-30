import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private List<String> args = new ArrayList<>();
    String commandName;
    static String input;

    public void parse(String input) {
        Parser.input = input;
        args = new ArrayList<>(Arrays.asList(input.split(" ")));
        commandName = args.get(0);
        args.remove(0);
    }

    public static void combineToOneString(List<String> args) {
        String s = String.join(" ", args);
        args.clear();
        args.add(s);
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getArgs() {
        return args;
    }
}
