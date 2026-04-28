package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Itokagimaru_daw;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.mun10music.manager.SheetMusicManager;
import io.github.itokagimaru.mun10music.util.GetPresetItemStack;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class MusicSheetExportHolder extends BaseGuiHolder {
    ItemStack daw;
    private final int mainSlot;

    public MusicSheetExportHolder(ItemStack daw, int mainSlot){
        this.daw = daw;
        this.mainSlot = mainSlot;
        inv = Bukkit.createInventory(this, 9, Component.text("MusicSheetSave"));
        setup();
    }

    public void setup(){
        ItemStack air = new ItemStack(Material.AIR);
        for (int i = 0; i < 9;i++){
            inv.setItem(i, air);
        }
        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(bar,"未選択",NamedTextColor.RED,null, null,null);
        bar.lore(List.of(Component.text("\"白紙の楽譜\"を選択").color(NamedTextColor.WHITE)));
        inv.setItem(4, bar);
    }

    @Override
    public void onClick(InventoryClickEvent event){
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (Objects.equals(ItemData.ITEM_ID.get(clicked), "BLANK SHEET")){
            if(("SELECT SHEET").equals(ItemData.ITEM_ID.get(inv.getItem(4)))) return;
            setup();
            ItemStack exportedSheet = clicked.clone();
            clicked.setAmount(0);
            ItemData.ITEM_ID.set(exportedSheet,"SELECT SHEET");
            inv.setItem(4, exportedSheet);
            ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            MakeItem.setItemMetaByColor(green,"決定",NamedTextColor.GREEN,null, ItemData.BUTTON_ID,"DECISION");
            inv.setItem(7, green);
            ItemStack red =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
            MakeItem.setItemMetaByColor(red,"キャンセル", NamedTextColor.RED,null, ItemData.BUTTON_ID,"CANCEL");
            inv.setItem(1, red);
        } else if (Objects.equals(ItemData.ITEM_ID.get(clicked), "SELECT SHEET")) {
            onClose(player);
            setup();
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "DECISION")) {
            ItemStack check = inv.getItem(4);
            if (Objects.equals(ItemData.ITEM_ID.get(check), "SELECT SHEET")) {
                ItemStack returnItem = SheetMusicManager.makeSheetMusic(player, daw);
                if(returnItem == null) return;
                player.getInventory().addItem(returnItem);
                check.setAmount(0);
                setup();
            }
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "CANCEL")) {
            setup();
        }
    }
    @Override
    public void onClose(Player player){
        if (!closeFlag) return;
        closeFlag = false;
        ItemStack check = inv.getItem(4);
        if (check != null) {
            if (Objects.equals(ItemData.ITEM_ID.get(check), "SELECT SHEET")) {
                GetPresetItemStack.musicSheet();
                player.getInventory().addItem(GetPresetItemStack.musicSheet());
            }
        }
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(), () -> {
            MusicMenuHolder musicMenuHolder = new MusicMenuHolder(daw, mainSlot);
            player.openInventory(musicMenuHolder.getInventory());
        });
    }


}
