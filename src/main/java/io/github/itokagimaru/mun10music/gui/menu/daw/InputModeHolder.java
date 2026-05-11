package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.manager.InventoryManager;
import io.github.itokagimaru.mun10music.manager.PacketManager;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicManager;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InputModeHolder extends BaseGuiHolder {
    final Inventory playerInventory = Bukkit.createInventory(null, 36);
    int[] musicList;
    Music music;
    int selectedSlot = 0;
    int page = 1;
    int topNote = 1;
    Player clickedPlayer;

    HashMap<Integer, BottomIcon> bottomIcons = new HashMap<Integer, BottomIcon>();

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
        INSERT_REST("INSERT REST"),
        CUT_NOTE("CUT NOTE"),
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

    public InputModeHolder(Music music) {
        this.music = music;
        this.musicList = music.getMusic();
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

        ItemStack gray = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        MakeItem.setItemMeta(gray, "", null, 0, null, null);
        for (int i = 0; i < 36; i++) {
            bottomIcons.put(i, new BottomIcon(gray.clone(), () -> {}));
        }

        setSelectedSlot(0);

        MakeItem.setItemMeta(base, "1ページへ", null, icons.getTriangleLeft().getCmd(), ItemData.BUTTON_ID, "GO FIRST");
        bottomIcons.put(27, new BottomIcon(base.clone(), () -> {
            jumpPage(1);
        }));

        MakeItem.setItemMeta(base, "前のページへ", null, icons.getTriangleLeft().getCmd(), ItemData.BUTTON_ID, "BACK PAGE");
        bottomIcons.put(28, new BottomIcon(base.clone(), this::jumpBackPage));

        MakeItem.setItemMeta(base, "次のページへ", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "NEXT PAGE");
        bottomIcons.put(30, new BottomIcon(base.clone(), this::jumpNextPage));

        MakeItem.setItemMeta(base, "最後のページへ", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "GO END");
        bottomIcons.put(31, new BottomIcon(base.clone(), () -> {
            setMusicEndpoint();
            int endPage = getMusicFinalPage();
            jumpPage(endPage);
        }));

        MakeItem.setItemMeta(base, "上へスクロール", null, icons.getTriangleUp().getCmd(), ItemData.BUTTON_ID, "UP NOTE");
        bottomIcons.put(9, new BottomIcon(base.clone(), () -> {
            inputGuiUpdate(-1);
        }));
        MakeItem.setItemMeta(base, "下へスクロール", null, icons.getTriangleDown().getCmd(), ItemData.BUTTON_ID, "DOWN NOTE");
        bottomIcons.put(18, new BottomIcon(base.clone(), () -> {
            inputGuiUpdate(1);
        }));

        ItemStack sign = new ItemStack(Material.OAK_HANGING_SIGN);
        MakeItem.setItemMeta(sign, "現在1ページ目", null, 0, ItemData.PAGE, 1);
        bottomIcons.put(29, new BottomIcon(sign.clone(), null));

        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(bar, "しゅうりょう", NamedTextColor.DARK_RED, 0, ItemData.BUTTON_ID, "CLOSE");
        bottomIcons.put(35, new BottomIcon(bar.clone(), this::close));

        String[] whiteName = {"休符", "null", "ド/C", "レ/D", "ミ/E", "ファ/F", "ソ/G", "ラ/A", "シ/B"};
        for (int i = 0; i < whiteName.length; i++) {
            if (!whiteName[i].equals("null")) {
                ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                MakeItem.setItemMeta(white, whiteName[i], null, 0, ItemData.BUTTON_ID, whiteName[i]);
                final NoteSpec noteSpec = NOTE_SPECS.get(whiteName[i]);
                bottomIcons.put(i + 17, new BottomIcon(white.clone(), () -> {
                    applyNoteSpec(noteSpec);
                    moveNextSelectedSlot();
                }));
            }
        }

        String[] blackName = {"ド#/C#", "レ#/D#", "null", "ファ#/F#", "ソ#/G#", "ラ#/A#"};
        for (int i = 0; i < blackName.length; i++) {
            if (!blackName[i].equals("null")) {
                ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                MakeItem.setItemMeta(black, blackName[i], null, 0, ItemData.BUTTON_ID, blackName[i]);
                final NoteSpec noteSpec = NOTE_SPECS.get(blackName[i]);
                bottomIcons.put(i + 10, new BottomIcon(black.clone(), () -> {
                    applyNoteSpec(noteSpec);
                    moveNextSelectedSlot();
                }));
            }
        }

        ItemStack structure = new ItemStack(Material.STRUCTURE_VOID);
        MakeItem.setItemMeta(structure, "全削除", null, 0, ItemData.BUTTON_ID, "ALL DELETE");
        bottomIcons.put(34, new BottomIcon(structure.clone(), () -> {
            int[] reset = new int[Man10Music.MUSIC_LENGTH];
            Arrays.fill(reset, 0);
            musicList = reset;

            inputGuiUpdate(0);
        }));

        ItemStack insertRest = new ItemStack(Material.WRITABLE_BOOK);
        MakeItem.setItemMetaByColor(insertRest, "休符の挿入", NamedTextColor.YELLOW, 0, ItemData.BUTTON_ID, "INSERT REST");
        bottomIcons.put(32, new BottomIcon(insertRest.clone(), () -> {
            int index = getMusicIndex();
            insertRestKeepingLength(index);
            inputGuiUpdate(0);
        }));


        ItemStack cutNote = new ItemStack(Material.SHEARS);
        MakeItem.setItemMetaByColor(cutNote, "ノートの切り取り", NamedTextColor.RED, 0, ItemData.BUTTON_ID, "CUT NOTE");
        bottomIcons.put(33, new BottomIcon(cutNote.clone(), () -> {
            int index = getMusicIndex();
            cutNoteKeepingLength(index);
            inputGuiUpdate(0);
        }));
    }

    public void open(Player player) {
        inputGuiUpdate(0);
        player.openInventory(this.inv);
        updateFakeItemInPlayerInventorySlot(player);
    }

    public void inputGuiUpdate(int offset) {
        int noteId;

        if (musicList == null) {
            return;
        }
        topNote += offset;

        ItemStack paper = new ItemStack(icons().getBaseMaterial());
        for (int i = 0; i <= 53; i++) {
            MakeItem.setItemMeta(paper, "", null, icons().getNoteBlank().getCmd(), null, null);
            this.inv.setItem(i, paper);
        }
        for (int i = 0; i <= 5; i++) {
            noteId = topNote + i;
            paper.setAmount(7 - (noteId + 2) / 6);
            while (noteId >= 7) noteId -= 6;
            MakeItem.setItemMeta(paper, "", null, scaleCmd(icons(), noteId), ItemData.TOP_NOTE, topNote);
            this.inv.setItem(i * 9, paper);
        }
        paper.setAmount(1);
        for (int i = 0; i < 8; i++) {
            int note = musicList[i + ((page - 1) * 8)];
            if (note != 0) {
                if (note % 2 == 1) {
                    MakeItem.setItemMeta(paper, "", null, icons().getNoteUp().getCmd(), null, null);
                } else {
                    MakeItem.setItemMeta(paper, "", null, icons().getNoteDown().getCmd(), null, null);
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

    public void jumpPage(int page) {
        this.page = page;
        if (page < 1 || page > Man10Music.MAX_PAGE) return;
        ItemStack pageViewer = new ItemStack(Material.OAK_HANGING_SIGN);
        MakeItem.setItemMeta(pageViewer, "現在" + page + "ページ目", null, 0, ItemData.PAGE, page);
        bottomIcons.put(29, new BottomIcon(pageViewer.clone(), () -> {}));
        inputGuiUpdate(0);
        setSelectedSlot(0);
        updateFakeItemInPlayerInventorySlot(clickedPlayer);
    }

    private void jumpNextPage() {
        jumpPage(page + 1);
    }

    private void jumpBackPage() {
        jumpPage(page - 1);
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
        clickedPlayer = (Player) event.getWhoClicked();
        int rawSlot = event.getRawSlot();

        int slot = rawSlot - 54;
        if (slot < 0) slot += 45;

        if (0 > slot) onClickInTopInventory(event);
        else onClickInBottomInventory(slot);
        updateFakeItemInPlayerInventorySlot(clickedPlayer);
    }

    private void onClickInBottomInventory(int slot) {
        BottomIcon bottomIcon = bottomIcons.get(slot);
        if (bottomIcon != null) {
            bottomIcon.runClickAction();
        }
    }

    private void onClickInTopInventory(InventoryClickEvent event) {
    }

    @Override
    public void onClose(Player player) {
        if (!closeFlag) return;
        setMusicEndpoint();
        InventoryManager inventoryManager = Man10Music.getInstance().inventoryManager;
        inventoryManager.loadInventory(player);
        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
            player.closeInventory();
            MainMenuHolder mainMenuHolder = new MainMenuHolder();
            player.openInventory(mainMenuHolder.getInventory());
            player.updateInventory();
        });
        music.setMusic(musicList);
        MusicManager.saveMusicToDb(Man10Music.getInstance().getMySQLManager(), music);
    }

    private void close(){
        clickedPlayer.closeInventory();
    }

    private Icons icons() {
        return iconsData();
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

//    private boolean isNoteKey(ItemStack clicked) {
//        Material type = clicked.getType();
//        return type == Material.WHITE_STAINED_GLASS_PANE || type == Material.BLACK_STAINED_GLASS_PANE;
//    }

//    private void handleNoteInput(Player player, Inventory clickedInv, ItemStack clicked) {
//        Integer page = getCurrentPage(clickedInv);
//        Integer topnote = getCurrentTopNote(player);
//        if (page == null || topnote == null) {
//            return;
//        }
//
//        int select = getSelectedCursor(clickedInv);
//        applyNoteSpec(ItemData.BUTTON_ID.get(clicked), page, select, topnote);
//        inputGuiUpdate(topnote, page);
//        advanceSelection(player, page, topnote, select);
//    }

//    private void advanceSelection(int page, int topnote, int select) {
//        ItemStack updateSelect = new ItemStack(icons().getBaseMaterial());
//        MakeItem.setItemMeta(updateSelect, "カーソルを移動", null, icons().getTriangleUp().getCmd(), ItemData.BUTTON_ID, "SELECT");
//        player.getInventory().setItem(select + 9, updateSelect);
//
//        if (select >= 8) {
//            jumpPage(page + 1, topnote);
//            return;
//        }
//
//        select++;
//        MakeItem.setItemMeta(updateSelect, "選択中", null, icons().getTriangleSelect().getCmd(), ItemData.BUTTON_ID, "SELECTED");
//        player.getInventory().setItem(select + 9, updateSelect);
//    }

    private void applyNoteSpec(NoteSpec spec) {
        if (spec == null || musicList == null) {
            return;
        }

        int index = getMusicIndex();

        if (spec.restToggle()) {
            musicList[index] = musicList[index] != 0 ? 0 : 1;
            return;
        }

        if (spec.skipWhenTopNoteZero() && topNote == 0) {
            return;
        }

        int add = Math.min((topNote + spec.addOffset()) / 6, spec.addCap());
        if (spec.forceAddZeroWhenTopNoteZero() && topNote == 0) {
            add = 0;
        }
        musicList[index] = spec.baseValue() + (12 * add);
        inputGuiUpdate(0);
    }

    private int getMusicIndex() {
        return (page - 1) * 8 + selectedSlot;
    }

    private void insertRestKeepingLength(int index) {
        if (musicList == null || musicList.length == 0) {
            return;
        }
        if (index < 0 || index >= musicList.length) {
            return;
        }

        for (int i = musicList.length - 1; i > index; i--) {
            musicList[i] = musicList[i - 1];
        }
        musicList[index] = 0;
    }

    private void cutNoteKeepingLength(int index) {
        if (musicList == null || musicList.length == 0) {
            return;
        }
        if (index < 0 || index >= musicList.length) {
            return;
        }

        for (int i = index; i < musicList.length - 1; i++) {
            musicList[i] = musicList[i + 1];
        }
        musicList[musicList.length - 1] = 0;
    }

    private void setSelectedSlot(int slot){
        if (slot < 0 || slot >= 8) slot = 0;
        selectedSlot = slot;

        ItemStack selected = new ItemStack(icons().getBaseMaterial());
        MakeItem.setItemMeta(selected, "選択中", null, icons().getTriangleSelect().getCmd(), null, null);

        ItemStack select = new ItemStack(icons().getBaseMaterial());
        MakeItem.setItemMeta(select, "カーソルを移動", null, icons().getTriangleUp().getCmd(), null, null);
        for (int i = 0; i < 8; i++){
            if (i == selectedSlot){
                bottomIcons.put(i + 1, new BottomIcon(selected, () -> {
                }));

            } else {
                int finalI = i;
                bottomIcons.put(i + 1, new BottomIcon(select, () -> {
                    setSelectedSlot(finalI);
                }));
            }
        }
    }

    private void moveNextSelectedSlot(){
        selectedSlot = selectedSlot + 1;
        if (selectedSlot >= 8) {
            jumpNextPage();
            return;
        }
        setSelectedSlot(selectedSlot);
    }

    private void updateFakeItemInPlayerInventorySlot(Player player) {
        HashMap<Integer, ItemStack> fakeItemInPlayerInventorySlot = new HashMap<>();
        for (var entry : bottomIcons.entrySet()) {
            fakeItemInPlayerInventorySlot.put(entry.getKey(), entry.getValue().getIcon());
        }
        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
            PacketManager.setFakeItemInPlayerSlot(player, fakeItemInPlayerInventorySlot);
        });
    }
}

class BottomIcon {
    private final ItemStack icon;
    private final Runnable clickAction;
    BottomIcon(ItemStack icon, Runnable clickAction) {
        this.icon = icon;
        this.clickAction = clickAction;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void runClickAction() {
        if (clickAction == null) return;
        clickAction.run();
    }
}
