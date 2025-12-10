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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class WorkspacesMenuHolder extends BaseGuiHolder {
    public WorkspacesMenuHolder() {
        inv = Bukkit.createInventory(this, 9, Component.text("WorkspacesMenuHolder"));
        setup();
    }
    public void setup() {
        ItemStack convert = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(convert,"Convert", NamedTextColor.YELLOW, "cassette_tape", ItemData.BUTTON_ID,"convert");
        convert.lore(List.of(Component.text("楽譜をカセットテープに変換します")));
        inv.setItem(1, convert);

        ItemStack naming = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(naming, "Naming", NamedTextColor.YELLOW, "writable_book", ItemData.BUTTON_ID,"naming");
        naming.lore(List.of(Component.text("カセットテープに名前を付けます")));
        inv.setItem(3, naming);

        ItemStack merge = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(merge, "Merge", NamedTextColor.YELLOW, "anvil", ItemData.BUTTON_ID,"merge");
        merge.lore(List.of(Component.text("カセットテープを結合します")));
        inv.setItem(5, merge);

        ItemStack changeBpm = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(changeBpm,"ChangeBPM", NamedTextColor.YELLOW,"clock",ItemData.BUTTON_ID,"change_bpm");
        changeBpm.lore(List.of(Component.text("カセットテープのBPMを設定,変更します")));
        inv.setItem(7, changeBpm);
    }

    @Override
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        Inventory clicked_inv = event.getClickedInventory();
        if(Objects.equals(buttonId, "convert")){
            ConvertManuHolder convertManuHolder = new ConvertManuHolder(60);
            player.openInventory(convertManuHolder.getInventory());
        }
        else if (Objects.equals(buttonId, "naming")) {

        }
        else if (Objects.equals(buttonId, "merge")) {
            MergeHolder mergeHolder = new MergeHolder();
            player.openInventory(mergeHolder.getInventory());
        }
        else if (Objects.equals(buttonId, "change_bpm")) {

        }
    }
    @Override
    public void onClose(Player player) {
    }
}
