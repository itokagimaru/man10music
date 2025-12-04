package io.github.itokagimaru.itokagimaru_daw.gui.menu;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.manager.ByteArrayManager;
import io.github.itokagimaru.itokagimaru_daw.manager.MusicManager;
import io.github.itokagimaru.itokagimaru_daw.manager.PlayerMusicManager;
import io.github.itokagimaru.itokagimaru_daw.task.PlayMusic;
import io.github.itokagimaru.itokagimaru_daw.util.FakeEnchant;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemsOptionBpmHolder extends DawsOptionBpmHolder {
    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        switch (buttonId) {
            case "SET BPM" -> {
                int bpm = ItemData.BPM.get(clicked);
                ItemStack item = new ItemStack(Material.PAPER);
                MakeItem.setItemMeta(item, "記録済みのカセットテープ", null, "cassette_tape", ItemData.BPM, bpm);
                ItemData.BUTTON_ID.set(item, "RECORD ITEM");
                ItemMeta meta = item.getItemMeta();
                MusicManager musicManager = new MusicManager();
                int[] musicList = musicManager.loadMusicForPdc(player.getInventory().getItemInMainHand());
                ByteArrayManager byteArrayManager = new ByteArrayManager();
                byte[] data = byteArrayManager.encode(musicList);
                ItemData.BYTE_LIST.set(meta.getPersistentDataContainer(), data);
                meta.lore(List.of(Component.text("BPM:" + bpm), Component.text("recorded by " + player.getName())));
                item.setItemMeta(meta);
                FakeEnchant.addFakeEnchant(item);
                player.give(item);
                ItemData.FLAG.set(inv.getItem(0),(byte) 0);
                player.closeInventory();
            }
            case "SHIFT RIGHT" -> {
                int selectBpmId = getSelectBpmId(ItemData.BPM.get(inv.getItem(1)));
                selectBpmId += 1;
                if (selectBpmId > bpmList.length - 7) selectBpmId = bpmList.length - 7;
                int bpm = bpmList[selectBpmId];
                updateBpmIcons(bpm);
            }
            case "SHIFT LEFT" -> {
                int selectBpmId = getSelectBpmId(ItemData.BPM.get(inv.getItem(1)));
                selectBpmId -= 1;
                if (selectBpmId < 0) selectBpmId = 0;
                int bpm = bpmList[selectBpmId];
                updateBpmIcons(bpm);
            }
        }
    }
    @Override
    public void onClose(Player player) {
        if (ItemData.FLAG.get(inv.getItem(0)) == (byte) 0) return;
        ItemStack cassetteTape = new ItemStack(Material.PAPER);
        MakeItem.setItemMeta(cassetteTape,"カセットテープ",null,"cassette_tape", ItemData.ITEM_ID,"CASSETTE TAPE");
        player.getInventory().addItem(cassetteTape);
    }
}
