package hickorysb.forgediscordbridge;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import hickorysb.forgediscordbridge.config.CommandConfig;
import hickorysb.forgediscordbridge.config.Configuration;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class DiscordThread implements Runnable {
    public static GatewayDiscordClient client;

    @Override
    public void run() {
        if(!Configuration.mainConfig.bot_token.equals("REPLACE_THIS") && !Configuration.mainConfig.bot_token.equals("")) {
            try {
                client = DiscordClient.create(Configuration.mainConfig.bot_token).gateway().setEnabledIntents(IntentSet.of(Intent.GUILD_MESSAGES)).login().block();
                ForgeDiscordBridge.logger.info("Logged in as " + client.getSelf().block().getUsername() + ".");
                client.on(MessageCreateEvent.class).subscribe(event -> {
                    final Message message = event.getMessage();
                    final MessageChannel channel = message.getChannel().block();
                    if(Configuration.mainConfig.channelIDs.contains(channel.getId().asString())) {
                        if((Configuration.mainConfig.bridge_bots || !message.getAuthor().get().isBot()) && !message.getContent().startsWith(Configuration.mainConfig.command_prefix)) {
                            String nickname = message.getAuthorAsMember().block().getNickname().orElse(message.getAuthor().get().getUsername());
                            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(new TextComponentString("[" + nickname + "] " + message.getContent()));
                        } else if((Configuration.mainConfig.bridge_bots || !message.getAuthor().get().isBot()) && message.getContent().startsWith(Configuration.mainConfig.command_prefix)) {
                            ForgeDiscordBridge.logger.info("Received Discord command");
                            CommandConfig command = null;
                            for(CommandConfig c : Configuration.commandsConfig.commands) {
                                if(c.name.equals(message.getContent().split(" ")[0].replace("/", "")) || c.aliases.contains(message.getContent().split(" ")[0].replace("/", ""))) {
                                    ForgeDiscordBridge.logger.info("Found command: " + c.name);
                                    command = c;
                                    break;
                                }
                            }
                            if(command != null) {
                                ForgeDiscordBridge.logger.info("send command");
                                FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(new DiscordCommandSender(channel, message.getAuthor().get(), command), command.execute);
                            }
                        }
                    }
                });
                client.onDisconnect().block();
                ForgeDiscordBridge.logger.info("Exiting");
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
