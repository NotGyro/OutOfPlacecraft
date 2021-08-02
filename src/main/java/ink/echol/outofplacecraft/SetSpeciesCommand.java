package ink.echol.outofplacecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.TeleportCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collection;

public class SetSpeciesCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(Commands.literal("setspecies").requires((player) -> {
            return player.hasPermission(2);
        }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("species", StringArgumentType.string()).executes((context) -> {
            return setSpecies(context.getSource(), EntityArgument.getEntities(context, "targets"), StringArgumentType.getString(context, "species"));
        }))));

        dispatcher.register(Commands.literal("species").requires((context) -> {
            return context.hasPermission(2);
        }).redirect(literalcommandnode));
    }

    public static int setSpecies(CommandSource source, Collection<? extends Entity> targets, String species) {
        for(Entity entity:targets) {
            if(entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                SpeciesHelper.setPlayerSpecies(player, species.toLowerCase().hashCode());
                SpeciesHelper.syncSpeciesToClient(player, player, true);
                if( source.getEntity() != null ) {
                    if( source.getEntity() instanceof PlayerEntity ) {
                        PlayerEntity playerCommand = (PlayerEntity) source.getEntity();
                        if( playerCommand != player) {
                            SpeciesHelper.syncSpeciesToClient(playerCommand, player, true);
                        }
                    }
                }
            }
        }
        return targets.size();
    }
}
