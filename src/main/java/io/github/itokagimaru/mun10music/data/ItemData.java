package io.github.itokagimaru.mun10music.data;

import io.github.itokagimaru.mun10music.Man10Music;
import org.bukkit.NamespacedKey;

public class ItemData {
    private static final String NAMESPACE = "man10music";

    /**
     * Get key instance for pdc container id
     *
     * @param key key
     * @return New instance of NamespacedKey
     */
    private static NamespacedKey getKey(String key) {
        return new NamespacedKey(NAMESPACE, key);
    }

    public static final IntKey BPM = new IntKey(getKey("bpm"), () -> -1);
    public static final IntKey TOP_NOTE = new IntKey(getKey("topnote"), () -> 0);
    public static final IntKey PAGE = new IntKey(getKey("page"), () -> 0);
    public static final ByteArrayKey BYTE_LIST = new ByteArrayKey(getKey("bytelist"), () -> new byte[]{});
    public static final ByteKey IS_NAMED = new ByteKey(getKey("isnamed"), () -> (byte) 0);
    public static final ByteKey IS_MERGED = new ByteKey(getKey("ismerged"), () -> (byte) 0);
    public static final StringKey BUTTON_ID = new StringKey(getKey("buttonid"), () -> "");
    public static final StringKey ITEM_ID = new StringKey(getKey("itemid"), () -> "");
    public static final StringKey RECORDER = new StringKey(getKey("recorder"), () -> "");
    public static final StringKey MUSIC_NAME = new StringKey(getKey("musicname"), () -> "");
    public static final StringKey UUID = new StringKey(getKey("uuid"), () -> "");
    public static final IntArrayKey PUBLISHED_MUSIC_IDS = new IntArrayKey(getKey("music_id"), () -> new int[5]);
    // 将来のために変数だけ用意
//    public static final IntArrayKey MUSIC_SAVED_BLUE = new IntArrayKey(getKey("music_saved_blue"), () -> new int[0]);
    public static final IntArrayKey MUSIC_SAVED_RED = new IntArrayKey(getKey("music_saved_blue"), () -> new int[Man10Music.MUSIC_LENGTH]);
//    public static final IntArrayKey MUSIC_SAVED_YELLOW = new IntArrayKey(getKey("music_saved_blue"), () -> new int[0]);
}
