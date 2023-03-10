package net.chito.tutorialmod.networking.packet;

import net.chito.tutorialmod.thirst.PlayerThirstProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DrinkWaterC2SPacket {
    private static final String MESSAGE_DRINK_WATER = "message.tutorialmod.drink_water";
    private static final String MESSAGE_NO_WATER = "message.tutorialmod.no_water";

    public DrinkWaterC2SPacket() {

    }

    public DrinkWaterC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE SERVER
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();

            if(hasWaterAroundThem(player, level, 2)) {
                // Notify the player that water has been drank
                player.sendSystemMessage(Component.translatable(MESSAGE_DRINK_WATER).withStyle(ChatFormatting.DARK_AQUA));
                // play the drinking sound
                level.playSound(null, player.getOnPos(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS,
                        0.5f, level.random.nextFloat() * 0.1f + 0.9f);

                player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                    // increase the water level of player
                    thirst.addThirst(1);
                    // Output the current thirst level
                    player.sendSystemMessage(Component.literal("Current thirst: " + thirst.getThirst()).withStyle(ChatFormatting.AQUA));
                });
            } else {
                // Notify the player that there is no water around
                player.sendSystemMessage(Component.translatable(MESSAGE_NO_WATER).withStyle(ChatFormatting.RED));

                player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                    // Output the current thirst level
                    player.sendSystemMessage(Component.literal("Current thirst: " + thirst.getThirst()).withStyle(ChatFormatting.AQUA));
                });
            }
        });
        return true;
    }

    private boolean hasWaterAroundThem(ServerPlayer player, ServerLevel level, int size) {
        return level.getBlockStates(player.getBoundingBox().inflate(size))
                .filter(state -> state.is(Blocks.WATER)).toArray().length > 0;
    }
}
