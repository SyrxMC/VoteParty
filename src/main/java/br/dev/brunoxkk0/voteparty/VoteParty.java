package br.dev.brunoxkk0.voteparty;

import br.com.brunoxkk0.helper.ConfigAPI;
import br.com.brunoxkk0.helper.LoggerHelper;
import br.dev.brunoxkk0.voteparty.core.BarHandlerThread;
import br.dev.brunoxkk0.voteparty.core.VoteHandler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteParty extends JavaPlugin{

    private static VoteParty instance;

    private final ConfigAPI configAPI = new ConfigAPI(this, "config.yml",true);

    private LoggerHelper loggerHelper;
    private BarHandlerThread barHandlerThread;
    private VoteHandler voteHandler;

    public LoggerHelper getLoggerHelper() {
        return loggerHelper;
    }

    public static VoteParty getInstance() {
        return instance;
    }

    public ConfigAPI getConfigAPI() {
        return configAPI;
    }

    public BarHandlerThread getBarHandlerThread() {
        return barHandlerThread;
    }

    public VoteHandler getVoteHandler() {
        return voteHandler;
    }

    @Override
    public void onEnable() {

        instance = this;

        loggerHelper = new LoggerHelper(this);

        loggerHelper.info("Loading VoteHandler...");

        voteHandler = new VoteHandler();
        voteHandler.setup();

        loggerHelper.info("Registering events...");
        Bukkit.getPluginManager().registerEvents(voteHandler, this);

        loggerHelper.info("Loading BarHandlerThread...");

        barHandlerThread = new BarHandlerThread();
        barHandlerThread.setup();

    }

    @Override
    public void onDisable() {

        loggerHelper.info("Shutting down systems and saving data.");

        voteHandler.save();

        if(!barHandlerThread.getWarn().isInterrupted())
            barHandlerThread.getWarn().interrupt();

    }
}
