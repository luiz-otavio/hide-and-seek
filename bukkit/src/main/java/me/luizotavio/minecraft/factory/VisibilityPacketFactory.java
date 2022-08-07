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

package me.luizotavio.minecraft.factory;

import me.luizotavio.minecraft.util.Ranges;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 03/08/2022
 */
public class VisibilityPacketFactory {

    public static Packet<?>[] createHidePacket(@NotNull Set<Player> collection) {
        Objects.requireNonNull(collection);

        Set<EntityPlayer> entityPlayers = collection.parallelStream()
            .map(player -> ((CraftPlayer) player).getHandle())
            .collect(Collectors.toUnmodifiableSet());

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
            entityPlayers
        );

        PacketPlayOutEntityDestroy packetDestroy = new PacketPlayOutEntityDestroy(
            entityPlayers.stream()
                .mapToInt(EntityPlayer::getId)
                .toArray()
        );

        return new Packet<?>[] {
            packet,
            packetDestroy
        };
    }

    public static Packet<?>[] createShowPacket(@NotNull Player from, @NotNull Set<Player> collection) {
        Objects.requireNonNull(collection);

        Set<EntityPlayer> entityPlayers = collection.parallelStream()
            .map(player -> ((CraftPlayer) player).getHandle())
            .collect(Collectors.toUnmodifiableSet());

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
            entityPlayers
        );

        Location fromLocation = from.getLocation();

        PacketPlayOutNamedEntitySpawn[] packets = entityPlayers.stream()
            .filter(entityPlayer -> {
                if (entityPlayer.isRemoved()) {
                    return false;
                }

                // Check if player is in range of the view
                return Ranges.isInRange(fromLocation, entityPlayer.getBukkitEntity().getLocation());
            }).map(PacketPlayOutNamedEntitySpawn::new)
            .toArray(PacketPlayOutNamedEntitySpawn[]::new);

        // Need to call the entity tracker to add the player
        return (Packet<?>[]) ArrayUtils.add(new Packet<?>[] { packet }, packets);
    }



}
