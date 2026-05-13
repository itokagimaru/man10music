# Man10Music

## コマンド

- `/getDawItem`  
  DAWアイテムを取得します。

- `/getSheetMusic`  
  空白の楽譜を取得します。

- `/getPlayItem`  
  再生アイテムを取得します。

- `/getCassetteTape`  
  カセットテープを取得します。

- `/getCassetteWorkSpace`  
  カセット編集台を取得します。

- `/setCassettesName`  
  カセットテープに曲名を設定します。

- `/cassetteTransfer`  
  旧PDCで保存された楽曲データを新PDCへ移行します。

- `/getRadio`  
  ラジオアイテムを取得します。

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
例: `keyBoard`, `walkMan`, `cassette`, `sheetMusic`, `cassetteWorkspace`, `radio`

### `icons`
GUIアイコンの `material` と `cmd` を定義します。  
例: `triangle.*`, `sheetMusic.scale.*`, `sheetMusic.note.*`
