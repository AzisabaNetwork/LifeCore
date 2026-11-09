# アイテム保護

プレイヤーがアイテムを誤って捨ててしまわないようにする機能です。3つのコマンドで設定します。

| コマンド | 対象 | 設定の単位 | 保存先 |
|---|---|---|---|
| `/dropprotect` | **ドロップ** (Qキーなど) | レア度・PvEレベルの有無 | `drop-protect.yml` |
| `/trashprotect` (`/gomiprotect`) | **ゴミ箱** (`/trash`) | レア度・PvEレベルの有無 | `trash-protect.yml` |
| `/protect` | **ドロップとゴミ箱の両方** | アイテムの種類 | `mythic-item-protect.yml` |

保護されたアイテムは捨てられずにインベントリへ戻ります。インベントリがいっぱいの場合は ItemStash に送られ、`/pickupstash` で回収できます。

## `/dropprotect` と `/trashprotect`

GUIが開き、クリックで各項目のオン/オフを切り替えます。

| スロット | 項目 | 保護される条件 |
|---|---|---|
| 0 | レア度が付与されていないアイテム | Rarity が付いていない |
| 1〜7 | common / uncommon / rare / epic / legendary / mythic / special | そのレア度が付いている |
| 8 | PvEレベルが付与されているアイテム | LifePvELevel の必要レベルが 1 以上 |
| 9 | PvEレベルが付与されていないアイテム | 必要レベルが 0 **かつ** バニラアイテムではない(※) |
| 26 | 閉じる | — |

※ バニラアイテム(データコンポーネントが一切変更されていないアイテム)は、丸石や土などの普通のブロックまで保護されてしまわないよう対象外です。耐久値が減っている・名前を変えたアイテムなどはバニラアイテム扱いになりません。

## `/protect`

手に持っているアイテムの**種類**について、ドロップ/ゴミ箱保護をオン/オフします。もう一度実行すると解除されます。PvEレベルやレア度が付いていないアイテムにも使えます。

「種類」は次の順に決まります(`ItemUtil.getProtectKey`)。

| アイテム | 保護キー | 例 |
|---|---|---|
| MythicMobs アイテム | MythicMobs のアイテムID | `ontime_diamond` |
| LifeItemId タグ付きのアイテム | `lifeitem:<ID>` | `lifeitem:2dd646f1-...` |
| それ以外(バニラアイテムなど) | マテリアルID | `minecraft:diamond` |

> [!NOTE]
> マテリアルIDで保護した場合、**同じマテリアルのアイテムはすべて**保護されます(例: `minecraft:diamond` を保護すると、名前を変えたダイヤモンドも含めすべてのダイヤモンドが対象)。

判定の優先順位は `/protect` が最優先で、該当しなければ `/dropprotect`・`/trashprotect` の設定で判定します。

### データ形式

`plugins/LifeCore/mythic-item-protect.yml`

```yaml
players:
  "<プレイヤーUUID>":
    - "ontime_diamond"
    - "lifeitem:2dd646f1-70a1-4613-92c3-431de7c0126f"
    - "minecraft:diamond"
```

当初は MythicMobs アイテム専用だったため、ファイル名・クラス名(`MythicItemProtectConfig`)にその名残があります。MythicMobs のアイテムIDは接頭辞なしで保存されるため、以前のデータもそのまま使えます。

保護設定(3ファイルとも)は5分ごとと、プラグインの停止時に保存されます。
