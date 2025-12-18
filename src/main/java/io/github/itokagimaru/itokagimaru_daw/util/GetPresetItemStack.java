package io.github.itokagimaru.itokagimaru_daw.util;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GetPresetItemStack {
    public static ItemStack cassette(){
        ItemStack stack = new ItemStack(Material.PAPER);
        stack.editMeta(meta -> {
            meta.customName(Component.text("カセットテープ"));
            meta.setItemModel(NamespacedKey.minecraft("cassette_tape"));
            meta.setMaxStackSize(1);
        });
        ItemData.ITEM_ID.set(stack,"CASSETTE TAPE");
        return stack;
    }

    public static ItemStack daw(){
        ItemStack stack = new ItemStack(Material.WOODEN_HOE);
        stack.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("itokagimaru_daw"));
            meta.customName(Component.text("daw").color(TextColor.color(149, 229, 249)));
        });
        ItemData.ITEM_ID.set(stack,"daw");
        ItemData.MUSIC_SAVED_RED.set(stack,new int[Itokagimaru_daw.MUSIC_LENGTH]);
        return stack;
    }

    public static ItemStack workSpace(){
        ItemStack stack = new ItemStack(Material.PAPER);
        stack.editMeta(meta -> {
            meta.customName(Component.text("カセットテープ編集台"));
            meta.setItemModel(NamespacedKey.minecraft("cassette_workspace_item"));
            meta.lore(List.of(
                    Component.text("カセットテープを"),
                    Component.text("作成,編集できる作業台"),
                    Component.text("設置方法:地面に設置された\"輝く額縁\"に入れ"),
                    Component.text("　　　　 右クリックすることで設置"),
                    Component.text("撤去方法:シフト右クリックで撤去")
            ));
        });
        ItemData.ITEM_ID.set(stack,"CASSETTE_WORKSPACE_ITEM");
        return stack;
    }

    public static ItemStack walkMan(){
        ItemStack stack = new ItemStack(Material.WOODEN_HOE);
        stack.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("walkman"));
            meta.customName(Component.text("レトロなカセットプレイヤー"));
            meta.lore(List.of(
                    Component.text("レトロでかわいいカセットプレイヤー"),
                    Component.text("**注意**"),
                    Component.text("イヤホンジャックがぬけた状態で"),
                    Component.text("再生してしまいますと"),
                    Component.text("音漏れいたしますのでご注意ください")
            ));
        });
        ItemData.ITEM_ID.set(stack,"walkman");
        return stack;
    }

    public static ItemStack musicSheet(){
        ItemStack stack = new ItemStack(Material.PAPER);
        stack.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("blank_sheet_music"));
            meta.customName(Component.text("白紙の楽譜"));
            meta.setMaxStackSize(1);
        });
        ItemData.ITEM_ID.set(stack,"BLANK SHEET");
        return stack;
    }
}
