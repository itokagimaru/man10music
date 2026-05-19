package io.github.itokagimaru.mun10music.util;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Items;
import io.github.itokagimaru.mun10music.data.ItemData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GetPresetItemStack {
    private static Items itemsData() {
        Man10Music plugin = Man10Music.getInstance();
        if (plugin == null) {
            throw new IllegalStateException("プラグインが初期化される前に呼び出されました。");
        }
        return plugin.getPluginConfigData().getItems();
    }

    private static ItemStack createItem(Items.Entry entry) {
        ItemStack stack = new ItemStack(entry.getMaterial());
        stack.editMeta(meta -> {
            meta.setCustomModelData(entry.getCmd());
            meta.setMaxStackSize(1);
        });
        return stack;
    }

    public static ItemStack cassette(){

        ItemStack stack = createItem(itemsData().getCassette());
        stack.editMeta(meta -> {
            meta.customName(Component.text("カセットテープ").color(NamedTextColor.AQUA));
            meta.setMaxStackSize(64);
        });
        ItemData.ITEM_ID.set(stack,"CASSETTE TAPE");
        return stack;
    }

    public static ItemStack daw(){
        ItemStack stack = createItem(itemsData().getKeyBoard());
        stack.editMeta(meta -> {
            meta.customName(Component.text("自動再生キーボード").color(NamedTextColor.AQUA));
        });
        ItemData.ITEM_ID.set(stack,"daw");
        ItemData.MUSIC_SAVED_RED.set(stack,new int[Man10Music.MUSIC_LENGTH]);
        return stack;
    }

    public static ItemStack walkMan(){
        ItemStack stack = createItem(itemsData().getWalkMan());
        stack.editMeta(meta -> {
            meta.customName(Component.text("レトロなカセットプレイヤー").color(NamedTextColor.AQUA));
            meta.lore(List.of(
                    Component.text("レトロでかわいいカセットプレイヤー").color(NamedTextColor.WHITE),
                    Component.text("**注意**").color(NamedTextColor.DARK_RED),
                    Component.text("イヤホンジャックがぬけた状態で").color(NamedTextColor.DARK_GRAY),
                    Component.text("再生してしまいますと").color(NamedTextColor.DARK_GRAY),
                    Component.text("音漏れいたしますのでご注意ください").color(NamedTextColor.DARK_GRAY)
            ));
        });
        ItemData.ITEM_ID.set(stack,"walkman");
        return stack;
    }

    public static ItemStack musicSheet(){
        ItemStack stack = createItem(itemsData().getSheetMusicBlank());
        stack.editMeta(meta -> {
            meta.customName(Component.text("白紙の楽譜").color(NamedTextColor.AQUA));
        });
        ItemData.ITEM_ID.set(stack,"BLANK SHEET");
        return stack;
    }
}
