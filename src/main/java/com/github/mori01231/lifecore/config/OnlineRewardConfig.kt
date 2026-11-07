package com.github.mori01231.lifecore.config

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.Serializable

@Serializable
data class OnlineRewardConfig(
    @YamlComment(
        "オンライン報酬を有効にするかどうか",
        "有効にしたすべてのサーバーで同じ設定にしてください。",
        "付与済みプレイヤーはDBで管理されるため、複数サーバーで有効にしても同じ報酬が重複して付与されることはありません。",
    )
    val enabled: Boolean = false,
    @YamlComment(
        "報酬の一覧 (キーは報酬ID。DBでの重複判定に使われるため、変更すると別の報酬として扱われます)",
        "interval: 付与間隔 (秒)。UNIX時間をこの間隔で区切った区間ごとに1回だけ付与されます (例: 3600なら毎時0分)",
        "offset: 区間の開始をずらす秒数 (例: interval 3600, offset 1800 なら毎時30分)",
        "items: ItemStashに追加するMythicMobsアイテムの一覧",
    )
    val rewards: Map<String, OnlineReward> = mapOf(
        "diamond" to OnlineReward(3600, items = listOf(OnlineRewardItem("ontime_diamond"))),
        "iron_ingot" to OnlineReward(300, items = listOf(OnlineRewardItem("ontime_iron_ingot"))),
        "experience_bottle" to OnlineReward(1800, items = listOf(OnlineRewardItem("ontime_experience_bottle", 8))),
    ),
    @YamlComment("付与履歴をDBに残す日数 (これより古い履歴は起動時に削除されます)")
    val retentionDays: Int = 7,
)

@Serializable
data class OnlineReward(
    val interval: Long,
    val offset: Long = 0,
    val items: List<OnlineRewardItem>,
) {
    fun currentSlot(epochSecond: Long = System.currentTimeMillis() / 1000): Long =
        Math.floorDiv(epochSecond - offset, interval)
}

@Serializable
data class OnlineRewardItem(
    @YamlComment("MythicMobsのアイテムID")
    val id: String,
    @YamlComment("アイテムの数")
    val amount: Int = 1,
)
