package io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class WorkspacesMenuHolder extends BaseGuiHolder {
    UUID frameUuid;
    public void setUuid(UUID uuid){
        frameUuid = uuid;
    }

    public WorkspacesMenuHolder() {
        inv = Bukkit.createInventory(this, 27, Component.text("WorkspacesMenuHolder"));
        setup();
    }
    public void setup() {

        ItemStack setting = new ItemStack(Material.FLOWER_BANNER_PATTERN);
        MakeItem.setItemMetaByColor(setting, "Setting", NamedTextColor.YELLOW, null, ItemData.BUTTON_ID, "setting");
        inv.setItem(0, setting);

        ItemStack convert = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(convert,"Convert", NamedTextColor.YELLOW, "cassette_tape", ItemData.BUTTON_ID,"convert");
        convert.lore(List.of(Component.text("楽譜をカセットテープに変換します")));
        inv.setItem(10, convert);

        ItemStack naming = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(naming, "Naming", NamedTextColor.YELLOW, "name_tag", ItemData.BUTTON_ID,"naming");
        naming.lore(List.of(Component.text("カセットテープに名前を付けます")));
        inv.setItem(12, naming);

        ItemStack merge = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(merge, "Merge", NamedTextColor.YELLOW, "anvil", ItemData.BUTTON_ID,"merge");
        merge.lore(List.of(Component.text("カセットテープを結合します")));
        inv.setItem(14, merge);

        ItemStack changeBpm = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(changeBpm,"ChangeBPM", NamedTextColor.YELLOW,"clock",ItemData.BUTTON_ID,"changeBpm");
        changeBpm.lore(List.of(Component.text("カセットテープのBPMを設定,変更します")));
        inv.setItem(16, changeBpm);
    }

    @Override
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        switch (buttonId) {
            case "setting" -> {
                SettingHolder settingHolder = new SettingHolder();
                settingHolder.setUuid(frameUuid);
                player.openInventory(settingHolder.getInventory());
            }
            case "convert" -> {
                ConvertMenuHolder convertMenuHolder = new ConvertMenuHolder(60);
                convertMenuHolder.setUuid(frameUuid);
                player.openInventory(convertMenuHolder.getInventory());
            }
            case "naming" -> {
                NamingCassetteMenuHolder namingCassetteMenuHolder = new NamingCassetteMenuHolder();
                namingCassetteMenuHolder.setUuid(frameUuid);
                player.openInventory(namingCassetteMenuHolder.getInventory());
            }
            case "merge" -> {
                MergeHolder mergeHolder = new MergeHolder();
                mergeHolder.setUuid(frameUuid);
                player.openInventory(mergeHolder.getInventory());
            }
            case "changeBpm" -> {
                ChangeBpmHolder changeBpmHolder = new ChangeBpmHolder();
                changeBpmHolder.setUuid(frameUuid);
                player.openInventory(changeBpmHolder.getInventory());

            }
        }
    }
    @Override
    public void onClose(Player player) {}
}
