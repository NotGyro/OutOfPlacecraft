package ink.echol.outofplacecraft.net;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncAllSkinsPkt {
    protected String serializedIndex;

    public SyncAllSkinsPkt(String index) {
        this.serializedIndex = index;
    }

    public static void encode(SyncAllSkinsPkt msg, PacketBuffer buf) {
        buf.writeUtf(msg.serializedIndex);
    }

    public static SyncAllSkinsPkt decode(PacketBuffer buf) {
        String idx = buf.readUtf();

        return new SyncAllSkinsPkt(idx);
    }

    public static void handle(SyncAllSkinsPkt msg, Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get() != null) {
            ctx.get().enqueueWork(() -> {
                YingletSkinManager.mergeIndexFrom((YingletSkinManager.loadIndexFromJson(msg.serializedIndex)));
                YingletSkinManager.validateOrDownloadFromIndex();
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
