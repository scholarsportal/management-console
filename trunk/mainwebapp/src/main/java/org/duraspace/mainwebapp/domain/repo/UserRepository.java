
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.duraspace.mainwebapp.domain.model.User;

public interface UserRepository {

    /**
     * <pre>
     * This method persists the provided User.
     * If the provided User contains an ID,
     * ...the existing User will be over-written;
     * ...otherwise the provided User will be a new row.
     *
     * Note: If provided User contains an ID that does not exist
     * in the table, the ID will be ignored.
     * </pre> {@inheritDoc}
     *
     * @return ID of saved user.
     * @throws Exception
     */
    public abstract int saveUser(User user) throws Exception;

    /**
     * <pre>
     * This method returns the User of the provided ID.
     * If no User is found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public abstract User findUserById(int id) throws Exception;

    /**
     * <pre>
     * This method returns all IDs in the table.
     * If no results are found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public abstract List<Integer> getUserIds() throws Exception;

    /**
     * <pre>
     * This method returns the User of the provided DuraSpaceCredential ID.
     * If no User is found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public abstract User findUserByDuraCredId(int id) throws Exception;

}