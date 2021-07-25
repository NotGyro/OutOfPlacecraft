package ink.echol.outofplacecraft.net;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class OOPCPacketHandler {
    public static final int YINGLET_STATUS_MSG_ID = 0;

    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(OutOfPlacecraftMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        INSTANCE.registerMessage(YINGLET_STATUS_MSG_ID, YingletStatusPacket.class, YingletStatusPacket::encode, YingletStatusPacket::decode, YingletStatusPacket::handle);
    }
}
