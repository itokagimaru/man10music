package io.github.itokagimaru.itokagimaru_daw.gui.menu;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.manager.InventoryManager;
import io.github.itokagimaru.itokagimaru_daw.manager.MusicManager;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;

public class InputModeHolder extends BaseGuiHolder {
    final Inventory playerInventory = Bukkit.createInventory(null, 36);

    public InputModeHolder() {
        this.inv = Bukkit.createInventory(this, 54, Component.text("InputMode"));
        setup();
    }

    public void setup() {
        ItemStack paper = new ItemStack(Material.PAPER);
        for (int i = 0; i <= 53; i++) {
            MakeItem.setItemMeta(paper, "", null, "note_def", null, null);
            this.inv.setItem(i, paper);
        }
        for (int i = 0; i <= 5; i++) {
            MakeItem.setItemMeta(paper, "", null, "key_board_" + i, null, null);
            this.inv.setItem(i * 9, paper);
        }
        ItemStack gray = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        MakeItem.setItemMeta(gray, "", null, null, null, null);
        for (int i = 0; i < 36; i++) {
            playerInventory.setItem(i, gray);
        }
        MakeItem.setItemMeta(paper, "選択中", null, "select", ItemData.BUTTON_ID, "SELECTED");
        playerInventory.setItem(10, paper);
        for (int i = 1; i <= 7; i++) {
            MakeItem.setItemMeta(paper, "カーソルを移動", null, "next_b_up", ItemData.BUTTON_ID, "SELECT");
            playerInventory.setItem(10 + i, paper);
        }
        MakeItem.setItemMeta(paper, "1ページへ", null, "next_b_left", ItemData.BUTTON_ID, "GO FIRST");
        playerInventory.setItem(0, paper);
        MakeItem.setItemMeta(paper, "前のページへ", null, "next_b_left", ItemData.BUTTON_ID, "BACK PAGE");
        playerInventory.setItem(1, paper);

        MakeItem.setItemMeta(paper, "次のページへ", null, "next_b_right", ItemData.BUTTON_ID, "NEXT PAGE");
        playerInventory.setItem(3, paper);
        MakeItem.setItemMeta(paper, "最後のページへ", null, "next_b_right", ItemData.BUTTON_ID, "GO END");
        playerInventory.setItem(4, paper);
        MakeItem.setItemMeta(paper, "上へスクロール", null, "next_b_up", ItemData.BUTTON_ID, "UP NOTE");
        playerInventory.setItem(18, paper);
        MakeItem.setItemMeta(paper, "下へスクロール", null, "next_b_down", ItemData.BUTTON_ID, "DOWN NOTE");
        playerInventory.setItem(27, paper);

        ItemStack sign = new ItemStack(Material.OAK_HANGING_SIGN);
        MakeItem.setItemMeta(sign, "現在1ページ目", null, null, ItemData.PAGE, 1);
        playerInventory.setItem(2, sign);

        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(bar, "しゅうりょう", NamedTextColor.DARK_RED, null, ItemData.BUTTON_ID, "CLOSE");
        playerInventory.setItem(8, bar);

        String[] whiteName = {"休符", "null", "ド/C", "レ/D", "ミ/E", "ファ/F", "ソ/G", "ラ/A", "シ/B"};
        for (int i = 0; i < whiteName.length; i++) {
            if (!whiteName[i].equals("null")) {
                ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                MakeItem.setItemMeta(white, whiteName[i], null, null, ItemData.BUTTON_ID, whiteName[i]);
                playerInventory.setItem(i + 26, white);
            }
        }

        String[] blackName = {"ド#/C#", "レ#/D#", "null", "ファ#/F#", "ソ#/G#", "ラ#/A#"};
        for (int i = 0; i < blackName.length; i++) {
            if (!blackName[i].equals("null")) {
                ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                MakeItem.setItemMeta(black, blackName[i], null, null, ItemData.BUTTON_ID, blackName[i]);
                playerInventory.setItem(i + 19, black);
            }
        }

        ItemStack structure = new ItemStack(Material.STRUCTURE_VOID);
        MakeItem.setItemMeta(structure, "全削除", null, null, ItemData.BUTTON_ID, "ALL DELETE");
        playerInventory.setItem(6, structure);
    }

    public void open(Player player) {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.saveInventory(player);
        ItemStack pdcHolder = player.getInventory().getItemInMainHand().clone();
        player.getInventory().clear();
        player.getInventory().setContents(playerInventory.getContents());
        inputGuiUpdate(pdcHolder, 0, 1);
        ItemStack musicHolder = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        MusicManager.saveMusicForPdc(musicHolder,MusicManager.loadMusicForPdc(pdcHolder));
        player.getInventory().setItem(9,musicHolder);
        player.openInventory(this.inv);
    }

