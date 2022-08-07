/*
 * MIT License
 *
 * Copyright (c) [year] [fullname]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.luizotavio.minecraft.impl;

import me.luizotavio.minecraft.VisibilityAPI;
import me.luizotavio.minecraft.factory.VisibilityPacketFactory;
import me.luizotavio.minecraft.util.Parallelism;
import me.luizotavio.minecraft.util.Ranges;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 03/08/2022
 */
public class DefaultVisibilityAPI implements VisibilityAPI {

    private static final Map<UUID, Set<UUID>> HIDE_VISIBILITY_MAP = new ConcurrentHashMap<>();

    private final Plugin plugin;

    public DefaultVisibilityAPI(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void show(@NotNull Player player, Set<Player> targets) {
        Set<UUID> uuids = HIDE_VISIBILITY_MAP.get(player.getUniqueId());

        if (uuids != null) {
            uuids.removeAll(
                targets.stream()
                    .map(Player::getUniqueId)
                    .collect(Collectors.toSet())
            );
        }

        Packet<?>[] packets = VisibilityPacketFactory.createShowPacket(player, targets);

        Parallelism.callToSync(plugin, () -> {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

            for (Packet<?> packet : packets) {
                entityPlayer.b.sendPacket(packet);
            }
        });
    }

    @Override
    public void hide(@NotNull Player player, Set<Player> targets) {
        Set<UUID> uuids = HIDE_VISIBILITY_MAP.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet());

        uuids.addAll(
            targets.stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toSet())
        );

        // Make a copy stream because there is a hidden map in the CraftBukkit which excludes the target player
        Set<Player> visible = targets.parallelStream()
            .filter(player::canSee)
            .collect(Collectors.toUnmodifiableSet());

        Packet<?>[] packets = VisibilityPacketFactory.createHidePacket(visible);

        Parallelism.callToSync(plugin, () -> {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

            for (Packet<?> packet : packets) {
                entityPlayer.b.sendPacket(packet);
            }
        });
    }

    @Override
    public boolean isVisible(@NotNull Player player, @NotNull Player target) {
        Set<UUID> uuids = HIDE_VISIBILITY_MAP.get(player.getUniqueId());

        return uuids == null || !uuids.contains(target.getUniqueId());
    }

    @Override
    public boolean isVisible(@NotNull Player player, @NotNull Player target, boolean checkDistance) {
        Set<UUID> uuids = HIDE_VISIBILITY_MAP.get(player.getUniqueId());

        return uuids == null || (!uuids.contains(target.getUniqueId()) && (!checkDistance || Ranges.isInRange(player.getLocation(), target.getLocation())));
    }

    @Override
    public Set<Player> getInvisiblePlayers(@NotNull Player player) {
        return Bukkit.getOnlinePlayers().parallelStream()
            .filter(p -> !isVisible(player, p))
            .collect(Collectors.toUnmodifiableSet());
    }
}
