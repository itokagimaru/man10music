package io.github.itokagimaru.mun10music.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public final class Items {
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

    private final Entry keyBoard;
    private final Entry walkMan;
    private final Entry cassette;
    private final Entry sheetMusicBlank;
    private final Entry sheetMusicWritten;
    private final Entry cassetteWorkspaceItem;
    private final Entry cassetteWorkspaceBlock;
    private final Entry radioItem;
    private final Entry radioBlock;

    public Items(FileConfiguration config) {
        // 生成時に文字列をMaterialへ変換して保持
        this.keyBoard = readEntry(config, "items.keyBoard", Material.WOODEN_HOE, 1);
        this.walkMan = readEntry(config, "items.walkMan", Material.WOODEN_HOE, 2);
        this.cassette = readEntry(config, "items.cassette", Material.PAPER, 1);
        this.sheetMusicBlank = readEntry(config, "items.sheetMusic.blank", Material.PAPER, 2);
        this.sheetMusicWritten = readEntry(config, "items.sheetMusic.written", Material.PAPER, 3);
        this.cassetteWorkspaceItem = readEntry(config, "items.cassetteWorkspace.item", Material.PAPER, 4);
        this.cassetteWorkspaceBlock = readEntry(config, "items.cassetteWorkspace.block", Material.PAPER, 5);
        this.radioItem = readEntry(config, "items.radio.item", Material.PAPER, 6);
        this.radioBlock = readEntry(config, "items.radio.block", Material.PAPER, 7);
    }

    private Entry readEntry(FileConfiguration config, String path, Material defaultMaterial, int defaultCmd) {
        String materialName = config.getString(path + ".material", defaultMaterial.name());
        int cmd = config.getInt(path + ".cmd", defaultCmd);
        Material material = toMaterial(materialName, defaultMaterial);
        return new Entry(material, cmd);
    }

    private Material toMaterial(String materialName, Material fallback) {
        if (materialName == null) {
            return fallback;
        }
        Material material = Material.matchMaterial(materialName.toUpperCase(Locale.ROOT));
        return material != null ? material : fallback;
    }

    public Entry getKeyBoard() {
        return keyBoard;
    }

    public Entry getWalkMan() {
        return walkMan;
    }

    public Entry getCassette() {
        return cassette;
    }

    public Entry getSheetMusicBlank() {
        return sheetMusicBlank;
    }

    public Entry getSheetMusicWritten() {
        return sheetMusicWritten;
    }

    public Entry getCassetteWorkspaceItem() {
        return cassetteWorkspaceItem;
    }

    public Entry getCassetteWorkspaceBlock() {
        return cassetteWorkspaceBlock;
    }

    public Entry getRadioItem() {
        return radioItem;
    }

    public Entry getRadioBlock() {
        return radioBlock;
    }
}

