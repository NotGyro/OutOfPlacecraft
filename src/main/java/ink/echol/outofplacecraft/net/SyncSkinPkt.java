package ink.echol.outofplacecraft.net;

import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncSkinPkt {
    protected UUID playerId;
    protected String url;
    protected String hash;

    public SyncSkinPkt(UUID player, String url, String hash) {
        this.playerId = player;
        this.url = url;
        this.hash = hash;
    }

    public static void encode(SyncSkinPkt msg, PacketBuffer buf) {
        buf.writeUUID(msg.playerId);
        buf.writeUtf(msg.url);
        buf.writeUtf(msg.hash);
    }

    public static SyncSkinPkt decode(PacketBuffer buf) {
        UUID id = buf.readUUID();
        String url = buf.readUtf();
        String hash = buf.readUtf();

        return new SyncSkinPkt(id, url, hash);
    }

    public static void handle(SyncSkinPkt msg, Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get() != null) {
            ctx.get().enqueueWork(() -> {
                YingletSkinManager.clientSyncIncomingSkin(msg.playerId, msg.url, msg.hash);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
