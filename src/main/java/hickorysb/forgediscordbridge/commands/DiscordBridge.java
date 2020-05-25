package hickorysb.forgediscordbridge.commands;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DiscordBridge extends CommandTreeBase {
    public DiscordBridge() {
        this.addSubcommand(new SubCommandConfig());
    }

    @Override
    public String getName() {
        return "discordbridge";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/discordbridge <config>";
    }
}
