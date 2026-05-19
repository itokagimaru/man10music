# Man10Music

## コマンド

- `/mmusic get daw`  
  DAWアイテムを取得します。

- `/mmusic get playItem`  
  再生アイテムを取得します。

- `/mmusic help`  
  管理者用コマンド一覧を表示します。

- `/mmusicfurnitures`  
  家具連携用コマンドです。Man10系の家具プラグイン導入が必要です。  
  - `workspace <player>`: ワークスペースGUIを開きます。  
  - `radio play <player> <x> <y> <z>`: 指定座標のラジオ用アーマースタンドで再生GUIを開きます。  
  - `radio remove <player> <x> <y> <z>`: 指定座標のラジオ用アーマースタンドを削除します。

## コンフィグ（`config.yml`）

### `mysql`
- `host`: MySQLホスト名
- `port`: MySQLポート
- `database`: データベース名
- `user`: 接続ユーザー名
- `password`: 接続パスワード

### `musicConfig.musics`
- `maxLength`: 楽曲データの最大長

### `musicConfig.volume`
- `default`: 通常再生の音量
- `autoPlay`: 自動再生の音量
- `radius`: 公開再生時の可聴範囲

### `items`
各アイテムの `material` と `cmd` を定義します。  
例: `keyBoard`, `walkMan`, `cassette`, `cassetteWorkspace`, `radio`

### `icons`
GUIアイコンの `material` と `cmd` を定義します。  
例: `triangle.*`, `sheetMusic.scale.*`, `sheetMusic.note.*`
