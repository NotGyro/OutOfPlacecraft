package ink.echol.outofplacecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import ink.echol.outofplacecraft.net.YingletSkinManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.TeleportCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collection;
import java.util.UUID;

public class OOPCCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        // ---- Set species
        LiteralCommandNode<CommandSource> setSpeciesCommandNode = dispatcher
            .register(Commands.literal("setspecies")
            .requires((player) -> {
                return player.hasPermission(2);
            })
            .then(
                Commands.argument("targets",
                    EntityArgument.entities())
                    .then(
                    Commands.argument("species", StringArgumentType.string())
                    .executes((context) -> {
                        return setSpecies(context.getSource(), EntityArgument.getEntities(context, "targets"), StringArgumentType.getString(context, "species"));
                    })
                )
            )
        );

        // ---- Set yinglet skin

        LiteralCommandNode<CommandSource> setSkinCommandNode = dispatcher.register(Commands.literal("setyingletskin")
            .then(
                Commands.argument("url", StringArgumentType.string())
                    .executes((context) -> {
                        return setYingletSkin(context.getSource(), StringArgumentType.getString(context, "url"));
                    }
                )
            )
        );

        // ---- Set species
        dispatcher.register(Commands.literal("species").requires((context) -> {
            return context.hasPermission(2);
        }).redirect(setSpeciesCommandNode));
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


    public static int setYingletSkin(CommandSource source, String url) {
        try {
            PlayerEntity player = source.getPlayerOrException();
            UUID uuid = player.getUUID();
            YingletSkinManager.getServer().syncSkinServerSide(uuid, url);
            return 1;
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
