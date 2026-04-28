package io.github.itokagimaru.mun10music.data;

import io.github.itokagimaru.mun10music.Man10Music;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Supplier;

public class ItemData {
    private static final String NAMESPACE = "man10music";
    private static final String LEGACY_NAMESPACE = "itokagimaru_daw";

    /**
     * Get key instance for pdc container id
     *
     * @param key key
     * @return New instance of NamespacedKey
     */
    private static NamespacedKey getKey(String key) {
        return new NamespacedKey(NAMESPACE, key);
    }

    private static NamespacedKey getLegacyKey(String key) {
        return new NamespacedKey(LEGACY_NAMESPACE, key);
    }

    private static <C> C getWithLegacy(PersistentDataContainerView dataContainerView,
                                       NamespacedKey key,
                                       NamespacedKey legacyKey,
                                       PersistentDataType<?, C> dataType,
                                       Supplier<C> defaultValue) {
        if (dataContainerView.has(key, dataType)) {
            return dataContainerView.getOrDefault(key, dataType, defaultValue.get());
        }
        if (dataContainerView.has(legacyKey, dataType)) {
            return dataContainerView.getOrDefault(legacyKey, dataType, defaultValue.get());
        }
        return defaultValue.get();
    }

    private static Integer readInt(PersistentDataContainerView dataContainerView, NamespacedKey key) {
        if (dataContainerView.has(key, PersistentDataType.STRING)) {
            try {
                String rawInt = dataContainerView.get(key, PersistentDataType.STRING);
                if (rawInt == null) return null;
                return Integer.parseInt(rawInt);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (dataContainerView.has(key, PersistentDataType.INTEGER)) {
            return dataContainerView.get(key, PersistentDataType.INTEGER);
        }
        return null;
    }

    private static Integer getIntWithLegacy(PersistentDataContainerView dataContainerView,
                                            NamespacedKey key,
                                            NamespacedKey legacyKey,
                                            Supplier<Integer> defaultValue) {
        Integer value = readInt(dataContainerView, key);
        if (value != null) {
            return value;
        }
        value = readInt(dataContainerView, legacyKey);
        return value != null ? value : defaultValue.get();
    }

    private static final class LegacyIntKey extends IntKey {
        private final NamespacedKey legacyKey;

        private LegacyIntKey(NamespacedKey key, NamespacedKey legacyKey, Supplier<Integer> defaultValue) {
            super(key, defaultValue);
            this.legacyKey = legacyKey;
        }

        @Override
        public Integer get(PersistentDataContainerView dataContainerView) {
            return getIntWithLegacy(dataContainerView, key, legacyKey, defaultValue);
        }
    }

    private static final class LegacyByteArrayKey extends ByteArrayKey {
        private final NamespacedKey legacyKey;

        private LegacyByteArrayKey(NamespacedKey key, NamespacedKey legacyKey, Supplier<byte[]> defaultValue) {
            super(key, defaultValue);
            this.legacyKey = legacyKey;
        }

        @Override
        public byte[] get(PersistentDataContainerView dataContainerView) {
            return getWithLegacy(dataContainerView, key, legacyKey, dataType, defaultValue);
        }
    }

    private static final class LegacyByteKey extends ByteKey {
        private final NamespacedKey legacyKey;

        private LegacyByteKey(NamespacedKey key, NamespacedKey legacyKey, Supplier<Byte> defaultValue) {
            super(key, defaultValue);
            this.legacyKey = legacyKey;
        }

        @Override
        public Byte get(PersistentDataContainerView dataContainerView) {
            return getWithLegacy(dataContainerView, key, legacyKey, dataType, defaultValue);
        }
    }

    private static final class LegacyStringKey extends StringKey {
        private final NamespacedKey legacyKey;

        private LegacyStringKey(NamespacedKey key, NamespacedKey legacyKey, Supplier<String> defaultValue) {
            super(key, defaultValue);
            this.legacyKey = legacyKey;
        }

        @Override
        public String get(PersistentDataContainerView dataContainerView) {
            return getWithLegacy(dataContainerView, key, legacyKey, dataType, defaultValue);
        }
    }

    private static final class LegacyIntArrayKey extends IntArrayKey {
        private final NamespacedKey legacyKey;

        private LegacyIntArrayKey(NamespacedKey key, NamespacedKey legacyKey, Supplier<int[]> defaultValue) {
            super(key, defaultValue);
            this.legacyKey = legacyKey;
        }

        @Override
        public int[] get(PersistentDataContainerView dataContainerView) {
            return getWithLegacy(dataContainerView, key, legacyKey, dataType, defaultValue);
        }
    }

    public static final IntKey BPM = new LegacyIntKey(getKey("bpm"), getLegacyKey("bpm"), () -> -1);
    public static final IntKey TOP_NOTE = new LegacyIntKey(getKey("topnote"), getLegacyKey("topnote"), () -> 0);
    public static final IntKey PAGE = new LegacyIntKey(getKey("page"), getLegacyKey("page"), () -> 0);
    public static final ByteArrayKey BYTE_LIST = new LegacyByteArrayKey(getKey("bytelist"), getLegacyKey("bytelist"), () -> new byte[]{});
    public static final ByteKey IS_NAMED = new LegacyByteKey(getKey("isnamed"), getLegacyKey("isnamed"), () -> (byte) 0);
    public static final ByteKey IS_MERGED = new LegacyByteKey(getKey("ismerged"), getLegacyKey("ismerged"), () -> (byte) 0);
    public static final StringKey BUTTON_ID = new LegacyStringKey(getKey("buttonid"), getLegacyKey("buttonid"), () -> "");
    public static final StringKey ITEM_ID = new LegacyStringKey(getKey("itemid"), getLegacyKey("itemid"), () -> "");
    public static final StringKey RECORDER = new LegacyStringKey(getKey("recorder"), getLegacyKey("recorder"), () -> "");
    public static final StringKey MUSIC_NAME = new LegacyStringKey(getKey("musicname"), getLegacyKey("musicname"), () -> "");
    public static final StringKey UUID = new LegacyStringKey(getKey("uuid"), getLegacyKey("uuid"), () -> "");
    // 将来のために変数だけ用意
//    public static final IntArrayKey MUSIC_SAVED_BLUE = new IntArrayKey(getKey("music_saved_blue"), () -> new int[0]);
    public static final IntArrayKey MUSIC_SAVED_RED = new LegacyIntArrayKey(getKey("music_saved_blue"), getLegacyKey("music_saved_blue"), () -> new int[Man10Music.MUSIC_LENGTH]);
//    public static final IntArrayKey MUSIC_SAVED_YELLOW = new IntArrayKey(getKey("music_saved_blue"), () -> new int[0]);
}
