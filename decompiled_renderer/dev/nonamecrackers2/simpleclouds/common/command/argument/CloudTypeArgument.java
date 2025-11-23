/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  javax.annotation.Nullable
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.commands.arguments.ResourceLocationArgument
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo$Template
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package dev.nonamecrackers2.simpleclouds.common.command.argument;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudType;
import dev.nonamecrackers2.simpleclouds.common.cloud.CloudTypeSource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CloudTypeArgument
extends ResourceLocationArgument {
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_TYPE = new DynamicCommandExceptionType(o -> Component.m_237110_((String)"commands.simpleclouds.cloudType.notFound", (Object[])new Object[]{o}));
    @Nullable
    private CloudTypeSource source;
    @Nullable
    private List<ResourceLocation> cloudTypes;

    private CloudTypeArgument(CloudTypeSource source) {
        this.source = source;
    }

    public static CloudTypeArgument type(CloudTypeSource source) {
        return new CloudTypeArgument(source);
    }

    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation loc = super.parse(reader);
        if (this.source != null ? !this.source.doesCloudTypeExist(loc) : !this.cloudTypes.contains(loc)) {
            throw ERROR_UNKNOWN_TYPE.create((Object)loc);
        }
        return loc;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List vals = this.source == null ? this.cloudTypes.stream().map(ResourceLocation::toString).collect(Collectors.toList()) : Arrays.stream(this.source.getIndexedCloudTypes()).map(CloudType::id).map(ResourceLocation::toString).collect(Collectors.toList());
        return SharedSuggestionProvider.m_82970_(vals, (SuggestionsBuilder)builder);
    }

    public static class Info
    implements ArgumentTypeInfo<CloudTypeArgument, Template> {
        public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
            buffer.m_236828_(template.types, FriendlyByteBuf::m_130085_);
        }

        public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
            return new Template(buffer.m_236845_(FriendlyByteBuf::m_130281_));
        }

        public void serializeToJson(Template template, JsonObject json) {
            JsonArray array = new JsonArray();
            template.types.forEach(t -> array.add(t.toString()));
            json.add("types", (JsonElement)array);
        }

        public Template unpack(CloudTypeArgument argument) {
            return new Template(Arrays.stream(argument.source.getIndexedCloudTypes()).map(CloudType::id).collect(Collectors.toList()));
        }

        public final class Template
        implements ArgumentTypeInfo.Template<CloudTypeArgument> {
            private final List<ResourceLocation> types;

            public Template(List<ResourceLocation> types) {
                this.types = types;
            }

            public CloudTypeArgument instantiate(CommandBuildContext context) {
                CloudTypeArgument arg = new CloudTypeArgument(null);
                arg.cloudTypes = this.types;
                return arg;
            }

            public ArgumentTypeInfo<CloudTypeArgument, ?> m_213709_() {
                return Info.this;
            }
        }
    }
}

