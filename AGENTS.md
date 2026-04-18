# Itokagimaru DAW Agent Guidelines

## 🇯🇵 プロジェクト固有ルール（必読）

### 1. 実装言語: 日本語コメント必須
このプロジェクトは日本人コミュニティで運用・保守されます。
- ✅ **すべてのコメント・ログメッセージは日本語で作成すること**
- ✅ **変数名はキャメルケース（camelCase）で英語**
- ✅ **JavaDocのdescription部分も日本語でOK**

```java
// ❌ 悪い例
int count = 0;  // number of notes

// ✅ 良い例
int count = 0;  // ノート数をカウント
```

### 2. コード出力フロー: 必ず3段階
コードを生成する際は、以下のフロー厳守。即座にファイルに適用してはいけません：

1. **チャットに出力**: 実装コードを提示
2. **3点を説明**: 変更箇所・意図・仕組み
3. **ユーザー同意待機**: 確認後にのみファイル適用

```
[コード表示] 
  → "以下の3点で説明します..." 
  → [確認してください]
  → ユーザー: "了解" or "修正して"
  → [ファイル適用]
```

### 3. 説明の必須3項目
**変更した場所**（ファイルパス・メソッド名・行番号）
```
ファイル: src/main/java/.../InputModeHolder.java
メソッド: onClick()  
行: 150-170
```

**変更の意図**（なぜ必要か・解決する問題）
```
理由: 従来のString定数をEnumに統一して型安全化
問題: switch文でのタイプミスやNullPointerException防止
```

**実装の仕組み**（動作・パターン）
```
仕組み: PDC → String → Enum.fromId() → switch
パターン: Visitor + State パターンを応用
```

---

## 🏗️ Architecture Overview

### システム全体像
このMinecraftプラグインは、ItemStack内のPersistent Data Container (PDC) を活用した**完全DAWシステム**：

```
ユーザーUI層
  ↓ [GUIHolderベース]
ビジネスロジック層 
  ↓ [Manager群 + Task]
データ永続化層
  ├─ ItemStack PDC（楽曲 + メタデータ）
  └─ MySQL（プレイヤーインベ）
```

### コアコンポーネント
- **Data Storage**: 
  - ItemStack PersistentDataContainer (PDC) → 楽曲データ（int[16384]）＋ BPM＋各種フラグ
  - MySQL → プレイヤーインベントリ永続化（Base64 YAML）

- **GUI System**: 
  - `BaseGuiHolder` 抽象基底クラス
  - 各GUIが `onClick(InventoryClickEvent)` ＆ `onClose(Player)` 実装
  - ClickInventoryListener → onClick() ポリモーフィズム呼び出し

- **Music Engine**: 
  - `PlayMusic` BukkitRunnable で楽曲再生
  - int[16384] の各要素 = ノート値（0=休符, -1=終端, 2-74=音符）
  - BPM計算: `再生間隔(tick) = 1200 / BPM`

- **Manager層**: 
  - `MusicManager`: PDC↔楽曲配列 変換
  - `InventoryManager`: ItemStack[]↔Base64 YAML 変換（非同期MySQL I/O）
  - `PlayMusicManager`: 再生中楽曲状態 HashMap管理
  - `ParticleManager`: パーティクル出力
  - `SheetMusicManager`: 楽譜機能

## 🎯 Key Patterns

### 1. PDC→String→Enum→Switch フロー（推奨パターン）
```java
// ❌ 従来の方法（推奨しない）
private static final String BTN_CLOSE = "CLOSE";
private static final String BTN_SELECT = "SELECT";

// ✅ 推奨: Enum + fromId()
private enum ButtonId {
    CLOSE("CLOSE"),
    SELECT("SELECT"),
    UP_NOTE("UP NOTE"),
    // ...
    UNKNOWN("");
    
    private final String id;
    
    ButtonId(String id) { this.id = id; }
    
    public static ButtonId fromId(String value) {
        if (value == null) return UNKNOWN;
        for (ButtonId btn : values()) {
            if (btn.id.equals(value.trim())) return btn;
        }
        return UNKNOWN;
    }
}

// 使用例
String buttonIdStr = ItemData.BUTTON_ID.get(clickedItem);  // PDCから取得
ButtonId buttonId = ButtonId.fromId(buttonIdStr);          // String→Enum
switch(buttonId) {
    case UP_NOTE -> { /* 処理 */ }
    case SELECT -> { /* 処理 */ }
}
```

**利点**: 型安全・タイプミス防止・IDE補完・わかりやすい

