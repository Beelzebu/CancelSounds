package net.nifheim.beelzebu.cancelsounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.HashSet;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        HashSet<Sound> sounds = new HashSet<>();
        sounds.add(Sound.ENTITY_PLAYER_ATTACK_SWEEP);
        sounds.add(Sound.ENTITY_PLAYER_ATTACK_NODAMAGE);
        sounds.add(Sound.ENTITY_PLAYER_ATTACK_STRONG);
        sounds.add(Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK);
        sounds.add(Sound.ENTITY_PLAYER_ATTACK_CRIT);
        sounds.add(Sound.ENTITY_PLAYER_ATTACK_WEAK);
        sounds.add(Sound.BLOCK_PORTAL_AMBIENT);
        sounds.add(Sound.BLOCK_PORTAL_TRAVEL);
        sounds.add(Sound.BLOCK_PORTAL_TRIGGER);
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
}
