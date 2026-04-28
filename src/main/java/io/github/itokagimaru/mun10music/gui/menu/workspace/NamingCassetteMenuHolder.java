package io.github.itokagimaru.mun10music.gui.menu.workspace;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.mun10music.util.MakeItem;
import io.github.itokagimaru.mun10music.util.PlaySound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class NamingCassetteMenuHolder extends BaseGuiHolder {
    ItemStack nullIcon = makeNullIcon();
    public ItemStack makeNullIcon(){
        ItemStack icon = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(icon, "未選択", NamedTextColor.RED, null, ItemData.BUTTON_ID, "nullIcon");
        List<Component> lore = List.of(
                Component.text("\"記録済みのカセットテープ\"を選択")
        );
        icon.lore(lore);
        return icon;
    }

    String name;
    int NAME_MAX_LENGTH = 16;

    UUID frameUuid;
    public void setUuid(UUID uuid){
        frameUuid = uuid;
    }

    public NamingCassetteMenuHolder(){
        inv = Bukkit.createInventory(this, 9, Component.text("NamingOfCassette"));
        setup();
    }

    public void setup(){
        closeFlag = true;

        ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        MakeItem.setItemMetaByColor(red,"キャンセル",NamedTextColor.RED,null,ItemData.BUTTON_ID,"cancel");
        inv.setItem(1, red);

        inv.setItem(3,nullIcon);

        ItemStack inputNameVal = new ItemStack(Material.WRITABLE_BOOK);
        MakeItem.setItemMetaByColor(inputNameVal,"名前を入力",NamedTextColor.YELLOW, null, ItemData.BUTTON_ID, "input");
        if(name == null){
            inputNameVal.lore(List.of(Component.text("名前は未入力です")));
        }else {
            inputNameVal.lore(List.of(Component.text("命名:\"" + name + "\"")));
        }
        inv.setItem(5, inputNameVal);

        ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        MakeItem.setItemMetaByColor(green, "名前を書き込み", NamedTextColor.GREEN, null, ItemData.BUTTON_ID, "naming");
        inv.setItem(7, green);
    }

    @Override
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        String itemId = ItemData.ITEM_ID.get(clicked);
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        switch (buttonId){
            case "cancel" ->{
                name = null;
                ItemStack cassette = inv.getItem(3).clone();
                ItemData.ITEM_ID.set(cassette,"recordCassette");
                setup();
                if (cassette == null || ("nullIcon").equals(ItemData.BUTTON_ID.get(cassette)))return;
                player.give(cassette);

            }
            case "input" -> {
                closeFlag = false;
                NamingAnvilGUI namingAnvilGUI = new NamingAnvilGUI();
                ItemStack cassette = inv.getItem(3).clone();
                namingAnvilGUI.setCassette(cassette);
                namingAnvilGUI.setFreamUuid(frameUuid);
                namingAnvilGUI.open(player);
            }
            case "naming" -> {
                ItemStack cassette = inv.getItem(3).clone();
                if (cassette == null || ("nullIcon").equals(ItemData.BUTTON_ID.get(cassette)))return;
                if (name == null)return;
                ItemData.ITEM_ID.set(cassette, "recordCassette");
                String recorder = ItemData.RECORDER.get(cassette);
                ItemData.IS_NAMED.set(cassette, (byte) 1);
                ItemData.MUSIC_NAME.set(cassette, name);
                int bpm = ItemData.BPM.get(cassette);
                cassette.lore(
                        List.of(Component.text("\"").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                                .append(Component.text(name).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)
                                                .append(Component.text("\" was recorded in this").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false))),
                                        Component.text("BPM: ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                                                .append(Component.text(bpm).color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true)),
                                        Component.text("recorded by ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                                                .append(Component.text(recorder).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true))
                        )
                );
                player.give(cassette);
                PlaySound.playSmithingTableUse(player);
                name = null;
                setup();
            }
        }
        if(("recordCassette").equals(itemId)){
            if (!("nullIcon").equals(ItemData.BUTTON_ID.get(inv.getItem(3))))return;
            if (ItemData.IS_NAMED.get(clicked) == (byte) 1)return;
            ItemStack cassetteIcon = clicked.clone();
            clicked.setAmount(0);
            ItemData.ITEM_ID.set(cassetteIcon,"cassetteIcon");
            inv.setItem(3,cassetteIcon);
        } else if (("cassetteIcon").equals(itemId)) {
            ItemStack returnItem = clicked.clone();
            ItemData.ITEM_ID.set(returnItem,"recordCassette");
            inv.setItem(3,nullIcon);
            player.give(returnItem);
        }
    }

    public void setName(String name){
        this.name = name;
        setup();
    }

    public void setCassette(ItemStack cassette){
        inv.setItem(3,cassette);
    }

    @Override
    public void onClose(Player player){
        if (!closeFlag)return;
        closeFlag = false;
        ItemStack returnItem = inv.getItem(3);
        if (!("nullIcon").equals(ItemData.BUTTON_ID.get(returnItem))){
            ItemData.ITEM_ID.set(returnItem,"recordCassette");
            player.give(returnItem);
        }
        Bukkit.getScheduler().runTask(Man10Music.getInstance(),() -> {
            WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder();
            workspacesMenuHolder.setUuid(frameUuid);
            player.openInventory(workspacesMenuHolder.getInventory());
        });
    }
}
