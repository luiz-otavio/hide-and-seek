/**
 * MIT License
 * <p>
 * Copyright (c) [year] [fullname]
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.luizotavio.minecraft;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 03/08/2022
 */
public interface VisibilityAPI {

    /**
     * Shows the player to the other players.
     * @param player the player to show.
     * @param targets the players to show the player to.
     */
    void show(@NotNull Player player, Set<Player> targets);

    /**
     * Hide the player from the targets.
     * @param player The player to hide.
     * @param targets The targets to hide the player from.
     */
    void hide(@NotNull Player player, Set<Player> targets);

    /**
     * Check if the player is visible to the target.
     * @param player The player.
     * @param target The target.
     * @return True if the player is visible to the target.
     */
    boolean isVisible(@NotNull Player player, @NotNull Player target);

    /**
     * Check if the player is visible to the target with the default check distance.
     * @param player The player.
     * @param target The target.
     * @param checkDistance The check distance.
     * @return True if the player is visible to the target.
     */
    boolean isVisible(@NotNull Player player, @NotNull Player target, boolean checkDistance);

    /**
     * Retrieve almost invisible players from the player.
     * @param player The player.
     * @return The visibility of all players to the player.
     */
    Set<Player> getInvisiblePlayers(@NotNull Player player);

}
