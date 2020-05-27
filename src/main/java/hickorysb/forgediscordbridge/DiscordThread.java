package hickorysb.forgediscordbridge;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import hickorysb.forgediscordbridge.config.CommandConfig;
import hickorysb.forgediscordbridge.config.Configuration;
import hickorysb.forgediscordbridge.config.GroupConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class DiscordThread implements Runnable {
    public static GatewayDiscordClient client;

    @Override
    public void run() {
        if(!Configuration.mainConfig.bot_token.equals("REPLACE_THIS") && !Configuration.mainConfig.bot_token.equals("")) {
            try {
                client = DiscordClient.create(Configuration.mainConfig.bot_token).gateway().setEnabledIntents(IntentSet.of(Intent.GUILD_MESSAGES, Intent.GUILD_MEMBERS)).login().block();
                assert client != null : "Client came out null.";
                ForgeDiscordBridge.logger.info("Logged in as " + Objects.requireNonNull(client.getSelf().block()).getUsername() + ".");
                ForgeDiscordBridge.mdBridge.update();
                client.on(MessageCreateEvent.class).subscribe(event -> {
                    final Message message = event.getMessage();
                    final Member member = message.getAuthorAsMember().block();
                    final MessageChannel channel = message.getChannel().block();
                    assert channel != null : "Channel was missing.";
                    if(Configuration.mainConfig.channelIDs.contains(channel.getId().asString())) {
                        assert message.getAuthor().isPresent() : "Author was missing.";
                        if((Configuration.mainConfig.bridge_bots || !message.getAuthor().get().isBot()) && !message.getContent().startsWith(Configuration.mainConfig.command_prefix)) {
                            assert member != null : "Member was missing.";
                            String nickname = member.getNickname().orElse(message.getAuthor().get().getUsername());
                            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(new TextComponentString("[" + nickname + "] " + message.getContent()));
                        } else if((Configuration.mainConfig.bridge_bots || !message.getAuthor().get().isBot()) && message.getContent().startsWith(Configuration.mainConfig.command_prefix)) {
                            CommandConfig command = null;
                            for(CommandConfig c : Configuration.commandsConfig.commands) {
                                if(c.name.equals(message.getContent().split(" ")[0].replace(Configuration.mainConfig.command_prefix, "")) || c.aliases.contains(message.getContent().split(" ")[0].replace(Configuration.mainConfig.command_prefix, ""))) {
                                    command = c;
                                    break;
                                }
                            }
                            if(command != null) {
                                boolean canRun = false;
                                if(Configuration.mainConfig.enable_groups) {
                                    if(command.groupNames.size() > 0) {
                                        for(String gn : command.groupNames) {
                                            for(GroupConfig gc : Configuration.groupsConfig.groups) {
                                                if(gn.equals(gc.name)) {
                                                    for(String role : gc.roles) {
                                                        Flux<Role> c;
                                                        if(role.startsWith("role:")) {
                                                            assert member != null;
                                                            c = member.getRoles().filter(r -> r.getName().equals(role.replace("role:", "")));
                                                        } else {
                                                            assert member != null;
                                                            c = member.getRoles().filter(r -> r.getId().asString().equals(role.replace("id:", "")));
                                                        }
                                                        if (Objects.requireNonNull(c.count().block()) > 0) {
                                                            canRun = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    for(String role : command.roles) {
                                        Flux<Role> c;
                                        if(role.startsWith("role:")) {
                                            assert member != null;
                                            c = member.getRoles().filter(r -> r.getName().equals(role.replace("role:", "")));
                                        } else {
                                            assert member != null;
                                            c = member.getRoles().filter(r -> r.getId().asString().equals(role.replace("id:", "")));
                                        }
                                        if (Objects.requireNonNull(c.count().block()) > 0) {
                                            canRun = true;
                                            break;
                                        }
                                    }
                                }
                                if (canRun) {
                                    FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(new DiscordCommandSender(channel, message.getAuthor().get(), command), command.execute);
                                }
                            }
                        }
                    }
                });
                client.onDisconnect().block();
            } catch(Exception e) {
                ForgeDiscordBridge.logger.error("Error logging into your bot. This could be caused by:");
                ForgeDiscordBridge.logger.error("- An invalid token (Most likely)");
                ForgeDiscordBridge.logger.error("- Discord is down");
                ForgeDiscordBridge.logger.error("- The server does not have an internet connection");
                ForgeDiscordBridge.logger.error("- Your bot is banned");
            }
        }
    }

    public static void end() {
        if(client != null) {
            client.logout().block();
        }
    }
}