### 2. ItemData（PDCキー）の統一管理
```java
public class ItemData {
    private static final String NAMESPACE = "itokagimaru_daw";
    
    private static NamespacedKey getKey(String key) {
        return new NamespacedKey(NAMESPACE, key);
    }
    
    // 各種キー定義
    public static final IntKey BPM = new IntKey(getKey("bpm"), () -> -1);
    public static final IntArrayKey MUSIC_SAVED_RED = 
        new IntArrayKey(getKey("music_saved_red"), () -> new int[Itokagimaru_daw.MUSIC_LENGTH]);
    public static final StringKey BUTTON_ID = 
        new StringKey(getKey("buttonid"), () -> "");
}
```

**用途別キー**:
- **数値**: `IntKey`（BPM, TOP_NOTE, PAGE）
- **配列**: `IntArrayKey`（MUSIC_SAVED_RED[16384]）
- **文字列**: `StringKey`（BUTTON_ID, ITEM_ID, MUSIC_NAME）
- **バイト**: `ByteKey`（IS_NAMED, IS_MERGED）

### 3. GUI生成パターン（BaseGuiHolder継承）
```java
public class MyGuiHolder extends BaseGuiHolder {
    
    public MyGuiHolder() {
        this.inv = Bukkit.createInventory(this, 54, Component.text("MyGUI"));
        setup();  // UI構築
    }
    
    private void setup() {
        // ItemStack生成＋設定
        ItemStack item = new ItemStack(Material.PAPER);
        MakeItem.setItemMeta(item, displayName, lore, modelKey, 
                             ItemData.BUTTON_ID, "MY_BUTTON");
        this.inv.setItem(0, item);
    }
    
    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        ButtonId buttonId = ButtonId.fromId(ItemData.BUTTON_ID.get(clicked));
        
        switch(buttonId) {
            case MY_BUTTON -> { /* 処理 */ }
        }
    }
    
    @Override
    public void onClose(Player player) {
        if (!closeFlag) return;
        // 終了時の保存処理
    }
}
```

### 4. 非同期DB I/O（CompletableFuture）
```java
// ❌ 同期（メインスレッドがブロック）
ItemStack[] items = fromBase64(data);
player.getInventory().setContents(items);

// ✅ 非同期
CompletableFuture
    .supplyAsync(() -> fromBase64(base64))  // 別スレッド（重い処理）
    .thenAcceptAsync(items -> {
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.getInventory().setContents(items);  // メインスレッド（Bukkit API）
        });
    });
```

### 5. 再生フロー（BukkitRunnable ＋ BPM計算）
```java
public void playMusic(Entity target, ItemStack pdcHolder) {
    int[] loadedMusic = ItemData.MUSIC_SAVED_RED.get(pdcHolder);
    int bpm = ItemData.BPM.get(pdcHolder);
    long interval = 1200 / bpm;  // BPMから再生間隔を計算
    
    task = new BukkitRunnable() {
        int count = 0;
        @Override
        public void run() {
            if (loadedMusic[count] == -1) {
                stopTask(target);  // 終端に到達
            } else if (loadedMusic[count] != 0) {
                PlaySound.playNote(target, loadedMusic[count], volume, isPrivate);
                ParticleManager.playNote(player, isPrivate);
            }
            count++;
        }
    }.runTaskTimer(plugin, 0, interval);
}

## 🔧 Build & Test Workflow
- **Build Plugin**: `gradlew build` creates fat JAR in `build/libs/` (requires texture pack from https://github.com/itokagimaru/Itokagimaru_daw_tex)
- **Test Server**: `gradlew runServer` starts Paper 1.21.8 server for testing
- **Dependencies**: Java 21, Paper API 1.21.8, HikariCP 5.1.0

## ⚙️ Configuration
- **MySQL Settings**: Configure host/port/database/user/password in `config.yml`
- **Item Models**: Custom model data (CMD) values defined in `config.yml` under `items.*.cmd`
- **Icon CMDs**: Triangle/navigation icons use CMD 8-12, sheet music scales/notes use 13-22

## 📊 Data Flow Examples

### 1. 楽曲作成フロー
```
/getDawItem → ItemStack生成（PDC初期化）
  ↓
右クリック → MainMenuHolder開き
  ↓
"楽譜を作る" → InputModeHolder開き（鍵盤UI表示）
  ↓
鍵盤クリック → ButtonId判定 → musicList[]に値設定
  ↓
GUI閉じる → MusicManager.saveMusicForPdc()
  ↓
InventoryManager.saveInventory()
  ↓
非同期でMySQL保存（Base64エンコード）
```

### 2. 再生フロー
```
/getPlayItem → ラジカセ取得
  ↓
右クリック → ItemsPlayModeHolder開き
  ↓
