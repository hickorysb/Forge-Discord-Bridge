package hickorysb.forgediscordbridge.config;

import com.google.gson.annotations.Since;

import java.util.ArrayList;

public class GroupsWrapper {
    @Since(1.0)
    public ArrayList<GroupConfig> groups;

    public void fillFields() {
        if(this.groups == null) {
            this.groups = new ArrayList<>();
            GroupConfig list = new GroupConfig();
            list.roles = new ArrayList<>();
            list.roles.add("role:op");
            list.roles.add("role:staff");
            list.name = "op";
            groups.add(list);
        }

        for(GroupConfig group : groups) {
            group.fillFields();
        }
    }
}
