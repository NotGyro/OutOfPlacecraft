package ink.echol.outofplacecraft.net;

import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.ISpecies;
import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import java.util.function.Supplier;

import java.util.UUID;

public class SpeciesPacket {
    protected int speciesId;
    // If this is false, we're just synchronizing yinglet status. If this is *true*, the result is the same, but with more animation (maybe, someday, todo).
    protected boolean newTransformation = false;
    protected UUID player;

    public SpeciesPacket(UUID playerID, int value) {
        this.player = playerID;
        this.speciesId = value;
        this.newTransformation = false;
    }
    public SpeciesPacket(UUID playerID, int value, boolean newTF) {
        this.player = playerID;
        this.speciesId = value;
        this.newTransformation = newTF;
    }

    public static void encode(SpeciesPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.speciesId);
        buf.writeBoolean(msg.newTransformation);
        buf.writeUUID(msg.player);
    }

    public static SpeciesPacket decode(PacketBuffer buf) {
        int species = buf.readInt();
        boolean newTF = buf.readBoolean();
        UUID id = buf.readUUID();

        return new SpeciesPacket(id, species, newTF);
    }

    public static void handle(SpeciesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPlayerEntity playerLocal = Minecraft.getInstance().player;
            ClientWorld world = Minecraft.getInstance().level;
            assert world != null;
            PlayerEntity player = world.getPlayerByUUID(msg.player);
            System.out.println("Species ID of player: <");
            System.out.print(player.getScoreboardName());
            System.out.print("> is ");
            System.out.print(msg.speciesId);
            if(playerLocal.getGameProfile().getId().equals(msg.player)) {
                if(msg.speciesId == SpeciesCapability.YINGLET_ID) {
                    //We may have just been zat zing'd, maybe do something special!
                    //TODO invoke some cool rendering madness here
                    System.out.println("Zat's you! Wehh");
                }
            }
            SpeciesHelper.setPlayerSpecies(player, msg.speciesId);
        });
        ctx.get().setPacketHandled(true);
    }
}