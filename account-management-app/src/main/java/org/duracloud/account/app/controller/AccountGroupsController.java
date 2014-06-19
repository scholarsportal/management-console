/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.app.controller.GroupsForm.Action;
import org.duracloud.account.db.model.DuracloudGroup;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.DuracloudGroupService;
import org.duracloud.account.db.util.error.DuracloudGroupAlreadyExistsException;
import org.duracloud.account.db.util.error.DuracloudGroupNotFoundException;
import org.duracloud.account.db.util.error.InvalidGroupNameException;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

/**
 * 
 * @author Daniel Bernstein Date: Nov 9, 2011
 */
@Controller
@Lazy
public class AccountGroupsController extends AbstractAccountController {

    protected static final String AVAILABLE_USERS_KEY = "availableUsers";
    protected static final String GROUPS_VIEW_ID = "account-groups";
    protected static final String GROUP_VIEW_ID = "account-group";
    protected static final String GROUP_EDIT_VIEW_ID = "account-group-edit";

    protected static final String GROUPS_FORM_KEY = "groupsForm";
    protected static final String GROUP_FORM_KEY = "groupForm";
    protected static String GROUP_USERS_KEY = "groupUsers";
    protected static String GROUP_NAME_KEY = "groupName";
    protected static String GROUP_KEY = "group";

    protected static final String GROUPS_PATH = ACCOUNT_PATH + "/groups";
    protected static final String GROUP_PATH = GROUPS_PATH
            + "/{groupName:[a-z0-9._\\-@]+}";
    protected static final String GROUP_EDIT_PATH = GROUP_PATH + "/edit";

    private static final String GROUP_NAME_RESERVED_ERROR_CODE = "error.groupName.reserved";
    private static final String GROUP_NAME_INVALID_ERROR_CODE = "error.groupName.invalid";
    private static final String GROUP_NAME_EXISTS_ERROR_CODE = "error.groupName.exists";
    private static final String GROUP_NAME_RESERVED_MESSAGE = "The group name you specified is a reserved name. Please try another.";
    private static final String GROUP_NAME_INVALID_MESSAGE = "Group names can contain only lowercase letters, numbers, '.', '@',"
            + "'-', or '_' and must start and end only with letters or numbers.";
    private static final String GROUP_NAME_EXISTS_MESSAGE = "This group already exists. Please choose another name.";

    @Autowired
    protected DuracloudGroupService duracloudGroupService;

    @RequestMapping(value = GROUPS_PATH, method = RequestMethod.GET)
    public String getGroups(@PathVariable Long accountId, Model model)
            throws Exception {
        addGroupsObjectsToModel(getAccountService(accountId), model);

        return GROUPS_VIEW_ID;
    }

    @RequestMapping(value = GROUPS_PATH, method = RequestMethod.POST)
    public String modifyGroups(@PathVariable Long accountId, Model model,
            @ModelAttribute(GROUPS_FORM_KEY) @Valid GroupsForm form,
            BindingResult result) throws Exception {

        AccountService as = this.accountManagerService.getAccount(accountId);
        GroupsForm.Action action = form.getAction();

        if (action == Action.ADD) {
            String name = form.getGroupName();
            String groupName = DuracloudGroup.PREFIX + name;
            try {
                duracloudGroupService.createGroup(groupName, accountId);

            } catch (InvalidGroupNameException e) {
                if (groupName
                        .equalsIgnoreCase(DuracloudGroup.PUBLIC_GROUP_NAME)) {
                    result.rejectValue(GROUP_NAME_KEY,
                            GROUP_NAME_RESERVED_ERROR_CODE,
                            GROUP_NAME_RESERVED_MESSAGE);

                } else {
                    result.rejectValue(GROUP_NAME_KEY,
                            GROUP_NAME_INVALID_ERROR_CODE,
                            GROUP_NAME_INVALID_MESSAGE);
                }

            } catch (DuracloudGroupAlreadyExistsException e) {
                result.rejectValue(GROUP_NAME_KEY,
                        GROUP_NAME_EXISTS_ERROR_CODE, GROUP_NAME_EXISTS_MESSAGE);
            }

            if (!result.hasFieldErrors()) {
                return formatGroupRedirect(accountId, groupName, "/edit");
            }
        } else {
            String[] groups = form.getGroupNames();
            if (groups != null) {
                for (String name : groups) {
                    DuracloudGroup group = duracloudGroupService.getGroup(name,
                            accountId);
                    removeGroup(group, accountId);
                }
            }
        }

        addGroupsObjectsToModel(as, model);

        return GROUPS_VIEW_ID;
    }

