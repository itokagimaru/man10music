package io.github.itokagimaru.mun10music.manager.music;

public enum Track {
    RED("red"),
    AQUA("aqua"),
    GREEN("green"),
    YELLOW("yellow"),
    UNKNOWN("unknown");
    final String id;
    Track(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public static Track getByID(String id) {
        for (Track track : Track.values()) {
            if (track.getId().equals(id)) {
                return track;
            }
        }
        return UNKNOWN;
    }
}
