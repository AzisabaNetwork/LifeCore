# コマンド一覧

「デフォルト」列は、パーミッションを明示的に与えていない場合に誰が使えるかを示します(`plugin.yml` の `default`)。

- **全員**: `default: true`
- **OP**: `default: false`、または `plugin.yml` にパーミッションが定義されていない

`lifecore.*` を付与すると、ほぼすべてのコマンドが使えるようになります。

## 情報表示

| コマンド | 別名 | 説明 | パーミッション | デフォルト |
|---|---|---|---|---|
| `/wiki` | | Wiki のリンクを表示する | `lifecore.wiki` | 全員 |
| `/website` | `/web` `/webpage` `/hp` `/homepage` | 公式サイトのリンクを表示する | `lifecore.website` | 全員 |
| `/help` | | ヘルプを表示する(項目は `config.yml` の `Help`) | `lifecore.help` | 全員 |
| `/guide` | | ガイドブックを入手する | `lifecore.guide` | 全員 |
| `/vote` | | 投票サイト(JMS / MinePortal)のリンクを表示する | `lifecore.vote` | 全員 |
| `/rank` | | 自分のランク情報を表示する | `lifecore.rank` | OP |
| `/commandlist` | `/commands` | 使えるコマンドの一覧をGUIで表示する | `lifecore.commandlist` | 全員 |
| `/maplist` | `/maps` | 地図の一覧をGUIで表示する | `lifecore.maplist` | 全員 |
| `/debt [uuid]` | | 借金を表示する(他人を見るには `lifecore.debt.others`) | `lifecore.debt` | 全員 |
| `/damagelog` | | ダメージログの表示を切り替える | `lifecore.damagelog` | 全員 |

## サーバー移動・テレポート

| コマンド | 別名 | 移動先 | パーミッション | デフォルト |
|---|---|---|---|---|
| `/life` | | Life | `lifecore.life` | 全員 |
| `/resource` | `/liferesource` | LifeResource | `lifecore.resource` | 全員 |
| `/event` | `/lifeevent` | LifeEvent | `lifecore.event` | 全員 |
| `/build` | `/lifebuild` | LifeBuild | `lifecore.build` | OP |
| `/townserver` | `/towns` `/lifetown` | LifeTown | `lifecore.townserver` | 全員 |
| `/travel` | `/lifetravel` | LifeTravel | `lifecore.travel` | OP |
| `/pve` | `/lifepve` | ダンジョンロビー(`config.yml` の `use-pve-command-as-teleport` で動作が変わる) | `lifecore.pve` | OP |
| `/pve0`〜`/pve3` | `/lifepve0`〜`/lifepve3` | LifePvE 各サーバー | `lifecore.pve` | OP |
| `/tutorial` | | チュートリアル地点 | `lifecore.tutorial` | OP |

## アイテム・インベントリ

詳しくは [アイテム保護](item-protection.md) を参照してください。

| コマンド | 別名 | 説明 | パーミッション | デフォルト |
|---|---|---|---|---|
| `/trash` | `/gomi` | ゴミ箱を開く。捨てたアイテムは換金される | `lifecore.trash` | 全員 |
| `/dropprotect` | | レア度・PvEレベル単位のドロップ保護をGUIで設定する | `lifecore.dropprotect` | 全員 |
| `/trashprotect` | `/gomiprotect` | レア度・PvEレベル単位のゴミ箱保護をGUIで設定する | `lifecore.trashprotect` | 全員 |
| `/protect` | | 手に持ったアイテムの種類をドロップ/ゴミ箱から保護する(切り替え) | `lifecore.protect` | 全員 |
| `/dropnotify` | | アイテムをドロップしたときの通知を切り替える | `lifecore.dropnotify` | 全員 |
| `/petclick` | `/migikuristopman` | ペットをスニーク右クリックできないようにする(切り替え) | `lifecore.petclick` | 全員 |
| `/mmid [player]` | `/mythictype` | 手に持ったアイテムの MythicMobs ID を表示する | `lifecore.mmid` | OP |
| `/iteminfo` | | 手に持ったアイテムの詳細情報を表示する(旧 ItemInfoGetter) | `lifecore.iteminfo` | OP |
| `/inventorysorter` | `/is` | インベントリ整理(範囲外ダブルクリックでのソート)の有効/無効を切り替える(旧 InventorySorter) | なし | 全員 |
| `/givedice` | | 抽選ダイス(旧 KotlinDice)のアイテムを入手する | `lifecore.givedice` | OP |
| `/antidrop [add\|remove\|list] [world]` | | ドロップ禁止ワールドを管理する(旧 AntiDrop) | `lifecore.antidrop` | OP |

## その他(プレイヤー向け)

| コマンド | 説明 | パーミッション | デフォルト |
|---|---|---|---|
| `/kiai` | 気合 | `lifecore.kiai` | 全員 |
| `/ngword (add\|remove\|list)` | 自分のNGワードを管理する | `lifecore.ngword` | 全員 |
| `/fixtime` | Autorank のプレイ時間を修正する(クールダウンあり) | `lifecore.fixtime` | 全員 |
| `/townconfig` | 町の設定を変更する | `lifecore.townconfig` | 全員 |
| `/gclistenerrestartextendtimecommand` | GC検知による再起動の延長に投票する(5人で15分延長) | `lifecore.gclistenerrestartextendtimecommand` | 全員 |

## 管理者向け