    public void inputGuiUpdate(ItemStack pdcHolder, Integer topNote, Integer page) {
        MusicManager music = new MusicManager();
        int[] musicList = music.loadMusicForPdc(pdcHolder);
        int noteId;

        if (topNote == null || musicList == null) {
            return;
        }

        ItemStack paper = new ItemStack(Material.PAPER);
        for (int i = 0; i <= 53; i++) {
            MakeItem.setItemMeta(paper, "", null, "note_def", null, null);
            this.inv.setItem(i, paper);
        }
        for (int i = 0; i <= 5; i++) {
            noteId = topNote + i;
            paper.setAmount(7 - (noteId + 2) / 6);
            while (noteId >= 7) noteId -= 6;
            MakeItem.setItemMeta(paper, topNote + "/" + noteId, null, "key_board_" + noteId, ItemData.TOP_NOTE, topNote);
            this.inv.setItem(i * 9, paper);
        }
        paper.setAmount(1);
        for (int i = 0; i < 8; i++) {
            int note = musicList[i + ((page - 1) * 8)];
            if (note != 0) {
                if (note % 2 == 1) {
                    MakeItem.setItemMeta(paper, "", null, "note_up", null, null);
                } else {
                    MakeItem.setItemMeta(paper, "", null, "note_dw", null, null);
                }
                if (note > topNote * 2 && note <= (topNote + 6) * 2) {
                    note -= topNote * 2;
                    if (note == 25) note -= 24;
                    else if (note >= 13) note -= 12;
                    if (note % 2 == 1) {
                        note += 1;
                    }
                    this.inv.setItem(i + 1 + (9 * note / 2 - 9), paper);
                }
            }
        }
    }

    public void jumpPage(int page, int topNote, Player player , ItemStack pdcHolder) {
        ItemStack pageItem = player.getInventory().getItem(2);
        if (page < 1 || page > Itokagimaru_daw.MAX_PAGE) return;
        MakeItem.setItemMeta(pageItem, "現在" + page + "ページ目", null, null, ItemData.PAGE, page);
        inputGuiUpdate(pdcHolder, topNote, page);
        ItemStack paper = new ItemStack(Material.PAPER);
        MakeItem.setItemMeta(paper, "選択中", null, "select", ItemData.BUTTON_ID, "SELECTED");
        player.getInventory().setItem(10, paper);
        for (int i = 1; i <= 7; i++) {
            MakeItem.setItemMeta(paper, "カーソルを移動", null, "next_b_up", ItemData.BUTTON_ID, "SELECT");
            player.getInventory().setItem(10 + i, paper);
        }
    }

    public int[] setMusicEndpoint(int[] loadedMusic) {
        for (int i = 0; i < loadedMusic.length; i++) {//エンドポイントの削除
            if (loadedMusic[i] == -1) loadedMusic[i] = 0;
        }
        int music_end_point = 0;
        for (int i = loadedMusic.length; i > 0; i--) {//エンドポイントの追加
            if (loadedMusic[i - 1] != 0) {
                music_end_point = i;
                break;
            }
        }
        if (!(music_end_point >= loadedMusic.length)) loadedMusic[music_end_point] = -1;
        return loadedMusic;
    }

