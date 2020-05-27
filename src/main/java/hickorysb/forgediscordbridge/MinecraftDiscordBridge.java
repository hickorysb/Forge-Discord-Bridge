package hickorysb.forgediscordbridge;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ChannelData;
import discord4j.rest.util.Snowflake;
import hickorysb.forgediscordbridge.config.*;
import hickorysb.forgediscordbridge.DiscordThread;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import hickorysb.forgediscordbridge.DiscordThread;
import reactor.core.publisher.Mono;
import sun.security.krb5.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MinecraftDiscordBridge {
    private MessageChannel currentChannel;
    private ArrayList<String> channelids;
    private String buffer;
    public static MinecraftDiscordBridge instance = new MinecraftDiscordBridge();

    public MinecraftDiscordBridge(){
        channelids = Configuration.mainConfig.channelIDs;
    }

    @SubscribeEvent
    public void onIngameChat(ServerChatEvent event){
        if(event.isCanceled() || event.getPlayer() == null) return;

        String finalMessage = event.getMessage();



        for(String x : channelids){
            try {

                ArrayList<Member> members = new ArrayList<Member>(Objects.requireNonNull(DiscordThread.client.getChannelById(Snowflake.of(x)).cast(GuildMessageChannel.class).block().getMembers().collectList().block()));

                if(finalMessage.contains("@")){
                    System.out.println("Found it");
                    for(Member mem : members){
                        if(finalMessage.contains(new String("@" + mem.getDisplayName()))) {
                            System.out.println("Username: " + mem.getDisplayName() + "\nMemNick: " + mem.getNicknameMention());
                            finalMessage = finalMessage.replace(new String("@" + mem.getDisplayName()), mem.getNicknameMention());
                        }
                    }
                }

                currentChannel = DiscordThread.client.getChannelById(Snowflake.of(x)).cast(MessageChannel.class).block();
                buffer = "**[" + event.getPlayer().getName() + "]** " + finalMessage;
                currentChannel.createMessage(buffer).block();
                buffer = "";
            }catch(Exception e){
                ForgeDiscordBridge.logger.error("Error with bot\n");
                ForgeDiscordBridge.logger.error("Error message(s): \n" + e.getMessage());
            }

        }


    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(event.isCanceled() || event.player == null) return;
        //System.out.println("User joined");
        Configuration.loadMainConfig();

        for(String x : channelids) {
            System.out.println("Printing to channel: " + x + "\n");
            Snowflake flake = Snowflake.of(x);
            try {
                currentChannel = DiscordThread.client.getChannelById(flake).cast(MessageChannel.class).block();
                buffer = "**" + event.player.getName() + "** just joined the server!";
                currentChannel.createMessage(buffer).block();
                buffer = "";
            } catch (Exception e) {
                ForgeDiscordBridge.logger.error("Error with bot\n");
                ForgeDiscordBridge.logger.error("Error message(s): \n" + e.getMessage());
            }
        }

    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        if(event.isCanceled() || event.player == null) return;
        //System.out.println("User left");
        Configuration.loadMainConfig();

        for(String x : channelids) {
            Snowflake flake = Snowflake.of(x);
            try {
                currentChannel = DiscordThread.client.getChannelById(flake).cast(MessageChannel.class).block();
                buffer = "**" + event.player.getName() + "** just left the server!";
                currentChannel.createMessage(buffer).block();
                buffer = "";
            } catch (Exception e) {
                ForgeDiscordBridge.logger.error("Error with bot\n");
                ForgeDiscordBridge.logger.error("Error message(s): \n" + e.getMessage());
            }
        }


    }

}
