/**
 * OutOfPlaceCraft
 * Copyright (c) 2021.
 *
 * This file is part of OutOfPlaceCraft.
 *
 * OutOfPlaceCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OutOfPlaceCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OutOfPlaceCraft.  If not, see <https://www.gnu.org/licenses/>.
 */
package ink.echol.outofplacecraft.entities.yinglet;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Collections;

@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class YingletEntity extends CreatureEntity implements IAnimatable {
	public static final EntityType<YingletEntity> ENTITY_TYPE = new EntityType<>(
			YingletEntity::new, // Factory
			EntityClassification.MISC, // Category
			true, // Serialize
			false, // Summon
			false, // Fire immune
			false, // Can spawn far from player
			ImmutableSet.of(), // Immune to
			EntitySize.fixed(1f, 1f), // Dimensions
			0, // Client Tracking Range
			0 // Update Interval
	);

	public static final Item SPAWN_EGG = new SpawnEggItem(
			ENTITY_TYPE,
			0x000000, // eggPrimary ???
			0xFFFFFF, // eggSecondary ???
			(new Item.Properties()).tab(ItemGroup.TAB_MISC)
	);

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		YingletEntity.ENTITY_TYPE.setRegistryName(new ResourceLocation(OutOfPlacecraftMod.MODID, "yinglet"));
		event.getRegistry().register(YingletEntity.ENTITY_TYPE);
	}

	@SubscribeEvent
	public static void registerSpawnEggs(RegistryEvent.Register<Item> event) {
		SPAWN_EGG.setRegistryName(new ResourceLocation(OutOfPlacecraftMod.MODID, "yinglet_spawn_egg"));
		event.getRegistry().register(SPAWN_EGG);
	}

	public YingletEntity(final EntityType<? extends CreatureEntity> type, final World world_in) {
		super(type, world_in);
	}

	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return Collections.emptyList();
	}

	@Override
	public ItemStack getItemBySlot(final EquipmentSlotType slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemSlot(final EquipmentSlotType slot, final ItemStack stack) {

	}

	@Override
	public HandSide getMainArm() {
		return HandSide.RIGHT;
	}

	@Override
	public void registerControllers(final AnimationData data) {
		// TODO Figure out animations I guess?
	}

	@Override
	public AnimationFactory getFactory() {
		return null;
	}
}
