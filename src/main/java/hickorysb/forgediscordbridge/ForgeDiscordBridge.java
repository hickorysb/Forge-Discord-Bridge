package hickorysb.forgediscordbridge;

import discord4j.core.object.entity.channel.GuildMessageChannel;
import hickorysb.forgediscordbridge.commands.DiscordBridge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import hickorysb.forgediscordbridge.config.Configuration;
import hickorysb.forgediscordbridge.MinecraftDiscordBridge;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ForgeDiscordBridge.MODID, name = ForgeDiscordBridge.NAME, version = ForgeDiscordBridge.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class ForgeDiscordBridge
{
    public static final String MODID = "forgediscordbridge";
    public static final String NAME = "Forge Discord Bridge";
    public static final String VERSION = "1.0.00";
    public static Logger logger;
    public static Thread thread;
    public static MinecraftDiscordBridge mdBridge;

    private static Boolean hasInitializedDiscord = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration.load(event.getModConfigurationDirectory().getAbsolutePath());
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("Connecting the people.");
        Configuration.loadMainConfig();
        Configuration.loadCommandsConfig();
        Configuration.loadGroupsConfig();
    }

    @EventHandler
    public void onServerShutdown(FMLServerStoppingEvent event){
        if(!Configuration.mainConfig.bridge_server_start_stop){
            return;
        }
        try{
            for(GuildMessageChannel x : mdBridge.channels){
                x.createMessage("Server stopped!").block();
            }
        }catch(Exception e){
            ForgeDiscordBridge.logger.error("[ServerShutdown]Server shutdown error"); // lol
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new DiscordBridge());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if(!hasInitializedDiscord) {
            hasInitializedDiscord = true;
            Runnable runnable = new DiscordThread();
            thread = new Thread(runnable);
            thread.start();
            mdBridge = new MinecraftDiscordBridge();
            MinecraftForge.EVENT_BUS.register(mdBridge);
        }
    }

    @SubscribeEvent
    public void onConfigChangedEvent(OnConfigChangedEvent event) {
        if(event.getModID().equals(MODID)) {
            Configuration.loadMainConfig();
            Configuration.loadCommandsConfig();
            Configuration.loadGroupsConfig();
        }
    }
}
