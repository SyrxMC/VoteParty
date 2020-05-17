package br.com.brunoxkk0.voteparty.core;

import br.com.brunoxkk0.voteparty.VoteParty;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import static java.lang.Thread.sleep;

public class BarHandlerThread {

    private static Thread warn;
    private static String barMessage;
    private static BarColor color;
    private static BarStyle style;
    private static CounterType counterType;
    private static int bar_warn_timer;

    public Thread getWarn() {
        return warn;
    }

    public void setup(){

        barMessage = VoteParty.getInstance().getConfigAPI().getString("bar_format");
        counterType = CounterType.getByName(VoteParty.getInstance().getConfigAPI().getString("counter_type"));
        bar_warn_timer = VoteParty.getInstance().getConfigAPI().getInt("bar_warn_timer");

        loadColor(VoteParty.getInstance().getConfigAPI().getString("bar_color"));
        loadStyle(VoteParty.getInstance().getConfigAPI().getString("bar_style"));

        warn = new Thread(() -> {

            BossBar bar;

            while (!warn.isInterrupted()){

                bar = Bukkit.createBossBar(

                        barMessage
                        .replace("%m",""+VoteHandler.getMissingVotesCount())
                        .replace("%c",""+VoteHandler.getCurrentVoteCount())
                        .replace("%r",""+VoteHandler.getRequiredVotesCount())
                        .replace("&","\u00a7"),
                        color,
                        style

                );

                bar.setProgress(VoteHandler.getVotePercentage(counterType) / 100);

                for(Player player : Bukkit.getOnlinePlayers()) bar.addPlayer(player);

                bar.setVisible(true);

                try {
                    sleep(15000);
                } catch (InterruptedException ignored) {}

                bar.setVisible(false);
                bar.removeAll();

                try {
                    sleep(bar_warn_timer * 60000);
                } catch (InterruptedException ignored) { }

                try {
                    VoteParty.getInstance().getVoteHandler().save();
                    VoteParty.getInstance().getLoggerHelper().info("Votes successfully saved.");
                }catch (Exception e){
                    VoteParty.getInstance().getLoggerHelper().error("Fail to save votes.");
                }
            }
        });

        warn.start();
    }

    public void processBar(Player player){
        new Thread(() -> {

            BossBar bar = Bukkit.createBossBar(barMessage.replace("%m",""+VoteHandler.getMissingVotesCount()).replace("%c",""+VoteHandler.getCurrentVoteCount()).replace("%r",""+VoteHandler.getRequiredVotesCount()).replace("&","\u00a7"), color, style);

            bar.setProgress(VoteHandler.getVotePercentage(counterType) / 100);
            bar.addPlayer(player);

            bar.setVisible(true);

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bar.setVisible(false);

            bar.removePlayer(player);

        }).start();
    }

    private static void loadColor(String color){
        switch (color.toUpperCase()){
            case "BLUE": BarHandlerThread.color = BarColor.BLUE; return;
            case "GREEN": BarHandlerThread.color = BarColor.GREEN; return;
            case "PURPLE": BarHandlerThread.color = BarColor.PURPLE; return;
            case "RED": BarHandlerThread.color = BarColor.RED; return;
            case "WHITE": BarHandlerThread.color = BarColor.WHITE; return;
            case "YELLOW": BarHandlerThread.color = BarColor.YELLOW; return;
            default: BarHandlerThread.color = BarColor.PINK;
        }
    }

    private static void loadStyle(String Style){
        switch (Style.toUpperCase()){
            case "SEGMENTED_6": BarHandlerThread.style = BarStyle.SEGMENTED_6; return;
            case "SEGMENTED_6_10": BarHandlerThread.style = BarStyle.SEGMENTED_10; return;
            case "SEGMENTED_6_12": BarHandlerThread.style = BarStyle.SEGMENTED_12; return;
            case "SEGMENTED_6_20": BarHandlerThread.style = BarStyle.SEGMENTED_20; return;
            default: BarHandlerThread.style = BarStyle.SOLID;
        }
    }

}
