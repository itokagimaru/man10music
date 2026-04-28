package io.github.itokagimaru.mun10music.gui.menu.workspace;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
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
import java.util.UUID;

public class NamingAnvilGUI {
    AnvilView anvilInv;
    ItemStack cassette;
    UUID freamUuid;
    Boolean closeFlag = true;//onCloseを動かすかどうかのflag

    public void setCassette(ItemStack stack){
        cassette = stack;
    }

    public void setFreamUuid(UUID uuid){
        freamUuid = uuid;
    }

    public void setUuid(UUID uuid){
        freamUuid = uuid;
    }


    public void open(Player player){
        anvilInv = MenuType.ANVIL.builder()
                .title(Component.text("InputNameOfCassette")) // GUI の上部タイトル
                .location(player.getLocation()) // プレイヤー視点位置
                .checkReachable(false) // 本物のブロック必要なし
                .build(player);// プレイヤー向けにビュー作成
        setup();
        anvilInv.open();
        AnvilGUIOpening.anvilOpening.put(player.getUniqueId(), this);
    }
    public void setup(){
        Icons icons = Man10Music.getInstance().getPluginConfigData().getIcons();
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
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                NamingCassetteMenuHolder namingCassetteMenuHolder = new NamingCassetteMenuHolder();
                namingCassetteMenuHolder.setName(input);
                namingCassetteMenuHolder.setCassette(cassette);
                namingCassetteMenuHolder.setUuid(freamUuid);
                player.openInventory(namingCassetteMenuHolder.getInventory());
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
            NamingCassetteMenuHolder namingCassetteMenuHolder = new NamingCassetteMenuHolder();
            namingCassetteMenuHolder.setCassette(cassette);
            namingCassetteMenuHolder.setUuid(freamUuid);
            player.openInventory(namingCassetteMenuHolder.getInventory());
        });
    }
}
