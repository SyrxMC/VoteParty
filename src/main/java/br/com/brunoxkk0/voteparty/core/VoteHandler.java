package br.com.brunoxkk0.voteparty.core;

import br.com.brunoxkk0.voteparty.VoteParty;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class VoteHandler implements Listener {

    private static int currentVoteCount;
    private static int requiredVotesCount;
    private static List<String> rewardCommands;
    private static String rewardMessage;

    public void setup(){

        currentVoteCount =  VoteParty.getInstance().getConfigAPI().getInt("votes_current");
        requiredVotesCount = VoteParty.getInstance().getConfigAPI().getInt("votes_required");
        rewardCommands = VoteParty.getInstance().getConfigAPI().getStringList("reward_commands");
        rewardMessage = VoteParty.getInstance().getConfigAPI().getString("reward_message");

    }

    public static float getVotePercentage(CounterType type) {
        if (type == CounterType.INVERTED) {
            return Math.min(100F, Math.max((100F - (currentVoteCount * 100F) / requiredVotesCount),0F));
        }
        return Math.min(100F, Math.max((currentVoteCount * 100F / requiredVotesCount),0F));
    }

    public synchronized void process(Player player){

        currentVoteCount++;

        if(currentVoteCount >= requiredVotesCount){
            currentVoteCount = 0;

            Bukkit.broadcastMessage(rewardMessage.replace("&","\u00a7"));

            for (String cmd : rewardCommands){
                rewardAll(cmd);
            }

        }

        if(player != null){
            VoteParty.getInstance().getBarHandlerThread().processBar(player);
        }
    }


    public void rewardAll(String command){

        for(Player player : Bukkit.getOnlinePlayers()){

            if(command.startsWith("/")){
                command  = command.substring(1);
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%p", player.getName()));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
        }

    }

    public void save(){
        VoteParty.getInstance().getConfigAPI().setValue("votes_current", currentVoteCount);
        VoteParty.getInstance().getConfigAPI().save();
    }

    public static int getCurrentVoteCount() {
        return currentVoteCount;
    }

    public static int getRequiredVotesCount() {
        return requiredVotesCount;
    }

    public static int getMissingVotesCount(){
        return (requiredVotesCount - currentVoteCount);
    }


    @EventHandler
    public synchronized void onVote(VotifierEvent event){

        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getVote().getUsername());

        if(player != null && player.hasPlayedBefore()){

            if(player.isOnline()){
                process(player.getPlayer());
                return;
            }

            VoteParty.getInstance().getLoggerHelper().info("The player" + event.getVote().getUsername() + " had vote on this server but are offline.");
        }

        VoteParty.getInstance().getLoggerHelper().info("The player" + event.getVote().getUsername() + " never had played in this server before.");
    }

}
