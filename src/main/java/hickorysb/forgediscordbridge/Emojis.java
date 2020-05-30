package hickorysb.forgediscordbridge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Emojis {
    public static final Map<String, String> minecraftToDiscordEmotes = new HashMap<>();
    public static final Map<String, String> discordToMinecraftEmotes = new HashMap<>();

    static {
        minecraftToDiscordEmotes.put(":)", ":slight_smile:");
        minecraftToDiscordEmotes.put(":D", ":smile:");
        minecraftToDiscordEmotes.put(";)", ":wink:");
        minecraftToDiscordEmotes.put(";D", ":wink:");
        minecraftToDiscordEmotes.put("xD", ":laughing:");
        minecraftToDiscordEmotes.put("XD", ":laughing:");
        minecraftToDiscordEmotes.put(":p", ":stuck_out_tongue:");
        minecraftToDiscordEmotes.put(":P", ":stuck_out_tongue:");
        minecraftToDiscordEmotes.put(";p", ":stuck_out_tongue_winking_eye:");
        minecraftToDiscordEmotes.put(";P", ":stuck_out_tongue_winking_eye:");
        minecraftToDiscordEmotes.put("xp", ":stuck_out_tongue_closed_eyes:");
        minecraftToDiscordEmotes.put("xP", ":stuck_out_tongue_closed_eyes:");
        minecraftToDiscordEmotes.put("Xp", ":stuck_out_tongue_closed_eyes:");
        minecraftToDiscordEmotes.put("XP", ":stuck_out_tongue_closed_eyes:");
        minecraftToDiscordEmotes.put(":O", ":open_mouth:");
        minecraftToDiscordEmotes.put(":o", ":open_mouth:");
        minecraftToDiscordEmotes.put("xO", ":dizzy_face:");
        minecraftToDiscordEmotes.put("XO", ":dizzy_face:");
        minecraftToDiscordEmotes.put(":|", ":neutral_face:");
        minecraftToDiscordEmotes.put("B)", ":sunglasses:");
        minecraftToDiscordEmotes.put(":*", ":kissing:");
        minecraftToDiscordEmotes.put(";.;", ":sob:");
        minecraftToDiscordEmotes.put(";_;", ":cry:");
        minecraftToDiscordEmotes.put(":(", ":frowning:");
        minecraftToDiscordEmotes.put("<3", ":heart:");
        minecraftToDiscordEmotes.put("</3", ":broken_heart:");
        minecraftToDiscordEmotes.put("(y)", ":thumbsup:");
        minecraftToDiscordEmotes.put("(Y)", ":thumbsup:");
        minecraftToDiscordEmotes.put("(yes)", ":thumbsup:");
        minecraftToDiscordEmotes.put("(n)", ":thumbsdown:");
        minecraftToDiscordEmotes.put("(N)", ":thumbsdown:");
        minecraftToDiscordEmotes.put("(no)", ":thumbsdown:");
        minecraftToDiscordEmotes.put("(ok)", ":ok_hand:");

        Set<Map.Entry<String, String>> entries = minecraftToDiscordEmotes.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            discordToMinecraftEmotes.put(entry.getValue(), entry.getKey());
        }
    }
}