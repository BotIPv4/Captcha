package dev.vnco.captcha;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CaptchaListener implements Listener {

    private final Map<UUID, String> stringMap = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        StringBuilder builder = new StringBuilder(6);
        String string = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        int i = 0;
        while (i++ < 6) {
            builder.append(string.charAt((int) (string.length() * Math.random())));
        }
        
        String code = builder.toString();

        stringMap.put(player.getUniqueId(), code);

        sendMessage(player,

                "&7&m" + Strings.repeat("-", 40),
                "&b&lCaptcha Verification",
                "",
                "Type the following code in the chat: &a" + code,
                "&7&m" + Strings.repeat("-", 40)

        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        stringMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!event.getMessage().equals(stringMap.get(uuid))) {
            player.kickPlayer("Invalid Code.");
        } else {
            stringMap.remove(uuid);
            sendMessage(player, "&aCaptcha has been resolved.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        cancel(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/")) {
            cancel(event);
        }
    }

    private void cancel(PlayerEvent event) {
        if (!stringMap.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        if (event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(true);
        }
    }

    private void sendMessage(Player player, String... message) {
        for (String string : message) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
        }
    }
}
