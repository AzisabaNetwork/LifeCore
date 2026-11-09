package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.config.PetClickFile
import com.github.mori01231.lifecore.event.AsyncPlayerPreInteractEntityEvent
import de.Keyle.MyPet.MyPetApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CancelPetClickListener : Listener {
    @EventHandler
    fun onClickEntity(e: AsyncPlayerPreInteractEntityEvent) {
        if (!PetClickFile.isDisabled(e.player.uniqueId) && e.player.isSneaking) {
            return
        }
        val uuid = e.entityUniqueID
        val matched = MyPetApi.getMyPetManager().allActiveMyPets.any { pet ->
            val entity = pet.entity.orElse(null) ?: return@any false
            entity.uniqueId == uuid || entity.handle.uniqueID == uuid
        }
        if (matched) {
            e.isCancelled = true
        }
    }
}
