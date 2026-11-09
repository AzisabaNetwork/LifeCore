# 開発ガイド

## 必要なもの

- JDK 21(`gradle.properties` の `javaVersion`。ツールチェーンは自動で取得されます)
- Gradle(Wrapper を同梱しているので `./gradlew` で動きます)

## ビルド

```bash
./gradlew build
```

成果物は `build/libs/LifeCore-<バージョン>.jar` です(`-plain.jar` は依存ライブラリを含まないので、サーバーには入れないでください)。Paper の開発用バンドル(paperweight-userdev)を使っているため、初回のビルドには時間がかかります。

jar は Mojang マッピングのまま出力されます(reobf はしません)。Paper は 1.20.5 以降 Mojang マッピングで動いており、26.x 以降は難読化自体がなくなるためです。

## バージョンと依存関係

| ファイル | 内容 |
|---|---|
| `gradle.properties` | 対象の Minecraft バージョン(`minecraftVersion`)、Paper の開発用バンドル(`paperDevBundle`)、Java のバージョン(`javaVersion`)、プラグインのバージョン(`pluginVersion`) |
| `gradle/libs.versions.toml` | 依存ライブラリ・プラグインのバージョン(Version Catalog) |

- 成果物のバージョンは `<pluginVersion>+<minecraftVersion>`、`plugin.yml` の `api-version` は `minecraftVersion` から自動で入ります。
- `net.azisaba` 系のプラグインは [maven.azisaba.net](https://maven.azisaba.net/releases/net/azisaba/) から取得します。更新するときは `libs.versions.toml` のバージョンを書き換えてください。
- `adventure` のバージョンは、対象の Paper に同梱されている Adventure と合わせてください(開発用バンドルがバージョン指定なしの Adventure モジュールに依存しているため、BOM が必要です)。

### Minecraft 26.x に移行するとき

1. `gradle.properties` を書き換える
   - `minecraftVersion` を `26.x` に
   - `paperDevBundle` を [PaperMC のリポジトリ](https://repo.papermc.io/repository/maven-public/io/papermc/paper/dev-bundle/maven-metadata.xml) にある `26.x.build.<番号>-stable` に
   - `javaVersion` を Paper が要求するバージョン(26.x は Java 25)に
2. `libs.versions.toml` の `adventure` を Paper 26.x に同梱のバージョンに合わせ、`net.azisaba` 系プラグインも 26.x 対応版に更新する
3. ビルドして、NMS(`net.minecraft.*`)を使っている箇所のコンパイルエラーを直す
   - NMS を使っているファイルは `grep -rl "import net.minecraft" src` で確認できます(`util/ItemUtil.kt`、`network/PacketHandler.kt`、`listener/DropProtectListener.kt`、`util/PromptSign.kt`、`map/` など)

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

`src/main/java/com/github/mori01231/lifecore/` 以下。すべて Kotlin で書かれています(新しいコードも Kotlin で書いてください)。

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