| コマンド | 説明 | パーミッション | デフォルト |
|---|---|---|---|
| `/worldcreate <name> <environment> [options]` (`/wc`) | ワールドを作成する(下記) | `lifecore.worldcreate` | OP |
| `/schedulerestart <minutes>` | 再起動を予約する | `lifecore.schedulerestart` | OP |
| `/lifecoreconfig` | 領域の設定などを行う(`/lifecoreconfig wand` で範囲指定用のワンドを入手) | `lifecore.lifecoreconfig` | OP |
| `/lifecoreutil <subcommand>` | 管理用ユーティリティ(下記) | `lifecore.lifecoreutil` + `lifecore.lifecoreutil.<サブコマンド>` | OP |
| `/servermoney` | サーバーのお金を表示する | `lifecore.servermoney` | OP |
| `/respawn <player>` | 指定したプレイヤーをリスポーンさせる | `lifecore.respawn` | OP |
| `/killnonadmin` | 管理者用の剣を持っている管理者以外のプレイヤーをキルする | `lifecore.killnonadmin` | OP |
| `/debugvote <player> [service]` | 投票処理をテストする | `lifecore.debugvote` | OP |
| `/noob` | 初参加メッセージを流す | `lifecore.noob` | OP |
| `/firstjoinplus [reload\|setspawn\|debug]` (`/fjp`) | 初参加時の処理(旧 FirstJoinPlus)を管理する | `firstjoinplus.reload` / `firstjoinplus.setspawn` / `firstjoinplus.debug` | OP |

### `/firstjoinplus`

- `reload`: `firstjoin.yml` を再読み込みする
- `setspawn`: 初参加時のスポーン地点を現在位置に設定する
- `debug`: 自分を「初参加プレイヤー状態」にする(テスト用)

設定は `firstjoin.yml` に記述します。旧 FirstJoinPlus の `plugins/FirstJoinPlus/config.yml` が存在する場合は自動的に引き継ぎます。

### `/worldcreate`

```
/wc <name> <environment> [-t <worldType>] [-s <seed>] [-a <true|false>] [-d <difficulty>]
```

| 引数 | 説明 | デフォルト |
|---|---|---|
| `name` | ワールド名(`a-z A-Z 0-9 / . _ -` のみ) | — |
| `environment` | `NORMAL` / `NETHER` / `THE_END` / `CUSTOM` | — |
| `-t` | ワールドタイプ(`NORMAL` / `FLAT` / `LARGE_BIOMES` / `AMPLIFIED`) | `NORMAL` |
| `-s` | シード値。数値でなければ文字列のハッシュ値を使う | ランダム |
| `-a` | 構造物を生成するか | `true` |
| `-d` | 難易度(`PEACEFUL` / `EASY` / `NORMAL` / `HARD`) | `EASY` |

作成したワールドには次の設定が自動で入ります: モブのスポーンなし、ランダムティック 0、即時リスポーン、インベントリ保持、リスポーン範囲 0、PvP 無効、ワールドボーダーの中心を (0, 0)。

Multiverse-Core が導入されている場合は、作成後に `mv import <name> <environment>` を実行して Multiverse に登録します(次回起動時もワールドが読み込まれるようにするため)。

### `/lifecoreutil`

各サブコマンドを使うには、`lifecore.lifecoreutil.<サブコマンド名>` のパーミッションも必要です(ヘルプにも、権限のあるサブコマンドだけが表示されます)。

| サブコマンド | 説明 |
|---|---|
| `updateCommands` | コマンドの補完データを更新する |
| `onlineReward <reload\|give <id>>` | オンライン報酬の設定を再読み込みする / 今の区間の報酬を未受取のプレイヤーに付与する |
| `glowing <true\|false> [player]` | 発光状態を切り替える |
| `addPassenger <player> [sender]` | `<sender>` の上に `<player>` を乗せる |
| `eject` | `<sender>` からプレイヤーを降ろす |
| `setFallDistance <amount> [player]` | 落下距離をセットする |
| `getVelocity` / `setVelocity <x> <y> <z> [player]` | 速度を取得 / セットする |
| `getHealth` / `setHealth <amount> [player]` | 体力を取得 / セットする |
| `isEquippedInAnySlot <mythic type> [player]` | 指定した MythicMobs アイテムを装備しているか調べる |
| `getTag` / `mergeTag <value>` / `setTag <value>` | 手に持ったアイテムのタグを操作する |
| `getBlock` / `reloadBlocks` | カスタムブロックを取得 / 再読み込みする |
| `reloadConfig <plugin>` | 指定したプラグインの config.yml をリロードさせる |
| `clearPlot` | イマサラタウンの plot を消去する |
| `getOfflineMoney <player>` / `setOfflineMoney <player> <amount>` / `addOfflineMoney <player> <amount>` | offline_money を操作する |
| `saveMapData` / `loadMapData` / `resetMapId` | 地図をサーバー間で移動できる形に変換 / 読み込み / ID リセットする |
| `fixItem` | display タグを修正する |

## その他のパーミッション

| パーミッション | 説明 | デフォルト |
|---|---|---|
| `lifecore.picksaw` | Picksaw を `art` / `lifetownart` 以外のワールドでも使える(持っていないと、それ以外のワールドで使った時点で死亡する) | OP |
| `lifecore.canuseadminsword` | 管理者用の剣を使える | OP |
| `lifecore.giveguide` | ガイドブックを他人に渡せる | OP |
| `lifecore.mmid.others` | 他人のアイテムの MythicMobs ID を見られる | OP |
| `lifecore.debt.others` | 他人の借金を見られる | OP |
| `lifecore.fixtime.bypass-cooldown` | `/fixtime` のクールダウンを無視する | OP |
| `lifecore.wand` | ワンドを使える | OP |
| `lifecore.lobby-bypass` | ロビーからの脱出防止を無視する | OP |
| `lifecore.bypass-town-restrictions` | 町の制限を無視する | OP |
| `lifecore.extend-time-immediately` | 1人の投票で再起動をすぐ延長できる | OP |
