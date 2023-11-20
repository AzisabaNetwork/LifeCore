package com.github.mori01231.lifecore.listener;

import com.github.mori01231.lifecore.config.PetClickFile;
import com.github.mori01231.lifecore.event.AsyncPlayerPreInteractEntityEvent;
import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.MyPetBukkitEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class CancelPetClickListener implements Listener {
    @EventHandler
    public void onClickEntity(AsyncPlayerPreInteractEntityEvent e) {
        if (!PetClickFile.isDisabled(e.getPlayer().getUniqueId()) && e.getPlayer().isSneaking()) {
            return;
        }
        boolean matched = false;
        for (MyPet pet : MyPetApi.getMyPetManager().getAllActiveMyPets()) {
            Optional<MyPetBukkitEntity> opt = pet.getEntity();
            if (!opt.isPresent()) {
                continue;
            }
            if (opt.get().getUniqueId().equals(e.getInteractedEntity().getUniqueID()) ||
                    opt.get().getHandle().getUniqueID().equals(e.getInteractedEntity().getUniqueID())) {
                matched = true;
                break;
            }
        }
        if (matched) {
            e.setCancelled(true);
        }
    }
}
