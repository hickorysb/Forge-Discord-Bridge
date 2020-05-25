package hickorysb.forgediscordbridge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import hickorysb.forgediscordbridge.ForgeDiscordBridge;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LoadJSONConfigs {
    public static MainWrapper mainConfig;
    public static CommandsWrapper commandsConfig;
    public static GroupsWrapper groupsConfig;
    public static File main;
    public static File groups;
    public static File commands;

    public static void load(String configPath) {
        File configDirectory = new File(configPath);

        configDirectory.mkdirs();

        groups = new File(configPath + File.separator + "FDB" + File.separator + "groups.json");
        commands = new File(configPath + File.separator + "FDB" + File.separator + "commands.json");
        main = new File(configPath + File.separator + "FDB" + File.separator + "main.json");

        loadMainConfig();
        loadCommandsConfig();
        loadGroupsConfig();
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .setVersion(1.0)
                .setPrettyPrinting()
                .create();
    }

    public static void loadCommandsConfig() {
        if(commands == null) {
            return;
        }

        Gson gson = createGson();

        if(!commands.exists()) {
            commandsConfig = new CommandsWrapper();
            commandsConfig.fillFields();
            saveCommandsConfig();
        } else {
            FileReader reader = null;
            try {
                reader = new FileReader(commands);
                commandsConfig = gson.fromJson(reader, CommandsWrapper.class);
                if(commandsConfig == null) {
                    commandsConfig = new CommandsWrapper();
                }
                commandsConfig.fillFields();
            } catch (Exception e) {
                if(e instanceof JsonSyntaxException) {
                    ForgeDiscordBridge.logger.error("Config syntax was invalid. Please check your config using an online JSON validator.");
                }

                e.printStackTrace();

                if(commandsConfig == null) {
                    commandsConfig = new CommandsWrapper();
                    commandsConfig.fillFields();
                }
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

    public static void saveCommandsConfig() {
        Gson gson = createGson();

        try {
            FileWriter writer = new FileWriter(commands);
            writer.write(gson.toJson(commandsConfig));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMainConfig() {
        if(main == null) {
            return;
        }

        Gson gson = createGson();

        if(!main.exists()) {
            mainConfig = new MainWrapper();
            mainConfig.fillFields();
            saveMainConfig();
        } else {
            FileReader reader = null;
            try {
                reader = new FileReader(main);
                mainConfig = gson.fromJson(reader, MainWrapper.class);
                if(mainConfig == null) {
                    mainConfig = new MainWrapper();
                }
                mainConfig.fillFields();
            } catch (Exception e) {
                if(e instanceof JsonSyntaxException) {
                    ForgeDiscordBridge.logger.error("Config syntax was invalid. Please check your config using an online JSON validator.");
                }

                e.printStackTrace();

                if(mainConfig == null) {
                    mainConfig = new MainWrapper();
                    mainConfig.fillFields();
                }
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

    public static void saveMainConfig() {
        Gson gson = createGson();

        try {
            FileWriter writer = new FileWriter(main);
            writer.write(gson.toJson(mainConfig));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGroupsConfig() {
        if(groups == null) {
            return;
        }

        Gson gson = createGson();

        if(!groups.exists()) {
            groupsConfig = new GroupsWrapper();
            groupsConfig.fillFields();
            saveGroupsConfig();
        } else {
            FileReader reader = null;
            try {
                reader = new FileReader(groups);
                groupsConfig = gson.fromJson(reader, GroupsWrapper.class);
                if(groupsConfig == null) {
                    groupsConfig = new GroupsWrapper();
                }
                groupsConfig.fillFields();
            } catch (Exception e) {
                if(e instanceof JsonSyntaxException) {
                    ForgeDiscordBridge.logger.error("Config syntax was invalid. Please check your config using an online JSON validator.");
                }

                e.printStackTrace();

                if(groupsConfig == null) {
                    groupsConfig = new GroupsWrapper();
                    groupsConfig.fillFields();
                }
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

    public static void saveGroupsConfig() {
        Gson gson = createGson();

        try {
            FileWriter writer = new FileWriter(groups);
            writer.write(gson.toJson(groupsConfig));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
