package net.nifheim.beelzebu.cancelsounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ProtocolManager protocolManager;
    private final Set<Sound> cancel = new HashSet<>();
    private final Map<Sound, Sound> replace = new HashMap<>();

    @Override
    public void onEnable() {
        reload();
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this, new PacketType[]{PacketType.Play.Server.NAMED_SOUND_EFFECT}) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    PacketContainer packet = event.getPacket();
                    Sound sound = packet.getSoundEffects().read(0);
                    if (cancel.contains(sound)) {
                        event.setCancelled(true);
                    } else if (replace.containsKey(sound)) {
                        packet.getSoundEffects().write(0, sound);
                    }
                }
            }
        });
    }

    private void reload() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }
        cancel.clear();
        replace.clear();
        reloadConfig();
        getConfig().getStringList("Sounds").forEach(sound -> {
            try {
                cancel.add(Sound.valueOf(sound.toUpperCase().replaceAll("\\.", "_")));
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "{0} isn''t a valid sound.", sound);
            }
        });
        getConfig().getStringList("Replace").forEach(sound -> {
            try {
                replace.put(Sound.valueOf(sound.toUpperCase().replaceAll("\\.", "_").split(":")[0]), Sound.valueOf(sound.toUpperCase().replaceAll("\\.", "_").split(":")[1]));
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "{0} isn''t a valid sound.", sound);
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("cancelsounds.reload")) {
            reload();
            sender.sendMessage("§aPlugin reloaded.");
        }
        return true;
    }
}
