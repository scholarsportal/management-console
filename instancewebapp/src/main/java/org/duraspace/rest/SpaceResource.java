package org.duraspace.rest;

/**
 * Provides interaction with spaces
 *
 * @author Bill Branan
 */
public class SpaceResource {

    /**
     * Provides a listing of all spaces for a customer. Open spaces are
     * always included in the list, closed spaces are included based
     * on user authorization.
     *
     * @return XML listing of spaces
     */
    public static String getSpaces(String customerID) {
        String xml = "<spaces />";
        return xml;
    }

    /**
     * Gets the properties of a space.
     *
     * @param customerID
     * @param spaceID
     * @return XML listing of space properties
     */
    public static String getSpaceProperties(String customerID,
                                            String spaceID){
        String xml = "<space />";
        return xml;
    }

    /**
     * Gets a listing of the contents of a space.
     *
     * @param customerID
     * @param spaceID
     * @return XML listing of space contents
     */
    public static String getSpaceContents(String customerID,
                                          String spaceID){
        String xml = "<contents />";
        return xml;
    }

    /**
     * Adds a space.
     *
     * @param customerID
     * @param spaceID
     * @param spaceName
     * @param spaceAccess
     * @return success
     */
    public static boolean addSpace(String customerID,
                                   String spaceID,
                                   String spaceName,
                                   String spaceAccess){
        return true;
    }

    /**
     * Updates a the properties of a space.
     *
     * @param customerID
     * @param spaceID
     * @param spaceName
     * @param spaceAccess
     * @return success
     */
    public static boolean updateSpaceProperties(String customerID,
                                                String spaceID,
                                                String spaceName,
                                                String spaceAccess) {
        return true;
    }

    /**
     * Deletes a space, removing all included content.
     *
     * @param customerID
     * @param spaceID
     * @return success
     */
    public static boolean deleteSpace(String customerID,
                                      String spaceID){
        return true;
    }

}
