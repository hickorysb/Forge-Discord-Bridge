package hickorysb.forgediscordbridge.config;

import com.google.gson.annotations.Since;

import java.util.ArrayList;

public class CommandConfig {
    @Since(1.0)
    public String name;
    @Since(1.0)
    public String execute;
    @Since(1.0)
    public ArrayList<String> aliases;
    @Since(1.0)
    public ArrayList<String> groupNames;
    @Since(1.0)
    public ArrayList<String> roles;

    public void fillFields() {
        if(this.name == null) {
            this.name = "";
        }
        if(this.execute == null) {
            this.execute = "";
        }
        if(this.aliases == null) {
            this.aliases = new ArrayList<>();
        }
        if(this.groupNames == null) {
            this.groupNames = new ArrayList<>();
        }
        if(this.roles == null) {
            this.roles = new ArrayList<>();
        }
    }
}
