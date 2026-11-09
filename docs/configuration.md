# 設定ファイル

設定ファイルはすべて `plugins/LifeCore/` にあります。

| ファイル | 内容 | 編集 |
|---|---|---|
| `config.yml` | 全体の設定 | 手で編集する |
| `config-online-reward.yml` | オンライン報酬 | 手で編集する |
| `drop-protect.yml` | `/dropprotect` の設定データ | プラグインが自動で書き込む |
| `trash-protect.yml` | `/trashprotect` の設定データ | プラグインが自動で書き込む |
| `mythic-item-protect.yml` | `/protect` の設定データ | プラグインが自動で書き込む |

自動で書き込まれるファイルは5分ごとと、プラグインの停止時に保存されます。サーバーの起動中に手で編集しても上書きされるので注意してください。

## `config.yml`

主な項目です。

### 一般

| キー | 説明 | デフォルト |
|---|---|---|
| `lp-server-name` | このサーバーの名前(オンライン報酬の付与履歴に記録される) | `life` |
| `enable-backup-item-tag` | `/townserver` 実行時にアイテムタグをバックアップし、参加時に復元する | `true` |
| `use-pve-command-as-teleport` | `/pve` をサーバー移動ではなく、`pve-teleport` への `mvtp` テレポートとして使う | `true` |
| `tutorial-teleport` / `pve-teleport` | `/tutorial` / `/pve` のテレポート先(`e:<world>:<x>,<y>,<z>:<yaw>:<pitch>`) | — |
| `spawnOnJoin` | 参加時にスポーンへテレポートする | `false` |
| `NonEndermanWorlds` | エンダーマンをスポーンさせないワールド | — |

### URL

| キー | 説明 |
|---|---|
| `website-url` | `/website` で表示するURL |
| `wiki-url` / `unofficial-wiki-url` | `/wiki` で表示するURL |
| `vote-url-jms` / `vote-url-mineportal` | `/vote` で表示するURL |

### ゴミ箱

| キー | 説明 | デフォルト |
|---|---|---|
| `TrashMoneyPerItem` | アイテム1個あたりの換金額 | `1` |
| `TrashMoneyPerSpecialItem` | `Trash.Items` に含まれるアイテム1個あたりの換金額 | `5` |
| `Trash.Items` | 特別な換金額にするアイテムの表示名 | — |

### GC検知による自動再起動

| キー | 説明 | デフォルト |
|---|---|---|
| `enable-gc-detector` | GC検知を有効にする | `false` |
| `gc-threshold-max-minutes-between` | この分数以内に起きたGCを連続とみなす | `5` |
| `gc-threshold-count` | 連続GCがこの回数に達したら `gc-triggered-command` を実行する | `10` |
| `gc-triggered-command` | 実行するコマンド。`@delay <ticks> <command>` で遅延実行、`@schedulerestart <seconds>` で延長投票付きの再起動予約 | — |

再起動の延長投票は、5人が投票する(または `lifecore.extend-time-immediately` を持つプレイヤーが投票する)と15分延長されます。

### その他

| キー | 説明 | デフォルト |
|---|---|---|
| `destroy-experience-orb-on-chunk-load` | チャンク読み込み時に経験値オーブを消す | `false` |
| `disable-item-frame-map-update` | 起動時に額縁内の地図を更新しない | `false` |
| `adminSwordNotifyWebhookURL` / `adminSwordNotifyWebhookUsername` | 管理者用の剣(攻撃力が極端に高いアイテム)が使われたときに通知する Discord Webhook | `null` |
| `http-server` | ルール同意用HTTPサーバー(`enabled` / `port` / `token`) | 無効 |
| `database` | MariaDB / MySQL の接続設定 | — |
| `custom-model-data` | ワンドなどのカスタムモデルデータ | — |
| `custom-blocks` | カスタムブロックの定義 | — |
| `Rank` / `Help` | `/rank` / `/help` で表示する内容 | — |

## `config-online-reward.yml`

一定時間ごとに、オンラインのプレイヤーへ MythicMobs アイテムを ItemStash 経由で配布します。ファイルが無ければ、コメント付きのデフォルト設定が生成されます。

```yaml
# 有効にしたすべてのサーバーで同じ設定にしてください。
# 付与済みかどうかはDBで管理されるため、複数サーバーで有効にしても重複して付与されません。
enabled: false
rewards:
  diamond:            # 報酬ID(DBでの重複判定に使われる。変えると別の報酬扱いになる)
    interval: 3600    # 付与間隔(秒)。UNIX時間をこの間隔で区切った区間ごとに1回
    offset: 0         # 区間の開始をずらす秒数(interval 3600, offset 1800 なら毎時30分)
    items:
      - id: ontime_diamond   # MythicMobs のアイテムID
        amount: 1
retentionDays: 7      # 付与履歴をDBに残す日数
```

変更後は `/lifecoreutil onlineReward reload` で再読み込みできます。
