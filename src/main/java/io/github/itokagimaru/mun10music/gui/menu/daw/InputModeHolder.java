package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.manager.AutPlayManager;
import io.github.itokagimaru.mun10music.manager.PacketManager;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicManager;
import io.github.itokagimaru.mun10music.manager.music.Track;
import io.github.itokagimaru.mun10music.task.PlayMusic;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InputModeHolder extends BaseGuiHolder {
    int[] musicList;
    Music music;
    Track track;

    boolean isPlaying = false; //操作を受け付けるか

    int selectedSlot = 0;
    int page = 1;
    int topNote = 0;
    private int getNowSelectedSlot() {
        return selectedSlot;
    }

    private int getNowPage() {
        return page;
    }

    private int getNowTopNote() {
        return topNote;
    }

    HashMap<Integer, BottomIcon> bottomIcons = new HashMap<Integer, BottomIcon>();

    private static final List<Track> TRACK_ORDER = List.of(Track.RED, Track.AQUA, Track.GREEN, Track.YELLOW);

    private enum ButtonId {
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

    public InputModeHolder(Music music, Track track) {
        this.music = music;
        this.musicList = music.getMusic(track);
        this.track = track;
        this.inv = Bukkit.createInventory(this, 54, Component.text("InputMode").color(NamedTextColor.WHITE));
        setup();
    }

    private NamedTextColor getTitleColor(Track track) {
        switch (track) {
            case RED -> {return NamedTextColor.RED;}
            case AQUA -> {return NamedTextColor.AQUA;}
            case GREEN -> {return NamedTextColor.GREEN;}
            case YELLOW -> {return NamedTextColor.YELLOW;}
            default -> {return NamedTextColor.WHITE;}
        }
    }

    public void setup() {
        Icons icons = icons();
        ItemStack base = new ItemStack(icons.getBaseMaterial());

        ItemStack gray = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        MakeItem.setItemMeta(gray, "", null, 0, null, null);
        for (int i = 0; i < 36; i++) {
            bottomIcons.put(i, new BottomIcon(gray.clone(), event -> {}));
        }

        MakeItem.setItemMeta(base, "上へスクロール", null, icons.getTriangleUp().getCmd(), ItemData.BUTTON_ID, "UP NOTE");
        bottomIcons.put(9, new BottomIcon(base.clone(), event -> {
            inputGuiUpdate(-1);
        }));
        MakeItem.setItemMeta(base, "下へスクロール", null, icons.getTriangleDown().getCmd(), ItemData.BUTTON_ID, "DOWN NOTE");
        bottomIcons.put(18, new BottomIcon(base.clone(), event -> {
            inputGuiUpdate(1);
        }));

        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(bar, "しゅうりょう", NamedTextColor.DARK_RED, 0, ItemData.BUTTON_ID, "CLOSE");
        bottomIcons.put(35, new BottomIcon(bar.clone(), event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
        }));

        String[] whiteName = {"休符", "null", "ド/C", "レ/D", "ミ/E", "ファ/F", "ソ/G", "ラ/A", "シ/B"};
        for (int i = 0; i < whiteName.length; i++) {
            if (!whiteName[i].equals("null")) {
                ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                MakeItem.setItemMeta(white, whiteName[i], null, 0, ItemData.BUTTON_ID, whiteName[i]);
                final NoteSpec noteSpec = NOTE_SPECS.get(whiteName[i]);
                bottomIcons.put(i + 17, new BottomIcon(white.clone(), event -> {
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
                bottomIcons.put(i + 10, new BottomIcon(black.clone(), event -> {
                    applyNoteSpec(noteSpec);
                    moveNextSelectedSlot();
                }));
            }
        }

        ItemStack structure = new ItemStack(Material.STRUCTURE_VOID);
        MakeItem.setItemMeta(structure, "全削除", null, 0, ItemData.BUTTON_ID, "ALL DELETE");
        bottomIcons.put(33, new BottomIcon(structure.clone(), event -> {
            int[] reset = new int[Man10Music.MUSIC_LENGTH];
            Arrays.fill(reset, 0);
            musicList = reset;

            inputGuiUpdate(0);
        }));

        ItemStack insertRest = new ItemStack(Material.WRITABLE_BOOK);
        MakeItem.setItemMetaByColor(insertRest, "休符の挿入", NamedTextColor.YELLOW, 0, ItemData.BUTTON_ID, "INSERT REST");
        bottomIcons.put(29, new BottomIcon(insertRest.clone(), event -> {
            int index = getMusicIndex();
            insertRestKeepingLength(index);
            inputGuiUpdate(0);
        }));


        ItemStack cutNote = new ItemStack(Material.SHEARS);
        MakeItem.setItemMetaByColor(cutNote, "ノートの切り取り", NamedTextColor.RED, 0, ItemData.BUTTON_ID, "CUT NOTE");
        bottomIcons.put(31, new BottomIcon(cutNote.clone(), event -> {
            int index = getMusicIndex();
            cutNoteKeepingLength(index);
            inputGuiUpdate(0);
        }));
        setPlayIcon();
        setTrackIcon();
        jumpPage(1);
    }

    private void setTrackIcon() {
        ItemStack trackItem = new ItemStack(iconsData().getBaseMaterial());
        Track prevTrack = getPrevTrack(this.track);
        Track nextTrack = getNextTrack(this.track);

        trackItem.editMeta(meta -> {
            meta.customName(Component.text("現在のトラック: " + getTrackLabel(this.track))
                    .color(getTitleColor(this.track)));
            Component leftLore = Component.text("左クリック").color(NamedTextColor.GRAY)
                    .append(Component.text(getTrackLabel(prevTrack) + "トラックへ")
                            .color(getTitleColor(prevTrack)));
            Component rightLore = Component.text("右クリック").color(NamedTextColor.GRAY)
                    .append(Component.text(getTrackLabel(nextTrack) + "トラックへ")
                            .color(getTitleColor(nextTrack)));

            meta.setItemModel(getTrackModelKey(this.track));
            meta.lore(List.of(leftLore, rightLore));
        });
        bottomIcons.put(28, new BottomIcon(trackItem.clone(), event -> {
            if (event.isLeftClick()){
                changeTrack((Player) event.getWhoClicked(), prevTrack);
            } else if (event.isRightClick()) {
                changeTrack((Player) event.getWhoClicked(), nextTrack);
            }
        }));
    }

    private void setPlayIcon() {
        ItemStack playItem = new ItemStack(iconsData().getBaseMaterial());
        playItem.editMeta(meta -> {
            meta.customName(Component.text("再生").color(NamedTextColor.GREEN));
            meta.lore(List.of(
                    Component.text("左クリック: 現在位置から再生").color(NamedTextColor.GRAY),
                    Component.text("右クリック: 最初から再生").color(NamedTextColor.GRAY),
                    Component.text("シフト + 左クリック: 現在トラックの現在位置から再生").color(NamedTextColor.GRAY),
                    Component.text("シフト + 右クリック: 現在トラックの最初から再生").color(NamedTextColor.GRAY)
            ));
            meta.setItemModel(NamespacedKey.minecraft("music_disc_13"));
            meta.setMaxStackSize(99);
        });
        bottomIcons.put(0, new BottomIcon(playItem.clone(), event -> {
            Player player = (Player) event.getWhoClicked();
            switch (event.getClick()) {
                case LEFT -> startPreviewPlay(player, getMusicIndex(), false);
                case RIGHT -> startPreviewPlay(player, 0, false);
                case SHIFT_LEFT -> startPreviewPlay(player, getMusicIndex(), true);
                case SHIFT_RIGHT -> startPreviewPlay(player, 0, true);
                default -> {
                }
            }
            setStopIcon(page, topNote);
        }));
    }

    private void setStopIcon(int page, int topNote) {
        ItemStack stopItem = new ItemStack(iconsData().getBaseMaterial());
        stopItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("elytra"));
            meta.customName(Component.text("再生停止").color(NamedTextColor.RED));
        });
        ItemData.BUTTON_ID.set(stopItem, "STOP");
        bottomIcons.put(0, new BottomIcon(stopItem.clone(), event -> {
            Player player = (Player) event.getWhoClicked();
            stopMusic(player);
            onStopMusic(player, page, topNote);
        }));
    }

    private void stopMusic(Player player) {
        isPlaying = false;
        PlayMusic playMusic = PlayMusicManager.getMusic(player);
        AutPlayManager.set(player, false);
        if (playMusic != null) {
            playMusic.stopTask(player);
        }
    }

    public void open(Player player) {
        stopMusic(player);
        inputGuiUpdate(0);
        player.openInventory(this.inv);
        updateFakeItemInPlayerInventorySlot(player);
    }

    public void inputGuiUpdate(int offset) {
        if (musicList == null) {
            return;
        }
        topNote += offset;

        ItemStack icon = new ItemStack(icons().getBaseMaterial());
        for (int i = 0; i <= 53; i++) {
            MakeItem.setItemMeta(icon, "", null, icons().getNoteBlank().getCmd(), null, null);
            this.inv.setItem(i, icon);
        }
        int noteId;
        for (int i = 0; i <= 5; i++) {
            noteId = topNote + i;
            icon.setAmount(7 - (noteId + 2) / 6);
            while (noteId >= 7) noteId -= 6;
            MakeItem.setItemMeta(icon, "", null, scaleCmd(icons(), noteId), ItemData.TOP_NOTE, topNote);
            this.inv.setItem(i * 9, icon);
        }
        icon.setAmount(1);
        for (int i = 0; i < 8; i++) {
            int note = musicList[i + ((page - 1) * 8)];
            if (note != 0) {
                if (note % 2 == 1) {
                    MakeItem.setItemMeta(icon, "", null, icons().getNoteUp(track).getCmd(), null, null);
                } else {
                    MakeItem.setItemMeta(icon, "", null, icons().getNoteDown(track).getCmd(), null, null);
                }
                if (note > topNote * 2 && note <= (topNote + 6) * 2) {
                    note -= topNote * 2;
                    if (note == 25) note -= 24;
                    else if (note >= 13) note -= 12;
                    if (note % 2 == 1) {
                        note += 1;
                    }
                    this.inv.setItem(i + 1 + (9 * note / 2 - 9), icon);
                }
            }
        }
    }

    public void jumpPage(int page) {
        if (page < 1 || page > Man10Music.MAX_PAGE) return;
        this.page = page;
        ItemStack sign = getSign(page);
        bottomIcons.put(27, new BottomIcon(sign.clone(), event -> {
            switch (event.getClick()) {
                case RIGHT -> jumpNextPage();
                case LEFT -> jumpBackPage();
                case SHIFT_RIGHT -> {
                    setMusicEndpoint();
                    int endPage = getMusicFinalPage();
                    jumpPage(endPage);
                }
                case SHIFT_LEFT -> jumpPage(1);
            }
        }));
        inputGuiUpdate(0);
        setSelectedSlot(0);
    }

    private static @NonNull ItemStack getSign(int page) {
        ItemStack sign = new ItemStack(Material.OAK_HANGING_SIGN);
        sign.editMeta(meta -> {
            meta.customName(Component.text("現在 " + page +" ページ目"));
            meta.lore(List.of(
                    Component.text("右クリック            : ").color(NamedTextColor.YELLOW).append(Component.text("次のページへ").color(NamedTextColor.WHITE)),
                    Component.text("左クリック            : ").color(NamedTextColor.YELLOW).append(Component.text("前のページへ").color(NamedTextColor.WHITE)),
                    Component.text("シフト + 右クリック : ").color(NamedTextColor.YELLOW).append(Component.text("最後のページへ").color(NamedTextColor.WHITE)),
                    Component.text("シフト + 左クリック : ").color(NamedTextColor.YELLOW).append(Component.text("最初のページへ").color(NamedTextColor.WHITE))
            ));
            meta.setMaxStackSize(99);
        });
        return sign;
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
        int rawSlot = event.getRawSlot();

        int slot = rawSlot - 54;
        if (slot < 0) slot += 45;

        if (0 > slot) onClickInTopInventory(event);
        else onClickInBottomInventory(event, slot);
        updateFakeItemInPlayerInventorySlot((Player) event.getWhoClicked());
    }

    private void onClickInBottomInventory(InventoryClickEvent event, int slot) {
        BottomIcon bottomIcon = bottomIcons.get(slot);
        if (bottomIcon != null) {
            if (isPlaying) {
                if (!("STOP").equals(ItemData.BUTTON_ID.get(bottomIcon.getIcon()))){
                    Player player = (Player) event.getWhoClicked();
                    stopMusic(player);
                    onStopMusic(player, page, topNote);
                }
            }
            bottomIcon.runClickAction(event);
        }
    }

    private void onClickInTopInventory(InventoryClickEvent event) {
    }

    @Override
    public void onClose(Player player) {
        stopMusic(player);
        if (!closeFlag) return;
        closeFlag = false;
        setMusicEndpoint();
        music.setMusic(track, musicList);
        MusicManager.saveMusicToDb(Man10Music.getInstance().getMySQLManager(), music);
        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
            MainMenuHolder mainMenuHolder = new MainMenuHolder();
            player.openInventory(mainMenuHolder.getInventory());
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), player::updateInventory);
        });
    }

    @Override
    public void onPlayNote(int count, Player player) {
        page = count / 8 + 1;
        if (page < 1) page = 1;
        selectedSlot = count % 8;
        // 再生中の音符が見えるようにページとスロットを移動する
        jumpPage(page);
        updateTopNoteForPlayback(count);
        inputGuiUpdate(0);
        updateFakeItemInPlayerInventorySlot(player);
    }

    private void updateTopNoteForPlayback(int index) {
        if (musicList == null || index < 0 || index >= musicList.length) {
            return;
        }
        int note = musicList[index];
        if (note <= 0) {
            return;
        }
        int minTop = (int) Math.ceil(note / 2.0) - 6;
        if (minTop < 0) {
            minTop = 0;
        }
        if (note <= topNote * 2 || note > (topNote + 6) * 2) {
            topNote = minTop;
        }
    }

    public void onStopMusic(Player player, int page, int topNote) {
        jumpPage(page);
        this.topNote = topNote;
        inputGuiUpdate(0);
        setPlayIcon();
        updateFakeItemInPlayerInventorySlot(player);
    }

    private void changeTrack(Player player, Track track) {
        setMusicEndpoint();
        music.setMusic(this.track, musicList);
        MusicManager.saveMusicToDb(Man10Music.getInstance().getMySQLManager(), music);
        musicList = music.getMusic(track);
        this.track = track;
        setTrackIcon();
        jumpPage(1);
        updateFakeItemInPlayerInventorySlot(player);
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
                bottomIcons.put(i + 1, new BottomIcon(selected, event -> {
                }));

            } else {
                int finalI = i;
                bottomIcons.put(i + 1, new BottomIcon(select, event -> {
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

    private void startPreviewPlay(Player player, int startIndex, boolean currentTrackOnly) {
        if (player == null || music == null) {
            return;
        }
        music.setMusic(track, musicList);
        setMusicEndpoint();
        EnumSet<Track> trackSet = currentTrackOnly ? EnumSet.of(track) : EnumSet.allOf(Track.class);
        PlayMusic playing = PlayMusicManager.getMusic(player);
        if (playing != null) {
            playing.stopTask(player);
        }
        PlayMusic play = new PlayMusic();
        play.setPrivate(true);
        play.setRequester(player);
        PlayMusicManager.setPlayingMusic(player, play);
        AutPlayManager.set(player, false);
        play.playMusic(player, music, startIndex, trackSet, musicData().getDefaultVolume(), musicData().getSoundRange());
        isPlaying = true;
    }

    private Track getPrevTrack(Track current) {
        if (current == null) return Track.RED;
        int index = TRACK_ORDER.indexOf(current);
        int prevIndex = (index - 1);
        if (prevIndex < 0 ) return TRACK_ORDER.getLast();
        return TRACK_ORDER.get(prevIndex);
    }

    private Track getNextTrack(Track current) {
        if (current == null) return Track.RED;
        int index = TRACK_ORDER.indexOf(current);
        int nextIndex = (index + 1);
        if (nextIndex >= TRACK_ORDER.size()) return TRACK_ORDER.getFirst();
        return TRACK_ORDER.get(nextIndex);
    }

    private String getTrackLabel(Track track) {
        if (track == null) return "Unknown";
        return switch (track) {
            case RED -> "Red";
            case AQUA -> "Aqua";
            case GREEN -> "Green";
            case YELLOW -> "Yellow";
            default -> "Unknown";
        };
    }

    private NamespacedKey getTrackModelKey(Track track) {
        if (track == null) return NamespacedKey.minecraft("barrier");
        return switch (track) {
            case RED -> NamespacedKey.minecraft("redstone_block");
            case AQUA -> NamespacedKey.minecraft("diamond_block");
            case GREEN -> NamespacedKey.minecraft("emerald_block");
            case YELLOW -> NamespacedKey.minecraft("gold_block");
            default -> NamespacedKey.minecraft("barrier");
        };
    }
}

class BottomIcon {
    private final ItemStack icon;
    private final Consumer<InventoryClickEvent> clickAction;
    BottomIcon(ItemStack icon, Consumer<InventoryClickEvent> clickAction) {
        this.icon = icon;
        this.clickAction = clickAction;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public Consumer<InventoryClickEvent> getClickAction() {
        return clickAction;
    }

    public void runClickAction(InventoryClickEvent event) {
        if (clickAction == null) return;
        clickAction.accept(event);
    }
}