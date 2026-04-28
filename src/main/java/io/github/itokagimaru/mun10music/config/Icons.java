package io.github.itokagimaru.mun10music.config;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Locale;
public final class Icons {
    public static final class Entry {
        private final Material material;
        private final int cmd;
        public Entry(Material material, int cmd) {
            this.material = material;
            this.cmd = cmd;
        }
        public Material getMaterial() {
            return material;
        }
        public int getCmd() {
            return cmd;
        }
    }
    private final Material baseMaterial;
    private final Entry triangleUp;
    private final Entry triangleDown;
    private final Entry triangleLeft;
    private final Entry triangleRight;
    private final Entry triangleSelect;
    private final Entry scaleNullToFSharp;
    private final Entry scaleFToE;
    private final Entry scaleDSharpToD;
    private final Entry scaleCSharpToC;
    private final Entry scaleBToASharp;
    private final Entry scaleAToGSharp;
    private final Entry scaleGToFSharp;
    private final Entry noteBlank;
    private final Entry noteUp;
    private final Entry noteDown;
    public Icons(FileConfiguration config) {
        // 生成時にmaterialを解決して固定
        this.baseMaterial = toMaterial(config.getString("icons.material"), Material.PAPER);
        this.triangleUp = readEntry(config, "icons.triangle.up", 8);
        this.triangleDown = readEntry(config, "icons.triangle.down", 9);
        this.triangleLeft = readEntry(config, "icons.triangle.left", 10);
        this.triangleRight = readEntry(config, "icons.triangle.right", 11);
        this.triangleSelect = readEntry(config, "icons.triangle.select", 12);
        this.scaleNullToFSharp = readEntry(config, "icons.sheetMusic.scale.null-F#", 13);
        this.scaleFToE = readEntry(config, "icons.sheetMusic.scale.F-E", 14);
        this.scaleDSharpToD = readEntry(config, "icons.sheetMusic.scale.D#-D", 15);
        this.scaleCSharpToC = readEntry(config, "icons.sheetMusic.scale.C#-C", 16);
        this.scaleBToASharp = readEntry(config, "icons.sheetMusic.scale.B-A#", 17);
        this.scaleAToGSharp = readEntry(config, "icons.sheetMusic.scale.A-G#", 18);
        this.scaleGToFSharp = readEntry(config, "icons.sheetMusic.scale.G-F#", 19);
        this.noteBlank = readEntry(config, "icons.sheetMusic.note.blank", 20);
        this.noteUp = readEntry(config, "icons.sheetMusic.note.up", 21);
        this.noteDown = readEntry(config, "icons.sheetMusic.note.down", 22);
    }
    private Entry readEntry(FileConfiguration config, String path, int defaultCmd) {
        int cmd = config.getInt(path, defaultCmd);
        return new Entry(baseMaterial, cmd);
    }
    private Material toMaterial(String materialName, Material fallback) {
        if (materialName == null) {
            return fallback;
        }
        Material material = Material.matchMaterial(materialName.toUpperCase(Locale.ROOT));
        return material != null ? material : fallback;
    }
    public Material getBaseMaterial() {
        return baseMaterial;
    }
    public Entry getTriangleUp() {
        return triangleUp;
    }
    public Entry getTriangleDown() {
        return triangleDown;
    }
    public Entry getTriangleLeft() {
        return triangleLeft;
    }
    public Entry getTriangleRight() {
        return triangleRight;
    }
    public Entry getTriangleSelect() {
        return triangleSelect;
    }
    public Entry getScaleNullToFSharp() {
        return scaleNullToFSharp;
    }
    public Entry getScaleFToE() {
        return scaleFToE;
    }
    public Entry getScaleDSharpToD() {
        return scaleDSharpToD;
    }
    public Entry getScaleCSharpToC() {
        return scaleCSharpToC;
    }
    public Entry getScaleBToASharp() {
        return scaleBToASharp;
    }
    public Entry getScaleAToGSharp() {
        return scaleAToGSharp;
    }
    public Entry getScaleGToFSharp() {
        return scaleGToFSharp;
    }
    public Entry getNoteBlank() {
        return noteBlank;
    }
    public Entry getNoteUp() {
        return noteUp;
    }
    public Entry getNoteDown() {
        return noteDown;
    }
}
