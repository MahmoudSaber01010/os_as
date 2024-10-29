import java.util.ArrayList;
import java.util.List;

public class Parser {
    private String commandName;
    private List<String> args;

    public Parser() {
        this.args = new ArrayList<>();
    }

    public void parse(String input) {
        args.clear();
        input += ' ';

        boolean foundSpace = false;
        String temp = "";
        for (int i = 0; i < input.length(); i++) {
            // if space is not found then it is still the command name
            if (input.charAt(i) != ' ' && input.charAt(i) != '"')
                temp += input.charAt(i);
            // if space is found and foundSpace bool is not true yet then the command name has ended
            if (input.charAt(i) == ' ' && !foundSpace) {
                foundSpace = true;
                commandName = temp;
                temp = "";
            } else if (input.charAt(i) == ' ' && foundSpace) {
                args.add(temp);
                temp = "";
            }
        }
    }

    public static void combineToOneString(List<String> args) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            if (i != args.size() - 1)
                s.append(args.get(i)).append(' ');
            else
                s.append(args.get(i));
        }
        args.clear();
        args.add(s.toString());
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getArgs() {
        return args;
    }
}
