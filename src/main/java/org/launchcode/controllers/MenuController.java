package org.launchcode.controllers;

import org.launchcode.models.AddMenuItemForm;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by nico on 6/29/2017.
 */

@Controller
@RequestMapping(value="menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;


    @RequestMapping(value="")
    public String index(Model model){
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Cheese Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model, Menu menu) {
        Menu newMenu;

        model.addAttribute("title", " Add Menu");
        model.addAttribute("menu", new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors){

        if (errors.hasErrors()) {
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int id, Model model) {
        Menu menu =menuDao.findOne(id);
        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);
        return "menu/view";

    }
    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);

        AddMenuItemForm itemForm = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("title", "Add item to menu: " + menu.getName());
        model.addAttribute("form", itemForm);

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm itemForm,
                          Errors errors, @PathVariable int menuId) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Item");
            return "menu/add-item/" + menuId;
        }

        Menu menu = menuDao.findOne(itemForm.getMenuId());
        Cheese cheese = cheeseDao.findOne(itemForm.getCheeseId());

        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:../view/" + menu.getId();
    }
}
