package com.github.mori01231.lifecore.reflector.towny;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Town extends TownyObject {
    /**
     * Returns the list of residents who are outlawed from this town. The actual type of the list is {@link List}&lt;{@link Resident}&gt;.
     * @return the list
     */
    @NotNull List<?> getOutlaws();
}