    public int getMusicFinalPage(int[] loadedMusic) {
        int endPoint = 0;
        for (int i = 0; i < loadedMusic.length; i++) {//エンドポイントの削除
            if (loadedMusic[i] == -1) endPoint = i;
        }
        return endPoint / 8 + 1;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = clicked.getItemMeta();
        Inventory clickedInv = event.getClickedInventory();
        Inventory plInv = player.getInventory();
        if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "CLOSE")) {
            player.closeInventory();
        } else if (clicked.getType() == Material.WHITE_STAINED_GLASS_PANE) {
            MusicManager musicManager = new MusicManager();
            int[] lodedMusic = musicManager.loadMusicForPdc(player.getInventory().getItem(9));
            ItemStack pageItem = clickedInv.getItem(2);
            int page = ItemData.PAGE.get(pageItem);
            ItemStack topnoteItem = player.getOpenInventory().getTopInventory().getItem(0);
            int topnote = ItemData.TOP_NOTE.get(topnoteItem);
            int select = 1;
            int add;
            for (int i = 1; i <= 8; i++) {
                ItemStack selectItem = clickedInv.getItem(i + 9);
                if (Objects.equals(ItemData.BUTTON_ID.get(selectItem), "SELECTED")) {
                    select = i;
                }
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "ド/C")) {
                add = Math.min((topnote + 2) / 6, 5);
                Objects.requireNonNull(lodedMusic)[(page - 1) * 8 + select - 1] = 8 + (12 * add);
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "レ/D")) {
                add = Math.min((topnote + 3) / 6, 5);
                Objects.requireNonNull(lodedMusic)[(page - 1) * 8 + select - 1] = 6 + (12 * add);
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "ミ/E")) {
                add = Math.min((topnote + 4) / 6, 5);
                Objects.requireNonNull(lodedMusic)[(page - 1) * 8 + select - 1] = 4 + (12 * add);
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "ファ/F")) {
                add = Math.min((topnote + 4) / 6, 5);
                Objects.requireNonNull(lodedMusic)[(page - 1) * 8 + select - 1] = 3 + (12 * add);

            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "ソ/G")) {
                if (topnote != 0) {
                    add = Math.min((topnote - 1) / 6, 5);
                    Objects.requireNonNull(lodedMusic)[(page - 1) * 8 + select - 1] = 13 + add * 12;
                }
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "ラ/A")) {
                add = Math.min((topnote) / 6, 5);
                Objects.requireNonNull(lodedMusic)[(page - 1) * 8 + select - 1] = 11 + (12 * add);
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "シ/B")) {
                add = Math.min((topnote + 1) / 6, 5);
                Objects.requireNonNull(lodedMusic)[(page - 1) * 8 + select - 1] = 9 + (12 * add);
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "休符"))
                Objects.requireNonNull(lodedMusic)[(page - 1) * 8 + select - 1] = 0;
            //"ド/C","レ/D","ミ/E","ファ/F","ソ/G","ラ/A","シ/B"


            ItemStack pdcHolder = player.getInventory().getItem(9);
            MusicManager.saveMusicForPdc(pdcHolder, lodedMusic.clone());
            inputGuiUpdate(pdcHolder, topnote, page);

            ItemStack update_select = new ItemStack(Material.PAPER);
            MakeItem.setItemMeta(update_select, "カーソルを移動", null, "next_b_up", ItemData.BUTTON_ID, "SELECT");
            player.getInventory().setItem(select + 9, update_select);

            if (select >= 8) {
                jumpPage(page + 1, topnote, player, pdcHolder);
                return;
            }
            select++;
            MakeItem.setItemMeta(update_select, "選択中", null, "select", ItemData.BUTTON_ID, "SELECTED");
            player.getInventory().setItem(select + 9, update_select);
        } else if (clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            int[] loadedMusic = MusicManager.loadMusicForPdc(player.getInventory().getItem(9));
            ItemStack pageItem = clickedInv.getItem(2);
            int page = ItemData.PAGE.get(pageItem);
            ItemStack topnoteItem = player.getOpenInventory().getTopInventory().getItem(0);
            int topnote = ItemData.TOP_NOTE.get(topnoteItem);
            int add;
            int select = 1;
            for (int i = 1; i <= 8; i++) {
                ItemStack select_item = clickedInv.getItem(i + 9);
                if (Objects.requireNonNull(select_item).getType() == Material.PAPER && Objects.requireNonNull(select_item.getItemMeta().getItemModel()).equals(NamespacedKey.minecraft("select"))) {
                    select = i;
                }
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "ド#/C#")) {
                add = Math.min((topnote + 2) / 6, 5);
                Objects.requireNonNull(loadedMusic)[(page - 1) * 8 + select - 1] = 7 + (12 * add);
            }

            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "レ#/D#")) {
                add = Math.min((topnote + 3) / 6, 5);
                Objects.requireNonNull(loadedMusic)[(page - 1) * 8 + select - 1] = 5 + (12 * add);
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "ファ#/F#")) {
                add = Math.min((topnote + 5) / 6, 6);
                if (topnote == 0) add = 0;

                Objects.requireNonNull(loadedMusic)[(page - 1) * 8 + select - 1] = 2 + (12 * add);
            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "ソ#/G#")) {
                add = Math.min((topnote) / 6, 5);
                Objects.requireNonNull(loadedMusic)[(page - 1) * 8 + select - 1] = 12 + (12 * add);

            }
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "ラ#/A#")) {
                add = Math.min((topnote + 1) / 6, 5);
                Objects.requireNonNull(loadedMusic)[(page - 1) * 8 + select - 1] = 10 + (12 * add);
            }

            ItemStack pdcHolder = player.getInventory().getItem(9);
            MusicManager.saveMusicForPdc(pdcHolder, loadedMusic.clone());
            inputGuiUpdate(pdcHolder, topnote, page);
            ItemStack update_select = new ItemStack(Material.PAPER);
            MakeItem.setItemMeta(update_select, "カーソルを移動", null, "next_b_up", ItemData.BUTTON_ID, "SELECT");
            player.getInventory().setItem(select + 9, update_select);
            if (select >= 8) {
                jumpPage(page + 1, topnote, player, pdcHolder);
                return;
            }
            select++;
            MakeItem.setItemMeta(update_select, "選択中", null, "select", ItemData.BUTTON_ID, "SELECTED");
            player.getInventory().setItem(select + 9, update_select);
        } else if (clicked.getType() == Material.PAPER) {
            if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "UP NOTE")) {

                ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);

                int topnote = ItemData.TOP_NOTE.get(topnote_item);

                ItemStack page_item = clickedInv.getItem(2);
                int tag_int = ItemData.PAGE.get(page_item);
                if (topnote <= 0) topnote += 1;
                inputGuiUpdate(player.getInventory().getItem(9), topnote - 1, tag_int);
            } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "DOWN NOTE")) {
                ItemStack page_item = clickedInv.getItem(2);
                int tag_int = ItemData.PAGE.get(page_item);

                ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                int topnote = ItemData.TOP_NOTE.get(topnote_item);
                if (topnote >= 31) topnote -= 1;
                inputGuiUpdate(player.getInventory().getItem(9), topnote + 1, tag_int);

            } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "SELECT")) {
                MakeItem.setItemMeta(clicked, "選択中", null, "select", ItemData.BUTTON_ID, "SELECTED");
                ItemStack paper = new ItemStack(Material.PAPER);
                int slot = event.getRawSlot() - 55;
                for (int i = 0; i <= 7; i++) {
                    if (i != slot) {
                        MakeItem.setItemMeta(paper, "カーソルを移動", null, "next_b_up", ItemData.BUTTON_ID, "SELECT");
                        player.getInventory().setItem(10 + i, paper);
                    }
                }
            } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "NEXT PAGE")) {

                ItemStack page_item = Objects.requireNonNull(clickedInv).getItem(2);
                if (clickedInv == plInv) {
                    ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                    int topnote = ItemData.TOP_NOTE.get(topnote_item);
                    int page = ItemData.PAGE.get(Objects.requireNonNull(page_item));
                    jumpPage(page + 1, topnote, player,player.getInventory().getItem(9));
                }

            } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "BACK PAGE")) {

                ItemStack page_item = clickedInv.getItem(2);
                ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                int topnote = ItemData.TOP_NOTE.get(topnote_item);
                int page = ItemData.PAGE.get(Objects.requireNonNull(page_item));
                jumpPage(page - 1, topnote, player,player.getInventory().getItem(9));

            } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "GO FIRST")) {
                ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                int topnote = ItemData.TOP_NOTE.get(topnote_item);
                jumpPage(1, topnote, player, player.getInventory().getItem(9));
            } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "GO END")) {
                ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                int topnote = ItemData.TOP_NOTE.get(topnote_item);
                ItemStack pdcHolder = player.getInventory().getItem(9);
                MusicManager.saveMusicForPdc(pdcHolder,setMusicEndpoint(MusicManager.loadMusicForPdc(pdcHolder)));
                int endPage = getMusicFinalPage(MusicManager.loadMusicForPdc(pdcHolder));
                jumpPage(endPage, topnote, player,pdcHolder);
            }
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked),"ALL DELETE")) {
            int[] reset = new int[256];
            Arrays.fill(reset, 0);
            MusicManager musicManager = new MusicManager();
            musicManager.saveMusicForPdc(player.getInventory().getItem(9),reset);

            ItemStack page_item = clickedInv.getItem(2);
            Integer page = ItemData.PAGE.get(page_item);

            ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
            int topnote = ItemData.TOP_NOTE.get(topnote_item);
            inputGuiUpdate(player.getInventory().getItem(9), topnote, page);
        } else {
        }
    }

    @Override
    public void onClose(Player player) {
        ItemStack pdcHolder = player.getInventory().getItem(9);
        int[] music = setMusicEndpoint(MusicManager.loadMusicForPdc(pdcHolder));
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.loadInventory(player);
        ItemStack daw = player.getInventory().getItemInMainHand();
        MusicManager.saveMusicForPdc(daw,music);
    }
}
