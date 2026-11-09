# LifeCore

The core plugin for Life server in Azisaba Network

[![Github issues](https://img.shields.io/github/issues/AzisabaNetwork/LifeCore)](https://github.com/AzisabaNetwork/LifeCore/issues)
[![Github forks](https://img.shields.io/github/forks/AzisabaNetwork/LifeCore)](https://github.com/AzisabaNetwork/LifeCore/network/members)
[![Github stars](https://img.shields.io/github/stars/AzisabaNetwork/LifeCore)](https://github.com/AzisabaNetwork/LifeCore/stargazers)
[![Github license](https://img.shields.io/github/license/AzisabaNetwork/LifeCore)](https://github.com/AzisabaNetwork/LifeCore/)
![Latest release](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.azisaba.net%2Frepository%2Fmaven-releases%2Fnet%252Fazisaba%252FLifeCore%252Fmaven-metadata.xml)

アジ鯖 Life サーバーのコアプラグインです。サーバー間の移動、ゴミ箱、アイテム保護、カスタムアイテム、オンライン報酬など、Life サーバー固有の機能をまとめて提供します。

## 動作環境

- Paper 1.21.11(このブランチ。ほかのバージョンは [ブランチ一覧](docs/development.md#ブランチ) を参照)
- Java 21
- 必須プラグイン: AziPluginMessaging
- 任意プラグイン: MythicMobs, ItemStash, Rarity, LifePvELevel, Townia, Votifier, MyPet, Autorank, Statz, RyuZUPluginChat, JoinFilter, Multiverse-Core

任意プラグインが無い場合、それを使う機能だけが無効になります。

## ドキュメント

| ドキュメント | 内容 |
|---|---|
| [コマンド一覧](docs/commands.md) | すべてのコマンドとパーミッション |
| [アイテム保護](docs/item-protection.md) | `/dropprotect` `/trashprotect` `/protect` の仕様 |
| [設定ファイル](docs/configuration.md) | `config.yml`、オンライン報酬、データファイル |
| [開発ガイド](docs/development.md) | ビルド方法、ブランチ、ソースの構成 |

## 主なコマンド

プレイヤーがよく使うコマンドです。すべてのコマンドは [コマンド一覧](docs/commands.md) を参照してください。

| コマンド | 説明 |
|---|---|
| `/wiki` `/website` `/vote` | Wiki・公式サイト・投票サイトのリンクを表示する |
| `/help` `/guide` | ヘルプを表示する / ガイドブックを入手する |
| `/life` `/resource` `/event` `/townserver` | 各サーバーへ移動する |
| `/trash` (`/gomi`) | ゴミ箱を開く。捨てたアイテムは換金される |
| `/protect` | 手に持ったアイテムの種類を、ドロップ・ゴミ箱から保護する |
| `/dropprotect` `/trashprotect` | レア度・PvEレベルごとにドロップ・ゴミ箱保護を設定する |
| `/commandlist` `/maplist` | コマンド一覧・地図一覧をGUIで表示する |

## ビルド

```bash
./gradlew build
```

詳しくは [開発ガイド](docs/development.md) を参照してください。

## Contributors

- [Mori01231](https://github.com/Mori01231)

ほかの貢献者は [Contributors](https://github.com/AzisabaNetwork/LifeCore/graphs/contributors) を参照してください。
