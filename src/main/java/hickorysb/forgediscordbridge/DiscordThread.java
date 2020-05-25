package hickorysb.forgediscordbridge;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import hickorysb.forgediscordbridge.config.LoadJSONConfigs;

public class DiscordThread implements Runnable {
    public static GatewayDiscordClient client;

    @Override
    public void run() {
        if(!LoadJSONConfigs.mainConfig.bot_token.equals("REPLACE_THIS") && !LoadJSONConfigs.mainConfig.bot_token.equals("")) {
            try {
                ForgeDiscordBridge.logger.info("Beginning Discord login.");
                client = DiscordClient.create(LoadJSONConfigs.mainConfig.bot_token).gateway().setEnabledIntents(IntentSet.of(Intent.GUILD_MESSAGES)).login().block();
                ForgeDiscordBridge.logger.info("Logged in as " + client.getSelf().block().getUsername() + ".");
                client.onDisconnect().block();
            } catch(Exception e) {
                ForgeDiscordBridge.logger.info("Error logging into your bot. This could be caused by:");
                ForgeDiscordBridge.logger.info("- An invalid token (Most likely)");
                ForgeDiscordBridge.logger.info("- Discord is down");
                ForgeDiscordBridge.logger.info("- The server does not have an internet connection");
                ForgeDiscordBridge.logger.info("- Your bot is banned");
            }
        }
    }

    public static void end() {
        if(client != null) {
            client.logout().block();
        }
    }
}
