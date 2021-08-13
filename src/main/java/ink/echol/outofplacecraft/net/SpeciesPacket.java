package ink.echol.outofplacecraft.net;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.ISpecies;
import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import ink.echol.outofplacecraft.items.ZatZhingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.Level;

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
        if(ctx.get() != null) {
            ctx.get().enqueueWork(() -> {
                ClientPlayerEntity playerLocal = Minecraft.getInstance().player;
                ClientWorld world = Minecraft.getInstance().level;
                assert world != null;
                PlayerEntity player = world.getPlayerByUUID(msg.player);
                if(player != null) {
                    OutOfPlacecraftMod.LOGGER.log(Level.INFO, "Species ID of player: <" + player.getScoreboardName() + "> is " + msg.speciesId);
                    if(playerLocal.getGameProfile().getId().equals(msg.player)) {
                        if(msg.speciesId == SpeciesCapability.YINGLET_ID) {
                            //We may have just been zat zing'd, maybe do something special!
                            //TODO invoke some cool rendering madness here
                            OutOfPlacecraftMod.LOGGER.log(Level.INFO, "Zat's you! Wehh");
                        }
                    }
                    ISpecies cap = player.getCapability(CapabilityRegistry.SPECIES_CAPABILITY, null)
                            .orElse(new SpeciesCapability.Implementation(SpeciesCapability.HUMAN_ID));
                    cap.setSpecies(msg.speciesId);
                    // Make sure max health is consistent with species. Must be called AFTER setSpecies.
                    ZatZhingItem.fixMaxHealth(player);

                    // Make sure eye-height and hitbox are updated correctly.
                    player.refreshDimensions();
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}