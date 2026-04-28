package io.github.itokagimaru.mun10music.util;

import io.github.itokagimaru.mun10music.data.UsefulKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jspecify.annotations.NonNull;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MakeItem {
    /**
     * 互換用: 既存のmodel文字列をcmdへ変換して扱う。
     */
    @Deprecated(forRemoval = false)
    public static <T> void setItemMeta(ItemStack itemstack, String name, int[] nameColor, String model, UsefulKey<@NonNull T> key, T val) {
        setItemMeta(itemstack, name, nameColor, resolveCmd(model), key, val);
    }

    public static <T> void setItemMeta(ItemStack itemstack, String name, int[] nameColor, int cmd, UsefulKey<@NonNull T> key, T val) {
        TextColor textColor = null;
        if(nameColor != null) {
            textColor = TextColor.color(nameColor[0], nameColor[2], nameColor[3]);
        }
        applyItemMeta(itemstack, name, textColor, cmd, key, val);
    }

    /**
     * 互換用: 既存のmodel文字列をcmdへ変換して扱う。
     */
    @Deprecated(forRemoval = false)
    public static <T> void setItemMetaByColor(ItemStack itemstack, String name, TextColor nameColor, String model, UsefulKey<@NonNull T> key, T val) {
        setItemMetaByColor(itemstack, name, nameColor, resolveCmd(model), key, val);
    }

    public static <T> void setItemMetaByColor(ItemStack itemstack, String name, TextColor nameColor, int cmd, UsefulKey<@NonNull T> key, T val) {
        applyItemMeta(itemstack, name, nameColor, cmd, key, val);
    }

    private static <T> void applyItemMeta(ItemStack itemstack, String name, TextColor nameColor, int cmd, UsefulKey<@NonNull T> key, T val) {
        ItemMeta meta = itemstack.getItemMeta();
        if (name != null) {
            var nameComponent = Component.text(name);
            if (nameColor != null) {
                nameComponent = nameComponent.color(nameColor);
            }
            meta.displayName(nameComponent);

            if (cmd > 0) {
                CustomModelDataComponent customModelDataComponent = meta.getCustomModelDataComponent();
                customModelDataComponent.setFloats(List.of((float) cmd));
                meta.setCustomModelDataComponent(customModelDataComponent);
            }
            if (key != null && val != null) {
                key.set(meta.getPersistentDataContainer(), val);
            }
            itemstack.setItemMeta(meta);
        }
    }

    private static int resolveCmd(String model) {
        if (model == null || model.isBlank()) {
            return 0;
        }
        return switch (model) {
            case "next_b_up" -> 8;
            case "next_b_down" -> 9;
            case "next_b_left" -> 10;
            case "next_b_right" -> 11;
            case "select" -> 12;
            case "note_def" -> 20;
            case "note_up" -> 21;
            case "note_dw" -> 22;
            case "key_board_0" -> 13;
            case "key_board_1" -> 14;
            case "key_board_2" -> 15;
            case "key_board_3" -> 16;
            case "key_board_4" -> 17;
            case "key_board_5" -> 18;
            case "key_board_6" -> 19;
            default -> 0;
        };
    }
}

