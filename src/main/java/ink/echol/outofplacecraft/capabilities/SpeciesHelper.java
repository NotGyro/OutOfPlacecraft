package ink.echol.outofplacecraft.capabilities;

import ink.echol.outofplacecraft.net.OOPCPacketHandler;
import ink.echol.outofplacecraft.net.SpeciesPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

// TODO: Add compatibility to other mods which can set players as non-human entities. Planned for DragonSurvival.
public class SpeciesHelper {
    public static final int Species(String str) {
        return str.hashCode();
    }
    public static final int getPlayerSpecies(PlayerEntity player) {
        // Special cases for Dragon Survival and such (vampires?) will go here.
        ISpecies cap = player.getCapability(CapabilityRegistry.SPECIES_CAPABILITY, null)
                .orElse(new SpeciesCapability.Implementation(SpeciesCapability.HUMAN_ID));
        return cap.getSpecies();
    }
    //Set species - returns true if successful/possible, false if you cannot.
    public static final boolean setPlayerSpecies(PlayerEntity player, int speciesId) {
        ISpecies cap = player.getCapability(CapabilityRegistry.SPECIES_CAPABILITY, null)
                .orElse(new SpeciesCapability.Implementation(SpeciesCapability.HUMAN_ID));
        // Special cases for Dragon Survival and such (vampires?) will go here.

        cap.setSpecies(speciesId);
        // We are necessarily serverside. Send a packet.
        UUID targetUUID = player.getGameProfile().getId();

        // Last argument on YingletStatusPacket's constructor true for "this is a transformation."
        SpeciesPacket pkt = new SpeciesPacket(targetUUID, speciesId, true);
        OOPCPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), pkt);

        // Make sure eye-height and hitbox are updated correctly.
        player.refreshDimensions();

        return true;
    }

    public static void syncSpeciesToClient(PlayerEntity player, PlayerEntity target) {
        syncSpeciesToClient(player, target, false);
    }
    public static void syncSpeciesToClient(PlayerEntity player, PlayerEntity target, boolean newTF) {
        // It shouldn't be possible to get here without being serversided, but we might as well sanity check it, just in case.
        if (player instanceof ServerPlayerEntity) {
            ISpecies targetCap = target.getCapability(CapabilityRegistry.SPECIES_CAPABILITY, null).orElseThrow(NullPointerException::new);
            UUID targetUUID = target.getGameProfile().getId();
            SpeciesPacket pkt = new SpeciesPacket(targetUUID, targetCap.getSpecies(), newTF);
            OOPCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), pkt);
        }
    }
}
