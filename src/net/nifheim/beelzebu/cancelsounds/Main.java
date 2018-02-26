package net.nifheim.beelzebu.cancelsounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ProtocolManager protocolManager;
    private final Set<Sound> sounds = new HashSet<>();

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
                    if (sounds.contains(sound)) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }

    private void reload() {
        saveResource("config.yml", false);
        sounds.clear();
        reloadConfig();
        getConfig().getStringList("Sounds").forEach(sound -> {
            try {
                sounds.add(Sound.valueOf(sound.toUpperCase().replaceAll("\\.", "_")));
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
