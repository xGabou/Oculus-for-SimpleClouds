/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.commands.WeatherCommand
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.nonamecrackers2.simpleclouds.mixin;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeDataManager;
import dev.nonamecrackers2.simpleclouds.common.world.CloudManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={WeatherCommand.class})
public class MixinWeatherCommand {
    private static final SimpleCommandExceptionType WEATHER_CANNOT_BE_MODIFIED = new SimpleCommandExceptionType((Message)Component.m_237115_((String)"command.simpleclouds.weather.override"));

    private static void checkAndOrThrow(Level level) throws CommandSyntaxException {
        if (!CloudManager.useVanillaWeather(level, CloudTypeDataManager.getServerInstance())) {
            throw WEATHER_CANNOT_BE_MODIFIED.create();
        }
    }

    @Inject(method={"setClear"}, at={@At(value="HEAD")}, cancellable=true)
    private static void simpleclouds$preventWeatherModification_setClear(CommandSourceStack stack, int duration, CallbackInfoReturnable<Integer> ci) throws CommandSyntaxException {
        MixinWeatherCommand.checkAndOrThrow((Level)stack.m_81372_());
    }

    @Inject(method={"setRain"}, at={@At(value="HEAD")}, cancellable=true)
    private static void simpleclouds$preventWeatherModification_setRain(CommandSourceStack stack, int duration, CallbackInfoReturnable<Integer> ci) throws CommandSyntaxException {
        MixinWeatherCommand.checkAndOrThrow((Level)stack.m_81372_());
    }

    @Inject(method={"setThunder"}, at={@At(value="HEAD")}, cancellable=true)
    private static void simpleclouds$preventWeatherModification_setThunder(CommandSourceStack stack, int duration, CallbackInfoReturnable<Integer> ci) throws CommandSyntaxException {
        MixinWeatherCommand.checkAndOrThrow((Level)stack.m_81372_());
    }
}

