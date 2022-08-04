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

package me.luizotavio.minecraft;

import me.luizotavio.minecraft.impl.DefaultVisibilityAPI;
import me.luizotavio.minecraft.listener.VisibilityListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 03/08/2022
 */
public class VisibilityPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        ServicesManager servicesManager = Bukkit.getServicesManager();

        if (servicesManager.isProvidedFor(VisibilityAPI.class)) {
            return;
        }

        servicesManager.register(VisibilityAPI.class, new DefaultVisibilityAPI(this), this, ServicePriority.Normal);

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new VisibilityListener(), this);
    }

    @Override
    public void onDisable() {
        ServicesManager servicesManager = Bukkit.getServicesManager();

        if (!servicesManager.isProvidedFor(VisibilityAPI.class)) {
            return;
        }

        servicesManager.unregister(VisibilityAPI.class, this);

        HandlerList.unregisterAll(this);
    }
}
