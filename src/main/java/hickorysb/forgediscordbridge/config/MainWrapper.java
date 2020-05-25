package hickorysb.forgediscordbridge.config;

import com.google.gson.annotations.Since;

import java.util.ArrayList;

public class MainWrapper {
    @Since(1.0)
    public String bot_token;
    @Since(1.0)
    public ArrayList<String> channelIDs;
    @Since(1.0)
    public boolean enable_commands;
    @Since(1.0)
    public boolean enable_groups;

    public void fillFields() {
        if(this.bot_token == null) {
            this.bot_token = "REPLACE_THIS";
        }
        if(this.channelIDs == null) {
            this.channelIDs = new ArrayList<>();
        }
    }
}
