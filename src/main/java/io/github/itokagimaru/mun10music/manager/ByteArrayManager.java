package io.github.itokagimaru.mun10music.manager;

import java.nio.ByteBuffer;

public class ByteArrayManager {
    public static byte[] encode(int[] list) {
        ByteBuffer buffer = ByteBuffer.allocate(list.length * 4);
        for (int i : list) buffer.putInt(i);
        return buffer.array();
    }

    public static int[] decode(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int[] list = new int[data.length / 4];
        for (int i = 0; i < list.length; i++) {
            list[i] = buffer.getInt();
        }
        return list;
    }
}
