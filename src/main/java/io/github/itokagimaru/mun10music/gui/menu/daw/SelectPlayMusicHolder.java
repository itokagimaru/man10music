package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.gui.menu.base.BaseMusicListHolder;
import io.github.itokagimaru.mun10music.manager.music.Music;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.List;

public class SelectPlayMusicHolder extends BaseMusicListHolder {
    private static final List<Component> ICONS_LORE = List.of(
            Component.text(
                    "左クリック: 再生"
            ).decoration(TextDecoration.BOLD, true).color(NamedTextColor.YELLOW)
    );
    public SelectPlayMusicHolder(Player openPlayer) {
        super(openPlayer, "再生する曲を選択", ICONS_LORE);
    }

    @Override
    protected void onMusicLeftClick(Player player, int musicID) {
        Music music = getMusic(musicID);
        DawsPlayModeHolder dawsPlayModeHolder = new DawsPlayModeHolder(music);
        player.openInventory(dawsPlayModeHolder.getInventory());
    }

    @Override
    protected void onMusicRightClick(Player player, int musicID) {}//右クリックは特に何もしない 気が向けば編集画面に遷移させてもいいかも

    @Override
    public void onClose(Player player) {

    }
}
