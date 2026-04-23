package io.github.itokagimaru.itokagimaru_daw.util;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.config.Items;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GetPresetItemStack {
    private static Items itemsData() {
        Itokagimaru_daw plugin = Itokagimaru_daw.getInstance();
        if (plugin == null) {
            throw new IllegalStateException("プラグインが初期化される前に呼び出されました。");
        }
        return plugin.getPluginConfigData().getItems();
    }

    private static ItemStack createItem(Items.Entry entry) {
        ItemStack stack = new ItemStack(entry.getMaterial());
        stack.editMeta(meta -> {
            // modelキーではなく custom model data で管理
            meta.setCustomModelData(entry.getCmd());
            meta.setMaxStackSize(1);
        });
        return stack;
    }

    public static ItemStack cassette(){

        ItemStack stack = createItem(itemsData().getCassette());
        stack.editMeta(meta -> {
            meta.customName(Component.text("カセットテープ").color(NamedTextColor.AQUA));
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
        ItemData.MUSIC_SAVED_RED.set(stack,new int[Itokagimaru_daw.MUSIC_LENGTH]);
        return stack;
    }

    public static ItemStack workSpace(){
        ItemStack stack = createItem(itemsData().getCassetteWorkspaceItem());
        stack.editMeta(meta -> {
            meta.customName(Component.text("カセットテープ編集台").color(NamedTextColor.AQUA));
            meta.lore(List.of(
                    Component.text("カセットテープ").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD,true)
                            .append(Component.text("を").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false)),
                    Component.text("作成,編集").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)
                            .append(Component.text("できる作業台").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false)),
                    Component.text(""),
                    Component.text("設置方法:地面に設置された").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false)
                            .append(Component.text("\"輝く額縁\"").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)
                                    .append(Component.text("に入れ").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))),
                    Component.text("　　　　右クリックすることで設置").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false),
                    Component.text("撤去方法:シフト右クリックで撤去").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false)
            ));
        });

        ItemData.ITEM_ID.set(stack,"CASSETTE_WORKSPACE_ITEM");
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

    public static ItemStack radio(){
        ItemStack stack = createItem(itemsData().getRadioItem());
        stack.editMeta(meta -> {
            meta.customName(Component.text("ラジカセ").color(NamedTextColor.AQUA));
            meta.lore(List.of(
                    Component.text("カセットテープを").color(NamedTextColor.WHITE),
                    Component.text("再生できるラジカセ").color(NamedTextColor.WHITE),
                    Component.text("周囲" + "5" + "ブロックの範囲のプレイヤーに聞こえるように演奏する").color(NamedTextColor.WHITE),
                    Component.text(""),
                    Component.text("設置方法:地面に設置された").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false)
                            .append(Component.text("\"輝く額縁\"").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)
                                    .append(Component.text("に入れ").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))),
                    Component.text("　　　　右クリックすることで設置").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false),
                    Component.text("撤去方法:シフト右クリックで撤去").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false)
            ));
        });
        ItemData.ITEM_ID.set(stack,"RADIO_ITEM");
        return stack;
    }
}
