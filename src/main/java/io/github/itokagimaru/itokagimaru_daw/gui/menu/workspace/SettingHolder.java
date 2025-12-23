package io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.util.GetPresetItemStack;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SettingHolder extends BaseGuiHolder {
    public UUID frameUuid;
    public void setUuid(UUID uuid){
        frameUuid = uuid;
    }

    public SettingHolder(){
        closeFlag = true;
        inv = Bukkit.createInventory(this, 27, Component.text("Setting"));
        setup();
    }
    public void setup(){
        ItemStack remove = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(remove, "撤去する", NamedTextColor.RED, "cassette_workspace_item", ItemData.BUTTON_ID, "remove");
        //inv.setItem(12, remove);仕様変更に伴い消しましたが、場合によっては戻すかもなのでこのままで

        ItemStack info = new ItemStack(Material.OAK_SIGN);
        MakeItem.setItemMetaByColor(info, "問合せ情報", NamedTextColor.YELLOW, null, ItemData.BUTTON_ID, "info");
        inv.setItem(12,info);

        ItemStack flow = new ItemStack(Material.FLOW_BANNER_PATTERN);
        MakeItem.setItemMetaByColor(flow, "回転させる", NamedTextColor.YELLOW, null, ItemData.BUTTON_ID, "rotate");
        inv.setItem(14, flow);

    }

    @Override
    public void onClick(InventoryClickEvent event){
        ItemStack clicked = event.getCurrentItem();
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        Player player = (Player) event.getWhoClicked();
        World world = player.getWorld();
        Entity fream = world.getEntity(frameUuid);
        if (!(fream instanceof GlowItemFrame glowFream))return;
        switch (buttonId){
            case "remove" -> {
                player.give(GetPresetItemStack.workSpace());
                glowFream.setItem(null);
                glowFream.remove();
                closeFlag = false;
                player.closeInventory();
            }
            case "rotate" -> {
                Rotation current = glowFream.getRotation();
                Rotation next = current.rotateClockwise();//45°回しているよ
                glowFream.setRotation(next);
            }
            case "info" -> {
                ItemStack icon = glowFream.getItem();
                String placerUuid = ItemData.UUID.get(icon);
                player.sendMessage(Component.text(
                        "--問合せ情報--\n" +
                                "world:" + world.getName() +"\n" +
                                "額縁uuid:" + frameUuid + "\n" +
                                "設置者uuid:" + placerUuid + "\n"
                ));
            }
        }
    }

    @Override
    public void onClose(Player player){
        if(!closeFlag)return;
        closeFlag = false;
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(),() -> {
            WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder();
            workspacesMenuHolder.setUuid(frameUuid);
            player.openInventory(workspacesMenuHolder.getInventory());
        });
    }
}