"再生" → PlayMusic.playMusic()
  ↓
毎tick:
  - musicList[count]読込
  - PlaySound.playNote() → 音声出力
  - isPrivate判定（プレイヤー個別 vs 全員）
  - count++
  ↓
-1到達 → stopTask() → 再生終了
```

### 3. インベ永続化フロー
```
プレイヤー退出
  ↓
PlayerQuitListener
  ↓
InventoryManager.saveInventory()（メモリ+MySQL非同期保存）
  ↓
[次回ログイン時]
  ↓
PlayerJoinListener
  ↓
InventoryManager.loadFromDB()（非同期MySQL SELECT）
  ↓
Base64デコード → ItemStack[] 復帰
```

## 🛠️ Common Tasks

### タスク1: 新しいボタンを追加する
```java
// 1. ButtonIdに追加
private enum ButtonId {
    MY_NEW_BUTTON("MY NEW BUTTON"),
    // ...
}

// 2. ItemStack作成時にセット
MakeItem.setItemMeta(item, "ボタン名", null, "icon_name", 
                     ItemData.BUTTON_ID, ButtonId.MY_NEW_BUTTON.id());

// 3. onClick()でswitch処理
switch(buttonId) {
    case MY_NEW_BUTTON -> { /* 処理 */ }
}
```

### タスク2: 新しいGUIメニューを作成する
```java
public class MyMenuHolder extends BaseGuiHolder {
    public MyMenuHolder() {
        this.inv = Bukkit.createInventory(this, 54, Component.text("MyMenu"));
        setup();
    }
    
    @Override
    public void onClick(InventoryClickEvent event) {
        // onClick処理
    }
    
    @Override
    public void onClose(Player player) {
        if (!closeFlag) return;
        // 保存処理
    }
}

// plugin.ymlなどで登録
// リスナー(ClickInventoryListener)が自動的にポリモーフィズムで呼び出し
```

### タスク3: MySQL クエリを実行する
```java
CompletableFuture.supplyAsync(() -> {
    String sql = "SELECT * FROM player_data WHERE uuid = ?";
    
    try (Connection con = mysql.getConn();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, uuid.toString());
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getString("inventory");
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
    
}).thenAcceptAsync(result -> {
    // メインスレッドで処理
});
```

### タスク4: 新しいアイテムを追加する
```yaml
# config.yml に追加
items:
  myNewItem:
    material: paper
    cmd: 50

# ItemsConfig.java に追加
private static Model myNewItem;

public static void loadItems(Plugin plugin) {
    // ...
    myNewItem = new Model(config.getInt("items.myNewItem.cmd", 0), 
                          stringToMaterial(config.getString("items.myNewItem.material", "paper")));
}

public static Model getMyNewItem() {
    return myNewItem;
}
```

## 📝 プロジェクト固有ファイル構成

```
src/main/java/io/github/itokagimaru/itokagimaru_daw/
├── Itokagimaru_daw.java              [メインクラス]
├── commands/                         [コマンド実装]
│   ├── GetDawItem.java
│   └── ...
├── config/                           [設定読込]
│   ├── ItemsConfig.java
│   └── IconsConfig.java
├── data/                             [PDCキー定義]
│   ├── ItemData.java
│   └── *Key.java
├── db/                               [DB接続]
│   └── MySQLManager.java
├── gui/                              [GUI実装]
│   ├── menu/BaseGuiHolder.java
│   ├── menu/daw/InputModeHolder.java
│   └── listener/ClickInventoryListener.java
├── listeners/                        [イベント処理]
│   ├── ItemUseListener.java
│   └── PlayerQuitListener.java
├── manager/                          [ロジック層]
│   ├── MusicManager.java
│   ├── InventoryManager.java
│   └── PlayMusicManager.java
├── task/                             [実行タスク]
│   └── PlayMusic.java
└── util/                             [ユーティリティ]
    ├── PlaySound.java
    └── MakeItem.java
```

## ⚡ パフォーマンス考慮事項

### 1. BPM計算による軽量な再生
```java
long interval = 1200 / bpm;  // シンプル計算で最適化
// BPM=60 → interval=20tick（1秒）
// BPM=240 → interval=5tick（0.25秒）
```

### 2. 非同期MySQL I/O
- ItemStack[]のシリアライズ：別スレッド
- Bukkit API呼び出し（Inventory操作）：メインスレッド
- `CompletableFuture` で自動管理

### 3. インベ保存の頻度管理
- **現在**: プレイヤー退出時のみ（DB負荷軽減）
- **リスク**: クラッシュ時のデータロス
- **V2対策**: 自動保存間隔の実装検討

