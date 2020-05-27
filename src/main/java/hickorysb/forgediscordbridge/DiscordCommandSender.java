package hickorysb.forgediscordbridge;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.authlib.GameProfile;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import hickorysb.forgediscordbridge.config.CommandConfig;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SuppressWarnings("EntityConstructor")
@ParametersAreNonnullByDefault
public class DiscordCommandSender extends FakePlayer {
    private static final UUID playerUUID = UUID.fromString("913c871b-1ada-4c4b-a4be-6f3d71f73236");
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                    .setNameFormat(DiscordCommandSender.class.getSimpleName())
                    .setDaemon(true)
                    .build()
    );

    private final MessageChannel channel;
    private final Batcher<String> batcher = new Batcher<>(this::sendBatch, 100, 10, executor);
    private final CommandConfig command;


    public DiscordCommandSender(MessageChannel channel, User user, CommandConfig command) {
        super(FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0], new GameProfile(playerUUID, "@" + user.getUsername()));
        this.channel = channel;
        this.command = command;
    }

    @SuppressWarnings("unused")
    public DiscordCommandSender(WorldServer world, MessageChannel channel, String name, CommandConfig command) {
        super(world, new GameProfile(playerUUID, "@" + name));
        this.channel = channel;
        this.command = command;
    }

    private static String textComponentToDiscordMessage(ITextComponent component) {
        return Patterns.minecraftCodePattern.matcher(
                component.getUnformattedText()
        ).replaceAll("");
    }

    @Override
    public boolean canUseCommand(int i, String s) {
        return true;
    }

    @Override
    public void sendMessage(ITextComponent component) {

        Preconditions.checkNotNull(component);
        batcher.queue(textComponentToDiscordMessage(component));
    }

    @Override
    public void sendStatusMessage(ITextComponent component, boolean actionBar) {

        Preconditions.checkNotNull(component);
        batcher.queue(textComponentToDiscordMessage(component));
    }

    private void sendBatch(List<String> messages) {
        final int numMessages = messages.size();
        try {
            this.channel.createMessage(Joiner.on("\n").join(messages)).block();
            ForgeDiscordBridge.logger.info(Joiner.on("\n").join(messages));
        } catch (Exception e) {
            ForgeDiscordBridge.logger.info(
                    "Exception sending " + numMessages + " messages to Discord:\n"
                            + e.getStackTrace()
            );
        }
    }
}
