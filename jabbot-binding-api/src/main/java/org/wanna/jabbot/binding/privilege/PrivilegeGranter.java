package org.wanna.jabbot.binding.privilege;

import org.wanna.jabbot.binding.messaging.Resource;

/**
 * Interface which should be implemented by any class which is allowed to check a resource privilege
 *
 * @see {@link org.wanna.jabbot.binding.messaging.Resource}
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-09-04
 */
public interface PrivilegeGranter {
    /**
     * Check if a resource can execute a given action within the realm of a given resrouce.
     * For example if a user can execute a command in a room
     *
     * @param requester the resource which requested the action execution
     * @param action action to be executed
     * @return true if the resource can execute the action, false otherwise
     */
    boolean canExecute(Resource requester, Resource target, PrivilegedAction action);
}
