package io.github.itokagimaru.mun10music.gui.menu.workspace;

import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.util.MakeItem;
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
    private final UUID frameUuid;

    public WorkspacesMenuHolder(UUID frameUuid) {
        this.frameUuid = frameUuid;
        inv = Bukkit.createInventory(this, 27, Component.text("WorkspacesMenuHolder"));
        setup();
    }
    public void setup() {
        ItemStack setting = new ItemStack(Material.FLOWER_BANNER_PATTERN);
        MakeItem.setItemMetaByColor(setting, "Setting", NamedTextColor.YELLOW, 0, ItemData.BUTTON_ID, "setting");
        inv.setItem(0, setting);
        ItemStack convert = new ItemStack(itemsData().getCassette().getMaterial());
        MakeItem.setItemMetaByColor(convert,"Convert", NamedTextColor.YELLOW, itemsData().getCassette().getCmd(), ItemData.BUTTON_ID,"convert");
        convert.lore(List.of(Component.text("楽譜をカセットテープに変換します")));
        inv.setItem(12, convert);

        ItemStack merge = new ItemStack(Material.ANVIL);
        MakeItem.setItemMetaByColor(merge, "Merge", NamedTextColor.YELLOW, 0, ItemData.BUTTON_ID,"merge");
        merge.lore(List.of(Component.text("カセットテープを結合します")));
        inv.setItem(15, merge);
    }

    @Override
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if(clicked == null) return;
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        switch (buttonId) {
            case "setting" -> {
                SettingHolder settingHolder = new SettingHolder(frameUuid);
                player.openInventory(settingHolder.getInventory());
            }
            case "convert" -> {
                ConvertMusicListHolder convertMusicListHolder = new ConvertMusicListHolder(player, frameUuid);
                player.openInventory(convertMusicListHolder.getInventory());
            }
            case "merge" -> {
                MergeHolder mergeHolder = new MergeHolder(frameUuid);
                player.openInventory(mergeHolder.getInventory());
            }
        }
    }
    @Override
    public void onClose(Player player) {}
}
