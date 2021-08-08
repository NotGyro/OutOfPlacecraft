package ink.echol.outofplacecraft.net;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class OOPCPacketHandler {
    public static final int SPECIES_MSG_ID = 0;
    public static final int SYNC_SKIN_MSG_ID = 1;

    public static final String PROTOCOL_VERSION = "0.1.1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(OutOfPlacecraftMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        INSTANCE.registerMessage(SPECIES_MSG_ID, SpeciesPacket.class, SpeciesPacket::encode, SpeciesPacket::decode, SpeciesPacket::handle);
        INSTANCE.registerMessage(SYNC_SKIN_MSG_ID, SyncSkinPkt.class, SyncSkinPkt::encode, SyncSkinPkt::decode, SyncSkinPkt::handle);
    }
}
