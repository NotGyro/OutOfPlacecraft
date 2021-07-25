package ink.echol.outofplacecraft.net;

import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.IYingletStatus;
import ink.echol.outofplacecraft.capabilities.YingletStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;
import java.util.function.Supplier;

import java.util.UUID;

public class YingletStatusPacket {
    protected boolean value;
    // If this is false, we're just synchronizing yinglet status. If this is *true*, the result is the same, but with more animation (maybe, someday, todo).
    protected boolean newTransformation = false;
    protected UUID player;

    public YingletStatusPacket(UUID playerID, boolean status) {
        this.player = playerID;
        this.value = status;
        this.newTransformation = false;
    }
    public YingletStatusPacket(UUID playerID, boolean status, boolean newTF) {
        this.player = playerID;
        this.value = status;
        this.newTransformation = newTF;
    }

    public static void encode(YingletStatusPacket msg, PacketBuffer buf) {
        buf.writeBoolean(msg.value);
        buf.writeBoolean(msg.newTransformation);
        buf.writeUUID(msg.player);
    }

    public static YingletStatusPacket decode(PacketBuffer buf) {
        boolean status = buf.readBoolean();
        boolean newTF = buf.readBoolean();
        UUID id = buf.readUUID();

        return new YingletStatusPacket(id, status, newTF);
    }

    public static void handle(YingletStatusPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPlayerEntity playerLocal = Minecraft.getInstance().player;
            ClientWorld world = Minecraft.getInstance().level;
            assert world != null;
            PlayerEntity player = world.getPlayerByUUID(msg.player);
            System.out.println("Yinglet status of player: <");
            System.out.print(player.getScoreboardName());
            System.out.print("> is ");
            System.out.print(msg.value);
            if(playerLocal.getGameProfile().getId().equals(msg.player)) {
                //We may have just been zat zing'd, maybe do something special!
                //TODO invoke some cool rendering madness here
                System.out.println("That's you! Weh.");
            }

            IYingletStatus cap = player.getCapability(CapabilityRegistry.YINGLET_CAPABILITY).orElseThrow(NullPointerException::new);
            cap.setIsYinglet(msg.value);
        });
        ctx.get().setPacketHandled(true);
    }
}