    @RequestMapping(value = GROUP_PATH, method = RequestMethod.GET)
    public String getGroup(@PathVariable Long accountId,
            @PathVariable String groupName, Model model) throws Exception {

        addUserToModel(model);
        model.addAttribute(GROUPS_FORM_KEY, new GroupsForm());
        AccountService as = getAccountService(accountId);
        List<DuracloudGroup> groups = getGroups(accountId);
        DuracloudGroup group = getGroup(groupName, groups);
        addGroupToModel(group, model);

        addGroupsObjectsToModel(as, groups, model);
        return GROUP_VIEW_ID;
    }

    private List<DuracloudGroup> getGroups(Long accountId) {
        Set<DuracloudGroup> set = this.duracloudGroupService
                .getGroups(accountId);
        List<DuracloudGroup> list = new LinkedList<DuracloudGroup>();
        if (set != null) {
            list.addAll(set);
        }

        return list;
    }

    @RequestMapping(value = GROUP_EDIT_PATH, method = RequestMethod.GET)
    public String editGroup(@PathVariable Long accountId,
            @PathVariable String groupName, HttpServletRequest request,
            Model model) throws Exception {

        AccountService as = getAccountService(accountId);

        List<DuracloudGroup> groups = getGroups(accountId);
        addGroupsObjectsToModel(as, groups, model);

        DuracloudGroup group = getGroup(groupName, groups);
        addGroupToModel(group, model);

        model.addAttribute(GROUP_FORM_KEY, new GroupForm());
        Set<DuracloudUser> groupUsers = group.getUsers();

        addAvailableUsersToModel(as, groupUsers, model);
        HttpSession session = request.getSession();
        session.removeAttribute(GROUP_USERS_KEY);
        addGroupUsersIfNotAlreadyInSession(group, model, session);
        return GROUP_EDIT_VIEW_ID;
    }

    @RequestMapping(value = GROUP_EDIT_PATH, method = RequestMethod.POST)
    public String editGroup(@PathVariable Long accountId,
            @PathVariable String groupName,
            @ModelAttribute(GROUP_FORM_KEY) @Valid GroupForm form,
            HttpServletRequest request, Model model) throws Exception {

        GroupForm.Action action = form.getAction();
        AccountService as = getAccountService(accountId);
        List<DuracloudGroup> groups = getGroups(accountId);
        DuracloudGroup group = getGroup(groupName, groups);
        addGroupToModel(group, model);

        HttpSession session = request.getSession();

        List<DuracloudUser> groupUsers = addGroupUsersIfNotAlreadyInSession(
                group, model, session);

        // handle save case
        if (action == GroupForm.Action.SAVE) {
            Set<DuracloudUser> users = new HashSet<DuracloudUser>(groupUsers);
            save(group, users, accountId, form);
            session.removeAttribute(GROUP_USERS_KEY);

            return formatGroupRedirect(accountId, groupName, null);

        }

        Collection<DuracloudUser> availableUsers = getAvailableUsers(as,
                groupUsers);

        if (action == GroupForm.Action.ADD) {
            // handle add
            String[] availableUsernames = form.getAvailableUsernames();
            if (availableUsernames != null) {
                for (String username : availableUsernames) {
                    DuracloudUser user = getUser(username, availableUsers);
                    if (user != null) {
                        groupUsers.add(user);
                    }
                }

                form.setAvailableUsernames(null);
                form.setGroupUsernames(availableUsernames);
            }

        } else if (action == GroupForm.Action.REMOVE) {
            String[] groupUsernames = form.getGroupUsernames();
            if (groupUsernames != null) {
                for (String username : groupUsernames) {
                    DuracloudUser user = getUser(username, groupUsers);
                    if (user != null) {
                        groupUsers.remove(user);
                        availableUsers.add(user);
                    }
                }

                form.setGroupUsernames(null);
                form.setAvailableUsernames(groupUsernames);
            }
        } else {
            throw new DuraCloudRuntimeException("[" + action
                    + "] not supported.");
        }

        model.addAttribute(GROUP_FORM_KEY, form);
        addAvailableUsersToModel(availableUsers, groupUsers, model);
        addGroupsObjectsToModel(as, groups, model);

        return GROUP_EDIT_VIEW_ID;
    }

    private String formatGroupRedirect(Long accountId, String groupName,
            String suffix) {
        String redirect = "redirect:" + ACCOUNTS_PATH + GROUP_PATH;
        redirect = redirect.replace("{accountId}", String.valueOf(accountId));
        redirect = redirect.replaceAll("\\{groupName.*\\}",
                String.valueOf(groupName));
        redirect += (suffix != null ? suffix : "");
        return redirect;
    }

