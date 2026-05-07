package io.github.itokagimaru.mun10music.manager.music;

import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MusicDataCodec {

    public static int[] toIntArray(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return new int[0];
        if (bytes.length % 4 != 0) return new int[0];

        // 4バイト単位でint配列に復元
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        int[] result = new int[bytes.length / 4];
        for (int i = 0; i < result.length; i++) {
            result[i] = buffer.getInt();
        }
        return result;
    }

    public static byte[] toByteArray(int[] music) {
        if (music == null || music.length == 0) return new byte[0];

        // int配列を4バイト単位でBLOBに変換
        ByteBuffer buffer = ByteBuffer.allocate(music.length * 4).order(ByteOrder.BIG_ENDIAN);
        for (int value : music) {
            buffer.putInt(value);
        }
        return buffer.array();
    }

    public static List<UUID> fromYamlRelates(String yamlString) {
        if (yamlString == null || yamlString.isBlank()) return Collections.emptyList();

        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.loadFromString(yamlString);
        } catch (Exception e) {
            return Collections.emptyList();
        }

        List<String> uuidStrings = yaml.getStringList("relates");
        if (uuidStrings == null || uuidStrings.isEmpty()) return Collections.emptyList();

        List<UUID> relates = new ArrayList<>();
        for (String value : uuidStrings) {
            try {
                relates.add(UUID.fromString(value));
            } catch (IllegalArgumentException ignored) {
                // 不正UUIDは無視
            }
        }
        return relates;
    }

    public static String toYamlRelates(List<UUID> relates) {
        YamlConfiguration yaml = new YamlConfiguration();
        List<String> uuidList = new ArrayList<>();
        if (relates != null) {
            for (UUID uuid : relates) {
                uuidList.add(uuid.toString());
            }
        }
        yaml.set("relates", uuidList);
        return yaml.saveToString();
    }
}
