package org.duraspace.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.duraspace.domain.Account;
import org.duraspace.domain.Space;
import org.duraspace.util.SpaceUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class SpacesController extends AbstractCommandController {

    protected final Logger log = Logger.getLogger(getClass());

	public SpacesController()
	{
		setCommandClass(Account.class);
		setCommandName("account");
	}

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Account cmdAccount = (Account) command;
        String accountId = cmdAccount.getAccountId();

        if(accountId == null || accountId.equals("")) {
            throw new Exception("Account must be provided in order to view spaces.");
        }

        List<Space> spaces = SpaceUtil.getSpacesList(accountId);

        ModelAndView mav = new ModelAndView("spaces");
        mav.addObject("accountId", accountId);
        mav.addObject("spaces", spaces);

        return mav;
    }

}