    private void addAvailableUsersToModel(Collection<DuracloudUser> allUsers,
            Collection<DuracloudUser> groupUsers, Model model) {
        if (allUsers != null && groupUsers != null) {
            allUsers.removeAll(groupUsers);
        }
        model.addAttribute(AVAILABLE_USERS_KEY, allUsers);
    }

    private DuracloudUser getUser(String username,
            Collection<DuracloudUser> users) {
        for (DuracloudUser user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    private List<DuracloudUser> addGroupUsersIfNotAlreadyInSession(
            DuracloudGroup group, Model model, HttpSession session) {
        List<DuracloudUser> groupUsers = (List<DuracloudUser>) session
                .getAttribute(GROUP_USERS_KEY);
        if (groupUsers == null) {
            groupUsers = new LinkedList<DuracloudUser>();
            groupUsers.addAll(group.getUsers());
            Collections.sort(groupUsers, USERNAME_COMPARATOR);
            session.setAttribute(GROUP_USERS_KEY, groupUsers);
        }

        model.addAttribute(GROUP_USERS_KEY, groupUsers);

        return groupUsers;
    }

    private void save(DuracloudGroup group, Set<DuracloudUser> groupUsers,
            Long accountId, GroupForm form)
            throws DuracloudGroupNotFoundException {
        duracloudGroupService.updateGroupUsers(group, groupUsers, accountId);
        form.reset();
    }

    private void removeGroup(DuracloudGroup group, Long accountId) {
        this.duracloudGroupService.deleteGroup(group, accountId);
    }

    private AccountService getAccountService(Long accountId) throws Exception {
        return this.accountManagerService.getAccount(accountId);
    }

    private void addGroupsObjectsToModel(AccountService as, Model model)
            throws Exception {
        Long accountId = as.retrieveAccountInfo().getId();
        addGroupsObjectsToModel(as, this.getGroups(accountId), model);
    }

    private void addGroupsObjectsToModel(AccountService as,
            List<DuracloudGroup> groups, Model model) throws Exception {
        addUserToModel(model);
        model.addAttribute("accountId", as.getAccountId());
        if (!model.asMap().containsKey(GROUPS_FORM_KEY)) {
            model.addAttribute(GROUPS_FORM_KEY, new GroupsForm());
        }
        addGroupsToModel(model, groups);
    }

    private void addAvailableUsersToModel(AccountService as,
            Collection<DuracloudUser> groupUsers, Model model) {
        Collection<DuracloudUser> availableUsers = getAvailableUsers(as,
                groupUsers);
        addAvailableUsersToModel(availableUsers, groupUsers, model);
    }

    private Collection<DuracloudUser> getAvailableUsers(AccountService as,
            Collection<DuracloudUser> groupUsers) {
        Set<DuracloudUser> allUsers = as.getUsers();
        LinkedList<DuracloudUser> list = new LinkedList<DuracloudUser>();
        list.addAll(allUsers);
        for (DuracloudUser user : allUsers) {
            if (user.isRoot()) {
                list.remove(user);
            }
        }

        if (groupUsers != null) {
            list.removeAll(groupUsers);
        }

        Collections.sort(list, USERNAME_COMPARATOR);
        return list;
    }

    private static Comparator<DuracloudUser> USERNAME_COMPARATOR = new Comparator<DuracloudUser>() {

        @Override
        public int compare(DuracloudUser o1, DuracloudUser o2) {
            return o1.getUsername().compareTo(o2.getUsername());
        }
    };

    private void addGroupToModel(DuracloudGroup group, Model model) {
        model.addAttribute(GROUP_KEY, group);
        model.addAttribute(GROUP_USERS_KEY, group.getUsers());
    }

    private DuracloudGroup getGroup(String groupName,
            List<DuracloudGroup> groups) throws DuracloudGroupNotFoundException {
        for (DuracloudGroup g : groups) { 
            if (g.getName().equalsIgnoreCase(groupName)) {
                return g;
            }
        }

        throw new DuracloudGroupNotFoundException("no group named '"
                + groupName + "' found in group set.");
    }

    private void addGroupsToModel(Model model, List<DuracloudGroup> groups) {
        model.addAttribute("groups", groups);
    }

    public void setDuracloudGroupService(DuracloudGroupService groupService) {
        this.duracloudGroupService = groupService;

    }

}
