/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.app.controller.GroupsForm.Action;
import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudGroupService;
import org.duracloud.account.util.error.DuracloudGroupNotFoundException;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author  Daniel Bernstein
 *         Date: Nov 9, 2011
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
    protected static String GROUP_KEY = "group";

    protected static final String GROUPS_PATH = ACCOUNT_PATH + "/groups";
    protected static final String GROUP_PATH = GROUPS_PATH + "/{groupName}";
    protected static final String GROUP_EDIT_PATH = GROUP_PATH + "/edit";

    @Autowired
    protected DuracloudGroupService duracloudGroupService;
    
    @RequestMapping(value = GROUPS_PATH, method = RequestMethod.GET)
    public String getGroups(@PathVariable int accountId, Model model)
        throws Exception {
        addGroupsObjectsToModel(getAccountService(accountId), model);
        return GROUPS_VIEW_ID;
    }

    @RequestMapping(value = GROUPS_PATH, method = RequestMethod.POST)
    public String
        modifyGroups(@PathVariable int accountId,
                  @ModelAttribute(GROUPS_FORM_KEY) @Valid GroupsForm form,
                  Model model) throws Exception {

        AccountService as = this.accountManagerService.getAccount(accountId);
        GroupsForm.Action action = form.getAction();

        if(action == Action.ADD){
            String name = form.getGroupName();
            if(!StringUtils.isBlank(name)){
                this.duracloudGroupService.createGroup(name);
                return formatGroupRedirect(accountId, name, "/edit");
            }
        }else {
            String[] groups = form.getGroupNames();
            if(groups != null){
                for (String name : groups) {
                    removeGroup(as, this.duracloudGroupService.getGroup(name));
                }
            }
        }

        addGroupsObjectsToModel(as, model);

        return GROUPS_VIEW_ID;
    }



    @RequestMapping(value = GROUP_PATH, method = RequestMethod.GET)
    public String getGroup(@PathVariable int accountId,
                           @PathVariable String groupName,
                           Model model) throws Exception {

        addUserToModel(model);
        model.addAttribute(GROUPS_FORM_KEY, new GroupsForm());
        AccountService as = getAccountService(accountId);
        List<DuracloudGroup> groups = getGroups(as);
        DuracloudGroup group = getGroup(groupName, groups);
        addGroupToModel(group, model);

        addGroupsObjectsToModel(as, groups, model);
        return GROUP_VIEW_ID;
    }

    private List<DuracloudGroup> getGroups(AccountService as) {
        Set<DuracloudGroup> set = this.duracloudGroupService.getGroups();
        List<DuracloudGroup> list = new LinkedList<DuracloudGroup>();
        if (set != null) {
            list.addAll(set);
        }

        return list;
    }

    @RequestMapping(value = GROUP_EDIT_PATH, method = RequestMethod.GET)
    public String editGroup(@PathVariable int accountId,
                            @PathVariable String groupName,
                            HttpServletRequest request,
                            Model model) throws Exception {

        AccountService as = getAccountService(accountId);
        
        List<DuracloudGroup> groups = getGroups(as);
        addGroupsObjectsToModel(as, groups, model);

        DuracloudGroup group = getGroup(groupName, groups);
        addGroupToModel(group, model);

        model.addAttribute(GROUP_FORM_KEY, new GroupForm());
        Set<DuracloudUser> groupUsers = getUsers(group.getUserIds());
        
        addAvailableUsersToModel(as, groupUsers, model);
        HttpSession session = request.getSession();
        session.removeAttribute(GROUP_USERS_KEY);
        addGroupUsersIfNotAlreadyInSession(group,
                                           model,
                                           session);
        return GROUP_EDIT_VIEW_ID;
    }

    private Set<DuracloudUser> getUsers(Set<Integer> userIds) {
        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        try {

            if (userIds != null) {
                for (Integer id : userIds) {
                    users.add(this.userService.loadDuracloudUserById(id));
                }
            }
            return users;
        } catch (DBNotFoundException e) {
            throw new DuraCloudRuntimeException(e);
        }

    }

    @RequestMapping(value = GROUP_EDIT_PATH, method = RequestMethod.POST)
    public String
        editGroup(@PathVariable int accountId,
                  @PathVariable String groupName,
                  @ModelAttribute(GROUPS_FORM_KEY) @Valid GroupForm form,
                  HttpServletRequest request,
                  Model model) throws Exception {
        
        GroupForm.Action action = form.getAction();
        AccountService as = getAccountService(accountId);
        List<DuracloudGroup> groups = getGroups(as);
        DuracloudGroup group = getGroup(groupName, groups);
        addGroupToModel(group, model);

        HttpSession session = request.getSession();

        List<DuracloudUser> groupUsers =
            addGroupUsersIfNotAlreadyInSession(group, model, session);

        // handle save case
        if (action == GroupForm.Action.SAVE) {
            save(as, group, new HashSet<DuracloudUser>(groupUsers), form);
            session.removeAttribute(GROUP_USERS_KEY);
            
            return formatGroupRedirect(accountId, groupName, null);
         
        }

        Collection<DuracloudUser> availableUsers =
            getAvailableUsers(as, groupUsers);

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
        } else{
            throw new DuraCloudRuntimeException("[" + action + "] not supported.");
        }

        model.addAttribute(GROUP_FORM_KEY, form);
        addAvailableUsersToModel(availableUsers, groupUsers, model);
        addGroupsObjectsToModel(as, groups, model);

        return GROUP_EDIT_VIEW_ID;
    }

    @RequestMapping(value = GROUP_EDIT_PATH, method = RequestMethod.POST)
    public String
        saveGroup(@PathVariable int accountId,
                  @PathVariable String groupName,
                  @ModelAttribute(GROUPS_FORM_KEY) @Valid GroupForm form,
                  HttpServletRequest request,
                  Model model) throws Exception {

        GroupForm.Action a = form.getAction();
        AccountService as = getAccountService(accountId);
        List<DuracloudGroup> groups = getGroups(as);
        DuracloudGroup group = getGroup(groupName, groups);
        addGroupToModel(group, model);

        HttpSession session = request.getSession();

        List<DuracloudUser> groupUsers =
            addGroupUsersIfNotAlreadyInSession(group, model, session);

        // handle save case
        if (a == GroupForm.Action.SAVE) {
            save(as, group, new HashSet<DuracloudUser>(groupUsers), form);
            session.removeAttribute(GROUP_USERS_KEY);
            
            return formatGroupRedirect(accountId, groupName, null);
         
        }

        Collection<DuracloudUser> availableUsers =
            getAvailableUsers(as, groupUsers);

        if (a == GroupForm.Action.ADD) {
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

        } else if (a == GroupForm.Action.REMOVE) {
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
        }

        model.addAttribute(GROUP_FORM_KEY, form);
        addAvailableUsersToModel(availableUsers, groupUsers, model);
        addGroupsObjectsToModel(as, model);

        return GROUP_EDIT_VIEW_ID;
    }

    
    private String formatGroupRedirect(int accountId, String groupName, String suffix) {
        return "redirect:"
            + ACCOUNTS_PATH
            + GROUP_PATH.replace("{accountId}", String.valueOf(accountId))
                        .replace("{groupName}", String.valueOf(groupName))
            + (suffix != null ? suffix : "");
    }

    private void addAvailableUsersToModel(Collection<DuracloudUser> allUsers,
                                          Collection<DuracloudUser> groupUsers,
                                          Model model) {
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

    private List<DuracloudUser>
        addGroupUsersIfNotAlreadyInSession(DuracloudGroup group,
                                           Model model,
                                           HttpSession session) {
        List<DuracloudUser> groupUsers =
            (List<DuracloudUser>) session.getAttribute(GROUP_USERS_KEY);
        if (groupUsers == null) {
            groupUsers = new LinkedList<DuracloudUser>();
            if (group.getUserIds() != null) {
                groupUsers.addAll(getUsers(group.getUserIds()));
            }
            Collections.sort(groupUsers, USERNAME_COMPARATOR);
            session.setAttribute(GROUP_USERS_KEY, groupUsers);
        }

        model.addAttribute(GROUP_USERS_KEY, groupUsers);

        return groupUsers;
    }

    private void save(AccountService as,
                      DuracloudGroup group,
                      Set<DuracloudUser> groupUsers,
                      GroupForm form)
        throws DuracloudGroupNotFoundException,
            DBConcurrentUpdateException {
        duracloudGroupService.updateGroupUsers(group,
                                               groupUsers);
        form.reset();
    }

    private void removeGroup(AccountService as, DuracloudGroup group)
        throws DBConcurrentUpdateException {
        this.duracloudGroupService.deleteGroup(group);
    }

    private AccountService getAccountService(int accountId) throws Exception {
        return this.accountManagerService.getAccount(accountId);
    }

    private void addGroupsObjectsToModel(AccountService as, Model model)
        throws Exception {
        addGroupsObjectsToModel(as, this.getGroups(as), model);
    }

    private void addGroupsObjectsToModel(AccountService as, List<DuracloudGroup> groups, Model model)
        throws Exception {
        addUserToModel(model);
        model.addAttribute("accountId", as.getAccountId());
        model.addAttribute(GROUPS_FORM_KEY, new GroupsForm());
        addGroupsToModel(model, groups);
    }

    
    private void addAvailableUsersToModel(AccountService as,
                                          Collection<DuracloudUser> groupUsers,
                                          Model model) {
        Collection<DuracloudUser> availableUsers =
            getAvailableUsers(as, groupUsers);
        addAvailableUsersToModel(availableUsers, groupUsers, model);
    }

    private Collection<DuracloudUser>
        getAvailableUsers(AccountService as,
                          Collection<DuracloudUser> groupUsers) {
        Set<DuracloudUser> allUsers = as.getUsers();
        LinkedList<DuracloudUser> list = new LinkedList<DuracloudUser>();
        list.addAll(allUsers);
        if (groupUsers != null) {
            list.removeAll(groupUsers);
        }

        Collections.sort(list, USERNAME_COMPARATOR);
        return allUsers;
    }

    private static Comparator<DuracloudUser> USERNAME_COMPARATOR =
        new Comparator<DuracloudUser>() {

            @Override
            public int compare(DuracloudUser o1, DuracloudUser o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        };

    private void addGroupToModel(DuracloudGroup group, Model model) {
        model.addAttribute(GROUP_KEY, group);
        model.addAttribute(GROUP_USERS_KEY, getUsers(group.getUserIds()));
    }

    private DuracloudGroup getGroup(String groupName,
                                    List<DuracloudGroup> groups) {
        for (DuracloudGroup g : groups) {
            if (g.getName().equals(groupName)) {
                return g;
            }
        }
        return null;
    }

    private void addGroupsToModel(Model model, List<DuracloudGroup> groups) {
        model.addAttribute("groups", groups);
    }


    public void setDuracloudGroupService(DuracloudGroupService groupService) {
       this.duracloudGroupService = groupService;
        
    }

}
