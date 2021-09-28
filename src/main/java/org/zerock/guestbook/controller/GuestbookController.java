package org.zerock.guestbook.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.guestbook.dto.PageRequestDTO;
import org.zerock.guestbook.service.GuestbookService;

@Controller
@RequestMapping("/guestbook")
@Log4j2
@RequiredArgsConstructor
public class GuestbookController {

    private final GuestbookService service;

    /*
    @GetMapping({"/","/list"})
    public String list(){
        log.info("list.............");
        return "/guestbook/list";
    }*/

    @GetMapping("/list")
    public void list(@ModelAttribute("pageRequestDTO")PageRequestDTO pageRequestDTO, Model model){

        log.info("list............." + pageRequestDTO);

        model.addAttribute("result", service.getList(pageRequestDTO));
    }
    @GetMapping("/register")
    public void register(){
        log.info("register get...");
    }

}
