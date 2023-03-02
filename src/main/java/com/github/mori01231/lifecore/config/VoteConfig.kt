package com.github.mori01231.lifecore.config

import com.github.mori01231.lifecore.vote.CommandReward
import com.github.mori01231.lifecore.vote.EconomyReward
import com.github.mori01231.lifecore.vote.ItemReward
import com.github.mori01231.lifecore.vote.MythicItemReward
import com.github.mori01231.lifecore.vote.VoteReward
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class VoteConfig(
    val rewards: List<VoteReward> = listOf(
        CommandReward("minecraft:give <player> minecraft:air 0"),
        MythicItemReward("vote_ticket"),
        ItemReward(Material.DIAMOND_BLOCK),
        EconomyReward(50000),
    ),
)
