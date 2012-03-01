/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import javax.validation.Valid;

import org.duracloud.common.error.DuraCloudRuntimeException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 27, 2012
 */
public abstract class AbstractCrudController<T> extends AbstractController{

    protected abstract String getBaseViewId();

    private Class<T> clazz;
    
    public AbstractCrudController(Class<T> clazz){
        this.clazz = clazz;
    }
    
    @ModelAttribute("form")
    public T form() {
        try {
            return this.clazz.newInstance();
        } catch (Exception e) {
            throw new DuraCloudRuntimeException(e.getMessage(), e);
        }
    }

    
    protected String getNewViewId() {
        return getBaseViewId() + "/new";
    }

    protected String getEditViewId() {
        return getBaseViewId() + "/edit";
    }
    
    @RequestMapping("")
    public abstract ModelAndView get();
    
    @RequestMapping(NEW_MAPPING)
    public ModelAndView getNew() {
        return new ModelAndView(getNewViewId());
    }
    
    @RequestMapping(value = NEW_MAPPING, method = RequestMethod.POST)
    public ModelAndView create(@ModelAttribute("form") @Valid T form,
                               BindingResult bindingResult, 
                               Model model,
                               RedirectAttributes redirectAttributes) {

        boolean hasErrors = bindingResult.hasErrors();

        if(hasErrors){
            return new ModelAndView(getNewViewId());
        }
        
        create(form);
        log.info("created server image: {}" + form);
        setSuccessFeedback(createSuccessMessage(),
                        redirectAttributes);

        return new ModelAndView(new RedirectView(getBaseViewId(), true));
    }
    
    protected String createSuccessMessage(){
        return "Successfully created!";
    }

    protected abstract void create(T form);

    @RequestMapping(value = BY_ID_EDIT_MAPPING, method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable int id){
        
        T form = getFormById(id);
        log.debug("retrieved form: id={} -> form=", id, form);

        return new ModelAndView(getEditViewId(), "form", form);
    }
    
    protected T getFormById(int id){
        Object entity = getEntity(id);
        return loadForm(entity);
    }

    protected abstract Object getEntity(int id);

    protected abstract T loadForm(Object entity);

    @RequestMapping(value = BY_ID_EDIT_MAPPING, method = RequestMethod.POST)
    public ModelAndView
        update(@PathVariable int id, @ModelAttribute("form") @Valid T form,
                          BindingResult bindingResult, Model model,RedirectAttributes redirectAttributes) {
        boolean hasErrors = bindingResult.hasErrors();
        if(hasErrors){
            return new ModelAndView(getEditViewId());
        }
        
        update(id, form);

        log.info("updated form: id={} -> form=", id, form);

        setSuccessFeedback(updateSuccessMessage(),
                        redirectAttributes);
        return new ModelAndView(new RedirectView(getBaseViewId(), true));
    }

    protected String updateSuccessMessage() {
        return "Update successful!";
    }

    protected abstract void update(int id, T form);

    @RequestMapping(value = BY_ID_DELETE_MAPPING, method = RequestMethod.POST)
    public ModelAndView
        delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        
        delete(id);
        setSuccessFeedback(deleteSuccessMessage(),
                        redirectAttributes);
        log.info("deleted object: id={}", id);
        
        return new ModelAndView(new RedirectView(getBaseViewId(), true));
    }
    
    protected String deleteSuccessMessage() {
        return "Successfully deleted!";
    }

    abstract protected void delete(int id);

}
