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

public class MergeHolder extends BaseGuiHolder {
    ItemStack[] cassettes = new ItemStack[4];

    public MergeHolder() {
        inv = Bukkit.createInventory(this, 9, "Merge Menu");
        setup();
    }

    public void setup(){
        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(bar,"未選択", NamedTextColor.RED, null, null, null);
        bar.lore(List.of(Component.text("カセットテープを選択してください")));
        for(int i = 0; i < 5; i++){
            inv.setItem(i + 1, bar);
        }
        ItemStack green = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        MakeItem.setItemMetaByColor(green, "結合！！", NamedTextColor.GREEN, null, ItemData.BUTTON_ID, "decision");
        inv.setItem(8,green);
    }

    @Override
    public void onClick(InventoryClickEvent event) {

    }
    @Override
    public void onClose(Player  player) {

    }
}
