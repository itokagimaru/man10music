package io.github.itokagimaru.itokagimaru_daw.gui.menu.daw;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.config.Icons;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.InventoryManager;
import io.github.itokagimaru.itokagimaru_daw.manager.MusicManager;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class InputModeHolder extends BaseGuiHolder {
    final Inventory playerInventory = Bukkit.createInventory(null, 36);
    ItemStack daw;
    int[] musicList;

    private enum ButtonId {
        CLOSE("CLOSE"),
        UP_NOTE("UP NOTE"),
        DOWN_NOTE("DOWN NOTE"),
        SELECT("SELECT"),
        NEXT_PAGE("NEXT PAGE"),
        BACK_PAGE("BACK PAGE"),
        GO_FIRST("GO FIRST"),
        GO_END("GO END"),
        ALL_DELETE("ALL DELETE"),
        UNKNOWN("");

        private final String id;

        ButtonId(String id) {
            this.id = id;
        }

        static ButtonId fromId(String value) {
            if (value == null) {
                return UNKNOWN;
            }
            for (ButtonId buttonId : values()) {
                if (buttonId.id.equals(value.trim())) {
                    return buttonId;
                }
            }
            return UNKNOWN;
        }
    }

    private record NoteSpec(
            int baseValue,
            int addOffset,
            int addCap,
            boolean skipWhenTopNoteZero,
            boolean forceAddZeroWhenTopNoteZero,
            boolean restToggle
    ) {}

    private static final Map<String, NoteSpec> NOTE_SPECS = Map.ofEntries(
            Map.entry("ド/C", new NoteSpec(8, 2, 5, false, false, false)),
            Map.entry("レ/D", new NoteSpec(6, 3, 5, false, false, false)),
            Map.entry("ミ/E", new NoteSpec(4, 4, 5, false, false, false)),
            Map.entry("ファ/F", new NoteSpec(3, 4, 5, false, false, false)),
            Map.entry("ソ/G", new NoteSpec(13, -1, 5, true, false, false)),
            Map.entry("ラ/A", new NoteSpec(11, 0, 5, false, false, false)),
            Map.entry("シ/B", new NoteSpec(9, 1, 5, false, false, false)),
            Map.entry("休符", new NoteSpec(0, 0, 0, false, false, true)),
            Map.entry("ド#/C#", new NoteSpec(7, 2, 5, false, false, false)),
            Map.entry("レ#/D#", new NoteSpec(5, 3, 5, false, false, false)),
            Map.entry("ファ#/F#", new NoteSpec(2, 5, 6, false, true, false)),
            Map.entry("ソ#/G#", new NoteSpec(12, 0, 5, false, false, false)),
            Map.entry("ラ#/A#", new NoteSpec(10, 1, 5, false, false, false))
    );

    public InputModeHolder(ItemStack daw) {
        this.daw = daw;
        this.inv = Bukkit.createInventory(this, 54, Component.text("InputMode"));
        setup();
    }

    public void setup() {
        Icons icons = icons();
        ItemStack base = new ItemStack(icons.getBaseMaterial());

        for (int i = 0; i <= 53; i++) {
            MakeItem.setItemMeta(base, "", null, icons.getNoteBlank().getCmd(), null, null);
            this.inv.setItem(i, base);
        }
        for (int i = 0; i <= 5; i++) {
            MakeItem.setItemMeta(base, "", null, scaleCmd(icons, i), null, null);
            this.inv.setItem(i * 9, base);
        }
        ItemStack gray = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        MakeItem.setItemMeta(gray, "", null, 0, ItemData.BUTTON_ID, "flager");
        for (int i = 0; i < 36; i++) {
            playerInventory.setItem(i, gray);
        }
        MakeItem.setItemMeta(base, "選択中", null, icons.getTriangleSelect().getCmd(), ItemData.BUTTON_ID, "SELECTED");
        playerInventory.setItem(10, base);
        for (int i = 1; i <= 7; i++) {
            MakeItem.setItemMeta(base, "カーソルを移動", null, icons.getTriangleUp().getCmd(), ItemData.BUTTON_ID, "SELECT");
            playerInventory.setItem(10 + i, base);
        }
        MakeItem.setItemMeta(base, "1ページへ", null, icons.getTriangleLeft().getCmd(), ItemData.BUTTON_ID, "GO FIRST");
        playerInventory.setItem(0, base);
        MakeItem.setItemMeta(base, "前のページへ", null, icons.getTriangleLeft().getCmd(), ItemData.BUTTON_ID, "BACK PAGE");
        playerInventory.setItem(1, base);

        MakeItem.setItemMeta(base, "次のページへ", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "NEXT PAGE");
        playerInventory.setItem(3, base);
        MakeItem.setItemMeta(base, "最後のページへ", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "GO END");
        playerInventory.setItem(4, base);
        MakeItem.setItemMeta(base, "上へスクロール", null, icons.getTriangleUp().getCmd(), ItemData.BUTTON_ID, "UP NOTE");
        playerInventory.setItem(18, base);
        MakeItem.setItemMeta(base, "下へスクロール", null, icons.getTriangleDown().getCmd(), ItemData.BUTTON_ID, "DOWN NOTE");
        playerInventory.setItem(27, base);

        ItemStack sign = new ItemStack(Material.OAK_HANGING_SIGN);
        MakeItem.setItemMeta(sign, "現在1ページ目", null, 0, ItemData.PAGE, 1);
        playerInventory.setItem(2, sign);

        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(bar, "しゅうりょう", NamedTextColor.DARK_RED, 0, ItemData.BUTTON_ID, "CLOSE");
        playerInventory.setItem(8, bar);

        String[] whiteName = {"休符", "null", "ド/C", "レ/D", "ミ/E", "ファ/F", "ソ/G", "ラ/A", "シ/B"};
        for (int i = 0; i < whiteName.length; i++) {
            if (!whiteName[i].equals("null")) {
                ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                // TODO: 鍵盤(白鍵)の専用cmdはIcons未定義のためblank cmdを利用する。
                MakeItem.setItemMeta(white, whiteName[i], null, 0, ItemData.BUTTON_ID, whiteName[i]);
                playerInventory.setItem(i + 26, white);
            }
        }

        String[] blackName = {"ド#/C#", "レ#/D#", "null", "ファ#/F#", "ソ#/G#", "ラ#/A#"};
        for (int i = 0; i < blackName.length; i++) {
            if (!blackName[i].equals("null")) {
                ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                MakeItem.setItemMeta(black, blackName[i], null, 0, ItemData.BUTTON_ID, blackName[i]);
                playerInventory.setItem(i + 19, black);
            }
        }

        ItemStack structure = new ItemStack(Material.STRUCTURE_VOID);
        MakeItem.setItemMeta(structure, "全削除", null, 0, ItemData.BUTTON_ID, "ALL DELETE");
        playerInventory.setItem(6, structure);
    }

    public void open(Player player) {
        InventoryManager inventoryManager = Itokagimaru_daw.getInstance().inventoryManager;
        inventoryManager.saveInventory(player);
        ItemStack pdcHolder = this.daw.clone();
        player.getInventory().clear();
        player.getInventory().setContents(playerInventory.getContents());
        musicList = MusicManager.loadMusicForPdc(pdcHolder);
        inputGuiUpdate(0, 1);
        player.openInventory(this.inv);
    }

    public void inputGuiUpdate(Integer topNote, Integer page) {
        int noteId;

        if (topNote == null || musicList == null) {
            return;
        }

        Icons icons = icons();
        ItemStack paper = new ItemStack(icons.getBaseMaterial());
        for (int i = 0; i <= 53; i++) {
            MakeItem.setItemMeta(paper, "", null, icons.getNoteBlank().getCmd(), null, null);
            this.inv.setItem(i, paper);
        }
        for (int i = 0; i <= 5; i++) {
            noteId = topNote + i;
            paper.setAmount(7 - (noteId + 2) / 6);
            while (noteId >= 7) noteId -= 6;
            MakeItem.setItemMeta(paper, "", null, scaleCmd(icons, noteId), ItemData.TOP_NOTE, topNote);
            this.inv.setItem(i * 9, paper);
        }
        paper.setAmount(1);
        for (int i = 0; i < 8; i++) {
            int note = musicList[i + ((page - 1) * 8)];
            if (note != 0) {
                if (note % 2 == 1) {
                    MakeItem.setItemMeta(paper, "", null, icons.getNoteUp().getCmd(), null, null);
                } else {
                    MakeItem.setItemMeta(paper, "", null, icons.getNoteDown().getCmd(), null, null);
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

    public void jumpPage(int page, int topNote, Player player) {
        ItemStack pageItem = player.getInventory().getItem(2);
        if (page < 1 || page > Itokagimaru_daw.MAX_PAGE) return;
        if (pageItem == null) return;
        MakeItem.setItemMeta(pageItem, "現在" + page + "ページ目", null, 0, ItemData.PAGE, page);
        inputGuiUpdate(topNote, page);
        ItemStack paper = new ItemStack(icons().getBaseMaterial());
        MakeItem.setItemMeta(paper, "選択中", null, icons().getTriangleSelect().getCmd(), ItemData.BUTTON_ID, "SELECTED");
        player.getInventory().setItem(10, paper);
        for (int i = 1; i <= 7; i++) {
            MakeItem.setItemMeta(paper, "カーソルを移動", null, icons().getTriangleUp().getCmd(), ItemData.BUTTON_ID, "SELECT");
            player.getInventory().setItem(10 + i, paper);
        }
    }

    public void setMusicEndpoint() {
        for (int i = 0; i < musicList.length; i++) {//エンドポイントの削除
            if (musicList[i] == -1) musicList[i] = 0;
        }
        int music_end_point = 0;
        for (int i = musicList.length; i > 0; i--) {//エンドポイントの追加
            if (musicList[i - 1] != 0) {
                music_end_point = i;
                break;
            }
        }
        if (!(music_end_point >= musicList.length)) musicList[music_end_point] = -1;
    }

    public int getMusicFinalPage() {
        int endPoint = 0;
        for (int i = 0; i < musicList.length; i++) {
            if (musicList[i] == -1) endPoint = i;
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
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }
        Inventory plInv = player.getInventory();
        ButtonId buttonId = ButtonId.fromId(ItemData.BUTTON_ID.get(clicked));

        switch (buttonId) {
            case CLOSE -> player.closeInventory();
            case UP_NOTE -> {
                Integer topnote = getCurrentTopNote(player);
                Integer page = getCurrentPage(clickedInv);
                if (topnote == null || page == null) {
                    return;
                }
                if (topnote <= 0) {
                    topnote += 1;
                }
                inputGuiUpdate(topnote - 1, page);
            }
            case DOWN_NOTE -> {
                Integer page = getCurrentPage(clickedInv);
                Integer topnote = getCurrentTopNote(player);
                if (topnote == null || page == null) {
                    return;
                }
                if (topnote >= 31) {
                    topnote -= 1;
                }
                inputGuiUpdate(topnote + 1, page);
            }
            case SELECT -> {
                MakeItem.setItemMeta(clicked, "選択中", null, icons().getTriangleSelect().getCmd(), ItemData.BUTTON_ID, "SELECTED");
                ItemStack paper = new ItemStack(icons().getBaseMaterial());
                int slot = event.getRawSlot() - 55;
                for (int i = 0; i <= 7; i++) {
                    if (i != slot) {
                        MakeItem.setItemMeta(paper, "カーソルを移動", null, icons().getTriangleUp().getCmd(), ItemData.BUTTON_ID, "SELECT");
                        player.getInventory().setItem(10 + i, paper);
                    }
                }
            }
            case NEXT_PAGE -> {
                if (clickedInv == plInv) {
                    Integer topnote = getCurrentTopNote(player);
                    Integer page = getCurrentPage(clickedInv);
                    if (topnote == null || page == null) {
                        return;
                    }
                    jumpPage(page + 1, topnote, player);
                }
            }
            case BACK_PAGE -> {
                Integer topnote = getCurrentTopNote(player);
                Integer page = getCurrentPage(clickedInv);
                if (topnote == null || page == null) {
                    return;
                }
                jumpPage(page - 1, topnote, player);
            }
            case GO_FIRST -> {
                Integer topnote = getCurrentTopNote(player);
                if (topnote == null) {
                    return;
                }
                jumpPage(1, topnote, player);
            }
            case GO_END -> {
                Integer topnote = getCurrentTopNote(player);
                if (topnote == null) {
                    return;
                }
                setMusicEndpoint();
                int endPage = getMusicFinalPage();
                jumpPage(endPage, topnote, player);
            }
            case ALL_DELETE -> {
                int[] reset = new int[Itokagimaru_daw.MUSIC_LENGTH];
                Arrays.fill(reset, 0);
                musicList = reset;

                Integer page = getCurrentPage(clickedInv);
                Integer topnote = getCurrentTopNote(player);
                if (page == null || topnote == null) {
                    return;
                }
                inputGuiUpdate(topnote, page);
            }
            case UNKNOWN -> {
                if (isNoteKey(clicked)) {
                    handleNoteInput(player, clickedInv, clicked);
                }
            }
        }
    }

    @Override
    public void onClose(Player player) {
        if (!closeFlag) return;
        setMusicEndpoint();
        InventoryManager inventoryManager = Itokagimaru_daw.getInstance().inventoryManager;
        inventoryManager.loadInventory(player);
        MusicManager.saveMusicForPdc(daw,musicList);
        //daw.lore(List.of(Component.text(Arrays.toString(musicList))));
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(), () -> {
           MainMenuHolder mainMenuHolder = new MainMenuHolder(daw);
           player.openInventory(mainMenuHolder.getInventory());
        });
    }

    private Icons icons() {
        return Itokagimaru_daw.getInstance().getIconsData();
    }

    private int scaleCmd(Icons icons, int noteId) {
        return switch (noteId) {
            case 0 -> icons.getScaleNullToFSharp().getCmd();
            case 1 -> icons.getScaleFToE().getCmd();
            case 2 -> icons.getScaleDSharpToD().getCmd();
            case 3 -> icons.getScaleCSharpToC().getCmd();
            case 4 -> icons.getScaleBToASharp().getCmd();
            case 5 -> icons.getScaleAToGSharp().getCmd();
            default -> icons.getScaleGToFSharp().getCmd();
        };
    }

    private int getSelectedCursor(Inventory clickedInv) {
        int select = 1;
        for (int i = 1; i <= 8; i++) {
            ItemStack selectItem = clickedInv.getItem(i + 9);
            if (selectItem != null && Objects.equals(ItemData.BUTTON_ID.get(selectItem), "SELECTED")) {
                 select = i;
             }
        }
        return select;
    }

    private Integer getCurrentPage(Inventory inventory) {
        ItemStack pageItem = inventory.getItem(2);
        if (pageItem == null) {
            return null;
        }
        return ItemData.PAGE.get(pageItem);
    }

    private Integer getCurrentTopNote(Player player) {
        ItemStack topnoteItem = player.getOpenInventory().getTopInventory().getItem(0);
        if (topnoteItem == null) {
            return null;
        }
        return ItemData.TOP_NOTE.get(topnoteItem);
    }

    private boolean isNoteKey(ItemStack clicked) {
        Material type = clicked.getType();
        return type == Material.WHITE_STAINED_GLASS_PANE || type == Material.BLACK_STAINED_GLASS_PANE;
    }

    private void handleNoteInput(Player player, Inventory clickedInv, ItemStack clicked) {
        Integer page = getCurrentPage(clickedInv);
        Integer topnote = getCurrentTopNote(player);
        if (page == null || topnote == null) {
            return;
        }

        int select = getSelectedCursor(clickedInv);
        applyNoteSpec(ItemData.BUTTON_ID.get(clicked), page, select, topnote);
        inputGuiUpdate(topnote, page);
        advanceSelection(player, page, topnote, select);
    }

    private void advanceSelection(Player player, int page, int topnote, int select) {
        ItemStack updateSelect = new ItemStack(icons().getBaseMaterial());
        MakeItem.setItemMeta(updateSelect, "カーソルを移動", null, icons().getTriangleUp().getCmd(), ItemData.BUTTON_ID, "SELECT");
        player.getInventory().setItem(select + 9, updateSelect);

        if (select >= 8) {
            jumpPage(page + 1, topnote, player);
            return;
        }

        select++;
        MakeItem.setItemMeta(updateSelect, "選択中", null, icons().getTriangleSelect().getCmd(), ItemData.BUTTON_ID, "SELECTED");
        player.getInventory().setItem(select + 9, updateSelect);
    }

    private void applyNoteSpec(String buttonId, int page, int select, int topnote) {
        NoteSpec spec = NOTE_SPECS.get(buttonId);
        if (spec == null || musicList == null) {
            return;
        }

        int index = (page - 1) * 8 + select - 1;

        if (spec.restToggle()) {
            musicList[index] = musicList[index] != 0 ? 0 : 1;
            return;
        }

        if (spec.skipWhenTopNoteZero() && topnote == 0) {
            return;
        }

        int add = Math.min((topnote + spec.addOffset()) / 6, spec.addCap());
        if (spec.forceAddZeroWhenTopNoteZero() && topnote == 0) {
            add = 0;
        }
        musicList[index] = spec.baseValue() + (12 * add);
    }
}
