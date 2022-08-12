/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.wc.model;

import com.wynntils.core.WynntilsMod;
import com.wynntils.core.managers.CoreManager;
import com.wynntils.mc.event.ContainerClickEvent;
import com.wynntils.mc.event.MenuEvent.MenuClosedEvent;
import com.wynntils.mc.event.MenuEvent.MenuOpenedEvent;
import com.wynntils.mc.utils.ComponentUtils;
import com.wynntils.mc.utils.ItemUtils;
import com.wynntils.wc.event.WorldStateEvent;
import com.wynntils.wc.objects.ClassType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CharacterManager extends CoreManager {
    private static final Pattern CLASS_PATTERN = Pattern.compile("§e- §r§7Class: §r§f(.+)");
    private static final Pattern LEVEL_PATTERN = Pattern.compile("§e- §r§7Level: §r§f(\\d+)");

    private static CharacterInfo currentCharacter;
    private static boolean inCharacterSelection;

    public static boolean hasCharacter() {
        return currentCharacter != null;
    }

    public static CharacterInfo getCharacterInfo() {
        return currentCharacter;
    }

    @SubscribeEvent
    public static void onMenuOpened(MenuOpenedEvent e) {
        if (e.getMenuType() == MenuType.GENERIC_9x3
                && ComponentUtils.getCoded(e.getTitle()).equals("§8§lSelect a Class")) {
            inCharacterSelection = true;
            WynntilsMod.info("In character selection menu");
        }
    }

    @SubscribeEvent
    public static void onMenuClosed(MenuClosedEvent e) {
        inCharacterSelection = false;
    }

    @SubscribeEvent
    public static void onWorldStateChanged(WorldStateEvent e) {
        // Whenever we're leaving a world, clear the current character
        if (e.getOldState() == WorldStateManager.State.WORLD) {
            currentCharacter = null;
            // This should not be needed, but have it as a safeguard
            inCharacterSelection = false;
        }
        if (e.getNewState() == WorldStateManager.State.CHARACTER_SELECTION) {
            WynntilsMod.info("Preparing for character selection");
        }
    }

    @SubscribeEvent
    public static void onContainerClick(ContainerClickEvent e) {
        if (inCharacterSelection) {
            if (e.getItemStack().getItem() == Items.AIR) return;
            currentCharacter = CharacterInfo.parseCharacter(e.getItemStack(), e.getSlotNum());
            WynntilsMod.info("Selected character " + currentCharacter);
        }
    }

    // TODO: We don't have a way to parse CharacterInfo if auto select class is on for the player
    //       Fix this by storing last selected class in WebAPI.
    public static final class CharacterInfo {
        private final ClassType classType;
        private final boolean reskinned;
        private final int level;

        // This field is basically the slot id of the class,
        // meaning that if a class changes slots, the ID will not be persistent.
        // This was implemented the same way by legacy.
        private final int id;

        private CharacterInfo(ClassType classType, boolean reskinned, int level, int id) {
            this.classType = classType;
            this.reskinned = reskinned;
            this.level = level;
            this.id = id;
        }

        public ClassType getClassType() {
            return classType;
        }

        public boolean isReskinned() {
            return reskinned;
        }

        public int getLevel() {
            return level;
        }

        public int getId() {
            return id;
        }

        public static CharacterInfo parseCharacter(ItemStack itemStack, int slotNum) {
            List<String> lore = ItemUtils.getLore(itemStack);

            int level = 0;
            String className = "";

            for (String line : lore) {
                Matcher levelMatcher = LEVEL_PATTERN.matcher(line);
                if (levelMatcher.matches()) {
                    level = Integer.parseInt(levelMatcher.group(1));
                    continue;
                }

                Matcher classMatcher = CLASS_PATTERN.matcher(line);

                if (classMatcher.matches()) {
                    className = classMatcher.group(1);
                }
            }
            ClassType classType = ClassType.fromName(className);

            return new CharacterInfo(classType, classType != null && ClassType.isReskinned(className), level, slotNum);
        }

        @Override
        public String toString() {
            return "CharacterInfo[classType=" + classType + ", reskinned=" + reskinned + ", level=" + level + ", id="
                    + id + ']';
        }
    }
}