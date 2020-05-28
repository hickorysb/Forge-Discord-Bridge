package hickorysb.forgediscordbridge;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.*;
import discord4j.discordjson.json.ChannelData;
import discord4j.rest.util.Snowflake;
import hickorysb.forgediscordbridge.config.*;
import hickorysb.forgediscordbridge.DiscordThread;
import ibxm.Player;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import hickorysb.forgediscordbridge.DiscordThread;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MinecraftDiscordBridge {
    private MessageChannel currentChannel;
    public ArrayList<GuildMessageChannel> channels;
    private ArrayList<String> channelids;
    private ArrayList<Member> members;
    //private String buffer;
    //public static MinecraftDiscordBridge instance;


    public void update(){
        channelids = Configuration.mainConfig.channelIDs;
        channels = new ArrayList<GuildMessageChannel>();
        try{
            for(String x : channelids){
                channels.add(DiscordThread.client.getChannelById(Snowflake.of(x)).cast(GuildMessageChannel.class).block());
            }
        }catch(Exception e){
            ForgeDiscordBridge.logger.error("Error getting valid channels from bot.");
        }
    }

    public MinecraftDiscordBridge(){
        channelids = Configuration.mainConfig.channelIDs;
        //update();
    }

    public void serverStartupMessage(){
        try{
            for(GuildMessageChannel x : channels){
                x.createMessage("Server started!").block();
            }
        }catch(Exception e){
            ForgeDiscordBridge.logger.error("Server startup error");
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onIngameChat(ServerChatEvent event){
        if(event.isCanceled() || event.getPlayer() == null) return;
        String finalMessage = event.getMessage();
        ForgeDiscordBridge.logger.info("Received message");
        try{
            for(GuildMessageChannel x : channels){
                members = new ArrayList<Member>(Objects.requireNonNull(x.getMembers().collectList().block()));
            }
        }catch(Exception e){
            ForgeDiscordBridge.logger.error("Error with bot(any): \n- Invalid token (most likely)\n- Discord is down\n- Server has no internet connection\n- Your bot is banned");
        }


        if(finalMessage.contains("@")){
            for(Member mem : members){
                String displayName = mem.getDisplayName();
                finalMessage = finalMessage.replaceAll("@" + displayName, mem.getNicknameMention());
            }
        }

        try{
            for(GuildMessageChannel x : channels){
                x.createMessage("**[" + event.getPlayer().getName() + "]**" + finalMessage).block();
            }
        }catch(Exception e){
            //ForgeDiscordBridge.logger.error("Error with bot(any): \n- Invalid token (most likely)\n- Discord is down\n- Server has no internet connection\n- Your bot is banned");
            ForgeDiscordBridge.logger.error("Error when sending bot message.");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(event.isCanceled() || event.player == null) return;

        try{

            for(GuildMessageChannel x : channels){
                x.createMessage("**" + event.player.getName() + "** just joined the server!").block();
            }
        }catch(Exception e){
            //ForgeDiscordBridge.logger.error("Error with bot(any): \n- Invalid token (most likely)\n- Discord is down\n- Server has no internet connection\n- Your bot is banned");
            ForgeDiscordBridge.logger.error("Error when sending bot message.");
        }


    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        if(event.isCanceled() || event.player == null) return;

        try{
            for(GuildMessageChannel x : channels){
                x.createMessage("**" + event.player.getName() + "** just left the server!").block();
            }
        }catch(Exception e){
            //ForgeDiscordBridge.logger.error("Error with bot(any): \n- Invalid token (most likely)\n- Discord is down\n- Server has no internet connection\n- Your bot is banned");
            ForgeDiscordBridge.logger.error("Error when sending bot message.");
        }


    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerAdvancement(AdvancementEvent event){
        String message = "";
        EntityPlayer player = event.getEntityPlayer();
        if(player instanceof EntityPlayerMP){
            Advancement advance = event.getAdvancement();
            DisplayInfo info = advance.getDisplay();
            if(info == null || !info.shouldAnnounceToChat()){
                return;
            }else{
                message = new String("**" + player.getName() + "** just gained the achievement **" + info.getTitle().getUnformattedText().toString() + "**\n*" + info.getDescription().getUnformattedText().toString() + "*");
            }
        }
        if(message == ""){
            return;
        }
        try{
            for(GuildMessageChannel x : channels){
                x.createMessage(message).block();
            }
        }catch(Exception e){
            ForgeDiscordBridge.logger.error("Error when sending bot message.");
        }

    }



}
