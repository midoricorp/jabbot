package org.wanna.jabbot.binding.privilege;

/**
 * Interface which acts as a flag to indicate that the action which is currently executed requires resource privileges to be checked.
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-09-04
 */
public interface PrivilegedAction {
    /**
     * Returns the privilege which is required to execute an action
     * @return privilege.
     */
    Privilege getRequiredPrivilege();
}
