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
    @Since(1.0)
    public boolean bridge_bots;
    @Since(1.0)
    public String command_prefix;
    @Since(1.0)
    public boolean bridge_commands;
    @Since(1.0)
    public boolean bridge_say_me;
    @Since(1.0)
    public boolean bridge_achievements;
    @Since(1.0)
    public boolean bridge_join_leave;
    @Since(1.0)
    public boolean bridge_server_start_stop;
    @Since(1.0)
    public boolean enable_ftbutilities_integration;

    public void fillFields() {
        // TODO: There has to be a better way of doing fillFields()
        if(this.bot_token == null) {
            this.bot_token = "REPLACE_THIS";
        }
        if(this.channelIDs == null) {
            this.channelIDs = new ArrayList<>();
        }
        if(this.command_prefix == null) {
            this.command_prefix = "/";
        }

        /*if(this.bridge_join_leave == null){
            this.bridge_join_leave = true;
        }
        if(this.bridge_server_start_stop == null){
            this.bridge_server_start_stop = true;
        }
        if(this.bridge_say_me == null){
            this.bridge_say_me = true;
        }
        if(this.bridge_achievements == null){
            this.bridge_achievements = true;
        }
        if(this.bridge_commands == null){
            this.bridge_commands = true;
        }*/


    }
}
