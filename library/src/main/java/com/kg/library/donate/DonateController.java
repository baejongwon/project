package com.kg.library.donate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class DonateController {
@Autowired private DonateService service;
@Autowired private HttpSession session;	
	
	// 도서 기증 목록보기--------------------------------
	@RequestMapping("/donateForm")
	public String donateForm(String search, Model model, 
			@RequestParam(value="currentPage", required = false)String cp) {
		String select = "select";
		if(search == null || search.trim().isEmpty()) {
			search = "id";
			select = "all";
		}
		
		service.donateForm(cp, model, search, select);
		model.addAttribute("menu", "donateForm");
		return "/donate/donateForm";
	}
	
	// 도서 기증--------------------------------
	@GetMapping("/donateWrite")
	public String donateWrite(Model model) {	
		String msg = "";
		String sessionId = (String)session.getAttribute("id");
		if(sessionId == null) {
			msg="로그인 후 가능합니다.";
			return "redirect:login";
		}
		model.addAttribute("menu", "donateWrite");
		return "donate/donateWrite";
	}
	
	// 도서 기증 신청하기--------------------------------
	@PostMapping("/donateWriteProc")
	public String donateWriteProc(DonateDTO donate, Model model) {
		String msg = "";
		if(donate.getContent()==""||donate.getContent().trim().isEmpty()) {
			msg="내용을 입력하세요.";
			model.addAttribute("msg",msg);
			model.addAttribute("donate", donate);
			return "donate/donateWrite";
		}
		
		int res = service.donateWriteProc(donate);
		return "redirect:donateForm";
	}
	// 도서 기증 상세보기 --------------------------------
	@RequestMapping("donateContent")
	public String donateContent(String no, Model model, RedirectAttributes ra) {
		String msg = "";
		DonateDTO board = service.donateContent(no);
		String sessionId = (String)session.getAttribute("id");
		if(board == null) {
			return "redirect:donateForm";
		}
		if(sessionId == null) {
			return "redirect:login";
		}
		if(!sessionId.equals(board.getId())) {
			msg="작성자만 확인가능합니다.";
			ra.addFlashAttribute("msg", msg);
			return "redirect:donateForm";
		}
		
		model.addAttribute("menu", "donateForm");
		model.addAttribute("board", board);
		return "donate/donateContent";
	}
	// 도서 기증 안내서   --------------------------------
	@RequestMapping("donateguide")
	public String donateguide(Model model) {
		model.addAttribute("menu", "donateguide");
		return "donate/donateguide";
	}
	
	// 도서 기증 사이드바 --------------------------------
	@RequestMapping("donateheader")
	public String donateheader() {
		return "donate/donateheader";
	}
	
	@RequestMapping("donatefooter")
	public String donatefooter() {
		return "donate/donatefooter";
	}
}