package hickorysb.forgediscordbridge.config;

import com.google.gson.annotations.Since;

import java.util.ArrayList;

public class CommandsWrapper {
    @Since(1.0)
    public ArrayList<CommandConfig> commands;

    public void fillFields() {
        if(this.commands == null) {
            this.commands = new ArrayList<>();
            CommandConfig list = new CommandConfig();
            list.aliases = new ArrayList<>();
            list.execute = "list";
            list.name = "list";
            list.aliases.add("players");
            list.aliases.add("listplayers");
            commands.add(list);
        }
    }
}
