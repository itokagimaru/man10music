package io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import io.github.itokagimaru.itokagimaru_daw.util.PlaySound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChangeBpmHolder extends BaseGuiHolder {
    final public int[] bpmList = {1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 16, 20, 24, 25, 30, 40, 48, 50, 60, 75, 80, 100, 120, 150, 200, 240, 300, 400, 600, 1200};
    public int selectedBpmId;
    public int prevBpm;
    ItemStack nullIcon = makeNullIcon();
    public ItemStack makeNullIcon(){
        ItemStack icon = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(icon, "未選択", NamedTextColor.RED, null, ItemData.BUTTON_ID, "nullIcon");
        icon.lore(List.of(Component.text("\"記録済みのカセットテープ\"を選択してください")));
        return icon;
    }

    UUID frameUuid;
    public void setUuid(UUID uuid){
        frameUuid = uuid;
    }

    public ChangeBpmHolder(){
        closeFlag = true;
        inv = Bukkit.createInventory(this,9, Component.text("ChangeBPM"));
        setup();
    }

    public void setup(){
        for (int i = 0; i < 9; i++){
            inv.setItem(i,null);
        }
        inv.setItem(1,nullIcon);
        ItemStack left = new ItemStack(Material.PAPER);
        ItemStack right = new ItemStack(Material.PAPER);
        MakeItem.setItemMeta(left, "", null, "next_b_left", ItemData.BUTTON_ID, "SHIFT LEFT");
        MakeItem.setItemMeta(right, "", null, "next_b_right", ItemData.BUTTON_ID, "SHIFT RIGHT");
        inv.setItem(3, left);
        inv.setItem(8, right);
    }

    public void updateBpmIcons(int bpm) {
        getSelectBpmId(bpm);
        ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        if (selectedBpmId > bpmList.length - 4) selectedBpmId = bpmList.length - 4;
        for (int i = 0; i < 4; i++) {
            if(isCanChange(bpmList[selectedBpmId + i])){
                MakeItem.setItemMetaByColor(green, "set:" + bpmList[selectedBpmId + i], NamedTextColor.GREEN, null, ItemData.BPM, bpmList[selectedBpmId + i]);
                ItemData.BUTTON_ID.set(green, "SET BPM");
                inv.setItem(i + 4, green);
            }else{
                MakeItem.setItemMetaByColor(red, "set:" + bpmList[selectedBpmId + i], NamedTextColor.RED, null, ItemData.BPM, bpmList[selectedBpmId + i]);
                inv.setItem(i + 4, red);
            }
        }
    }

    public void getSelectBpmId(int bpm) {
        selectedBpmId = 0;
        for (int i = 0; i < bpmList.length; i++) {
            if (bpm == bpmList[i]) {
                selectedBpmId = i;
            }
        }
    }

    public int[] changeBpm(int[] music , int newBpm){
        double scale = (double) newBpm / prevBpm;
        int[] newMusic = new int[Itokagimaru_daw.MUSIC_LENGTH];
        int writePos = 0;
        for (int note : music){
            if (note == -1){
                newMusic[writePos] = -1;
                break;
            }
            if(note != 0) {
                newMusic[writePos] = note;
                writePos++;
                for (int i = 0;i < scale -1;i++){
                    newMusic[writePos] = 0;
                    writePos++;
                }
                continue;
            }
            for (int i = 0;i < scale;i++){
                newMusic[writePos] = 0;
                writePos++;
            }
        }
        return newMusic;
    }

    public boolean isCanChange(int newBpm){
        double scale = (double) newBpm/prevBpm;
        return Math.abs(scale - Math.round(scale)) <= 1e-9 && newBpm != prevBpm;
    }

    @Override
    public void onClick(InventoryClickEvent event){
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        String itemId = ItemData.ITEM_ID.get(clicked);
        switch (buttonId) {
            case "SET BPM" -> {
                ItemStack returnItem = Objects.requireNonNull(inv.getItem(1)).clone();
                if (("nullIcon").equals(ItemData.BUTTON_ID.get(returnItem)))return;
                int newBpm = ItemData.BPM.get(clicked);
                if (!isCanChange(newBpm))return;
                prevBpm = ItemData.BPM.get(returnItem);
                int[] music = ItemData.MUSIC_SAVED_RED.get(returnItem);
                int[] newMusic = changeBpm(music, newBpm);
                ItemData.MUSIC_SAVED_RED.set(returnItem,newMusic);
                ItemData.ITEM_ID.set(returnItem,"recordCassette");
                ItemData.BPM.set(returnItem,newBpm);
                returnItem.lore(
                        List.of(Component.text("\"" + ItemData.MUSIC_NAME.get(returnItem) + "\" was recorded in this"),
                                Component.text("BPM:" + newBpm),
                                Component.text("recorded by " + ItemData.RECORDER.get(returnItem)))
                );
                player.give(returnItem);
                PlaySound.playCompassLock(player);
                setup();
            }
            case "SHIFT RIGHT" -> {
                if(inv.getItem(4) == null)return;
                getSelectBpmId(ItemData.BPM.get(inv.getItem(4)));
                selectedBpmId += 1;
                if (selectedBpmId > bpmList.length - 4) selectedBpmId = bpmList.length - 4;
                int bpm = bpmList[selectedBpmId];
                updateBpmIcons(bpm);
            }
            case "SHIFT LEFT" -> {
                if(inv.getItem(4) == null)return;
                getSelectBpmId(ItemData.BPM.get(inv.getItem(4)));
                selectedBpmId -= 1;
                if (selectedBpmId < 0) selectedBpmId = 0;
                int bpm = bpmList[selectedBpmId];
                updateBpmIcons(bpm);
            }
        }
        if(("recordCassette").equals(itemId)){
            if (!("nullIcon").equals(ItemData.BUTTON_ID.get(inv.getItem(1))))return;
            if (ItemData.IS_NAMED.get(clicked) == (byte) 0)return;
            if (ItemData.IS_MERGED.get(clicked) == (byte) 1)return;
            ItemStack cassetteIcon = clicked.clone();
            clicked.setAmount(0);
            ItemData.ITEM_ID.set(cassetteIcon,"cassetteIcon");
            inv.setItem(1,cassetteIcon);
            prevBpm = ItemData.BPM.get(cassetteIcon);
            updateBpmIcons(ItemData.BPM.get(cassetteIcon));
        } else if (("cassetteIcon").equals(itemId)) {
            ItemStack returnItem = clicked.clone();
            ItemData.ITEM_ID.set(returnItem,"recordCassette");
            player.give(returnItem);
            setup();
        }

    }
    @Override
    public void onClose(Player player){
        if(!closeFlag)return;
        closeFlag = false;
        ItemStack returnItem = Objects.requireNonNull(inv.getItem(1)).clone();
        if (!("nullIcon").equals(ItemData.BUTTON_ID.get(returnItem))) {
            ItemData.ITEM_ID.set(returnItem,"recordCassette");
            player.give(returnItem);
        }
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(),() -> {
            WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder();
            workspacesMenuHolder.setUuid(frameUuid);
            player.openInventory(workspacesMenuHolder.getInventory());
        });

    }
}
