# 開発ガイド

## 必要なもの

- JDK 21
- Gradle(Wrapper を同梱しているので `./gradlew` で動きます)

## ビルド

```bash
./gradlew build
```

成果物は `build/libs/` に出力されます。Paper の開発用バンドル(paperweight-userdev)を使っているため、初回のビルドには時間がかかります。

## ブランチ

Minecraft のバージョンごとにブランチを分けています。

| ブランチ | Minecraft | 備考 |
|---|---|---|
| `ver/1.21.11` | 1.21.11 (Paper) | 現在のメインブランチ |
| `ver/1.21.1` | 1.21.1 | |
| `ver/1.20.2` | 1.20.2 | |
| `master` | 1.15.2 | 旧バージョン |

`master` に追加された機能を `ver/1.21.11` に持ってくる場合は、NMS・Adventure API の違いがあるため cherry-pick ではなく手で移植してください(例: `ChatColor` → Adventure の `Component`、`v1_15_R1` の NMS → Mojang マッピングの NMS)。

## ソースの構成

`src/main/java/com/github/mori01231/lifecore/` 以下(Java と Kotlin が混在しています)。

| パッケージ | 内容 |
|---|---|
| `LifeCore.kt` | プラグインのメインクラス。コマンド・リスナーの登録、設定ファイルの読み書き |
| `command/` | コマンド |
| `listener/` | イベントリスナー(`listener/item/` はカスタムアイテムの処理) |
| `gui/` | インベントリGUI(`/dropprotect` など) |
| `config/` | 設定・データファイルのクラス(kotlinx.serialization + kaml) |
| `reward/` | オンライン報酬 |
| `block/` | カスタムブロック |
| `util/` | ユーティリティ(`ItemUtil`、`MapUtil` など) |

`src/main/resources/data/` にはバニラのタグデータ(Picksaw のツール判定などに使用)が入っています。

## コマンドを追加するとき

1. `command/` にクラスを作る
2. `LifeCore.kt` の `onEnable` で `registerCommand("名前", ...)` する
3. `plugin.yml` の `commands` と `permissions` に追加する(`lifecore.*` の `children` にも追加)
4. [docs/commands.md](commands.md) を更新する

`plugin.yml` に登録していないコマンドを `registerCommand` すると、起動時にエラーになります。

## データファイルを追加するとき

`config/` に `@Serializable` な data class を作り、`LifeCore.kt` で既存の `drop-protect.yml` などと同じように読み込み・定期保存・停止時の保存を追加してください。
