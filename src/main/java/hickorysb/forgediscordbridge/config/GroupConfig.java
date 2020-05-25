package hickorysb.forgediscordbridge.config;

import com.google.gson.annotations.Since;

import java.util.ArrayList;

public class GroupConfig {
    @Since(1.0)
    public String name;
    @Since(1.0)
    public ArrayList<String> roles;
}
