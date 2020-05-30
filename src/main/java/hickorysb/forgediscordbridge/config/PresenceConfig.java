package hickorysb.forgediscordbridge.config;

import com.google.gson.annotations.Since;

public class PresenceConfig {
    @Since(1.0)
    public boolean enabled;
    @Since(1.0)
    public String noPlayersMessage = "with no one.";
    @Since(1.0)
    public String onePlayerMessage = "with ${USER}.";
    @Since(1.0)
    public String morePlayersMessage = "with ${PLAYERCOUNT} players.";
}
