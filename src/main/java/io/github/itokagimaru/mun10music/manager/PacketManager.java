package io.github.itokagimaru.mun10music.manager;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PacketManager {
    public static void setFakeItemInSlot(Player player, int slot, ItemStack item) {
        setFakeItemInTopSlot(player, slot, item);
    }

    public static void setFakeItemInTopSlot(Player player, int slot, ItemStack item) {
        if (player == null || slot < 0) {
            return;
        }

        InventoryView view = player.getOpenInventory();
        int rawSlot = slot;

        if (view != null) {
            int topSize = view.getTopInventory().getSize();
            if (slot >= topSize) {
                // トップ範囲外は送信しない
                return;
            }
        }

        sendFakeItemPacket(player, rawSlot, item);
    }

    public static void setFakeItemInPlayerSlot(Player player, int slot, ItemStack item) {
        setFakeItemInPlayerInventorySlot(player, slot, item);
    }

    public static void setFakeItemInPlayerSlot(Player player, HashMap<Integer, ItemStack> slotItemMap) {
        if (player == null || slotItemMap == null) {
            return;
        }
        for (var entry : slotItemMap.entrySet()) {
            setFakeItemInPlayerInventorySlot(player, entry.getKey(), entry.getValue());
        }
    }

    public static void setFakeItemInPlayerInventorySlot(Player player, int slot, ItemStack item) {
        if (player == null || slot < 0) {
            return;
        }

        int invSize = player.getInventory().getSize();
        if (slot >= invSize) {
            // プレイヤーインベントリ範囲外は送信しない
            return;
        }

        InventoryView view = player.getOpenInventory();
        int rawSlot = slot;

        if (view != null) {
            int topSize = view.getTopInventory().getSize();
            rawSlot = topSize + slot;
        }

        sendFakeItemPacket(player, rawSlot, item);
    }

    private static void sendFakeItemPacket(Player player, int rawSlot, ItemStack item) {
        try {
            String craftBukkitPackage = getCraftBukkitPackage();
            Class<?> craftPlayerClass = Class.forName(craftBukkitPackage + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            ServerPlayer handle = (ServerPlayer) craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);

            Class<?> craftItemStackClass = Class.forName(craftBukkitPackage + ".inventory.CraftItemStack");
            net.minecraft.world.item.ItemStack nmsItem =
                    (item == null)
                            ? net.minecraft.world.item.ItemStack.EMPTY
                            : (net.minecraft.world.item.ItemStack) craftItemStackClass
                            .getMethod("asNMSCopy", ItemStack.class)
                            .invoke(null, item);

            AbstractContainerMenu menu = handle.containerMenu;
            int containerId = menu.containerId;
            int stateId = menu.incrementStateId();

            // 実体を変更せずにクライアント表示のみ更新
            handle.connection.send(new ClientboundContainerSetSlotPacket(containerId, stateId, rawSlot, nmsItem));
        } catch (Exception e) {
            Bukkit.getLogger().warning("フェイクアイテム送信に失敗: " + e.toString());
        }
    }

    private static String getCraftBukkitPackage() {
        // サーバークラスのパッケージからCraftBukkitのベースを取得
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String base = "org.bukkit.craftbukkit";
        if (packageName.startsWith(base + ".")) {
            return packageName;
        }
        return base;
    }
}
