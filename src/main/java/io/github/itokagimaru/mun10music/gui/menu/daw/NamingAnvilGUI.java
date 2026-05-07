package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicManager;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;

import java.util.List;

public class NamingAnvilGUI {
    AnvilView anvilInv;
    Boolean closeFlag = true;//onCloseを動かすかどうかのflag
    Music music;

    public NamingAnvilGUI(Music music) {
        this.music = music;
    }

    public void open(Player player){
        anvilInv = MenuType.ANVIL.builder()
                .title(Component.text("InputNameOfMusic")) // GUI の上部タイトル
                .location(player.getLocation()) // プレイヤー視点位置
                .checkReachable(false) // 本物のブロック必要なし
                .build(player);// プレイヤー向けにビュー作成
        setup();
        anvilInv.open();
        AnvilGUIOpening.anvilOpening.put(player.getUniqueId(), this);
    }
    public void setup(){
        ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        MakeItem.setItemMetaByColor(green, "名前を入力", NamedTextColor.GREEN,null,ItemData.BUTTON_ID,"decision");
        green.lore(List.of(Component.text("クリックで決定")));
        anvilInv.setItem(0,green);
        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMeta(bar,"",null,0,null,null);
        anvilInv.setItem(1,bar);
    }

    public void onClick(InventoryClickEvent event){// こいつだけ処理が特殊なため、キャンセルは処理が終わってから行うこと
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        String input = anvilInv.getRenameText();

        if (("decision").equals(ItemData.BUTTON_ID.get(clicked))){
            anvilInv.setItem(0,null);
            anvilInv.setItem(1,null);
            event.setCancelled(true);
            closeFlag = false;
            music.setTitle(input);
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                MusicManager.saveMusicToDb(Man10Music.getInstance().getMySQLManager(), music)
                        .thenAccept(musicID -> Bukkit.getScheduler().runTask(Man10Music.getInstance(), () ->
                        {
                            if (musicID >= 0) {
                                MusicEditMenuHolder musicEditMenuHolder = new MusicEditMenuHolder(music);
                                player.openInventory(musicEditMenuHolder.getInventory());
                            } else {
                                player.sendMessage(Component.text("楽曲の保存に失敗しました").color(NamedTextColor.RED));
                                player.closeInventory();
                            }
                        }));
            });
        }else{
            event.setCancelled(true);
        }
    }

    public void onClose(Player player){
        anvilInv.setItem(0,null);
        anvilInv.setItem(1,null);
        if (!closeFlag)return;
        closeFlag = false;
        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
            MusicEditMenuHolder musicEditMenuHolder = new MusicEditMenuHolder(music);
            player.openInventory(musicEditMenuHolder.getInventory());
        });
    }
}
