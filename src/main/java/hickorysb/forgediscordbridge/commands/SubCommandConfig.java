package hickorysb.forgediscordbridge.commands;

import hickorysb.forgediscordbridge.DiscordThread;
import hickorysb.forgediscordbridge.ForgeDiscordBridge;
import hickorysb.forgediscordbridge.config.Configuration;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

public class SubCommandConfig extends CommandBase {

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/discordbridge config <reload|load|save>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(this.getUsage(sender)));
            return;
        }

        String arg = args[0];
        switch (arg) {
            case "load":
            case "reload":
                String oldToken = Configuration.mainConfig.bot_token;

                Configuration.loadGroupsConfig();
                Configuration.loadCommandsConfig();
                Configuration.loadMainConfig();
                sender.sendMessage(new TextComponentString("Config reloaded"));

                if (DiscordThread.client != null) {
                    DiscordThread.end();
                    Runnable run = new DiscordThread();
                    ForgeDiscordBridge.thread = new Thread(run);
                    ForgeDiscordBridge.thread.start();
                    sender.sendMessage(new TextComponentString("Connected Bot"));
                } else if (!oldToken.equals(Configuration.mainConfig.bot_token)) {
                    DiscordThread.end();
                    Runnable run = new DiscordThread();
                    ForgeDiscordBridge.thread = new Thread(run);
                    ForgeDiscordBridge.thread.start();
                    sender.sendMessage(new TextComponentString("Reconnected Bot"));
                }

                break;
            case "save":
                Configuration.saveCommandsConfig();
                Configuration.saveGroupsConfig();
                Configuration.saveMainConfig();

                sender.sendMessage(new TextComponentString("Config saved"));
                break;
            default:
                sender.sendMessage(new TextComponentString(this.getUsage(sender)));
                break;
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(4, "discordbridge." + getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, "reload", "save", "load");
    }
}
