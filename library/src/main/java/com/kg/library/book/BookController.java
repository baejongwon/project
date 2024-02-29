package com.kg.library.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class BookController {

	@Autowired
	private BookService service;
	@Autowired 
	private HttpSession session;
	
	@RequestMapping("bookForm") // 도서 검색 url
	public String bookForm(String search,Model model,
			@RequestParam(value="currentPage", required = false)String cp, String select) {
			//검색 시 select 값으로 검색(search) 확인.
			if(search == null || search.trim().isEmpty()) {
				search = ""; select = "title"; 
			}

			model.addAttribute("menu", "board");
			service.bookForm(cp, model, search, select); //DB 검색 및 정렬

		return "book/bookForm";
	}
	
	@RequestMapping("bookRegist") //도서 등록 url
	public String bookRegist(Model model) {
		String sessionId = (String) session.getAttribute("id");
		if (sessionId==null || sessionId.trim().isEmpty()) {
			System.out.println(sessionId);
			return "redirect:bookForm";
		}
		if(!sessionId.equals("admin")) {
			return "redirect:bookForm";
		}
	//관리자가 아니면 등록 불가, 회원이 url을 직접적으로 치고 들어올 경우 반환하기 위하여 설정. 회원가입 확인 되면 주석 풀것
		model.addAttribute("menu", "board");
		return "book/bookRegist";
	}
	
	@RequestMapping("bookRegistProc")
	public String bookRegistProc(MultipartHttpServletRequest multi, RedirectAttributes ra) {

		String path = service.bookRegistProc(multi);
		return path;
	}
	
	@RequestMapping("bookContent")
	public String bookContent(String no,Model model) {
		
		BookDTO board = service.bookContent(no, model);
		if(board == null) {
			return "redirect:bookForm";
		}

		model.addAttribute("menu", "board");
		model.addAttribute("board", board);
		return "book/bookContent";
	}
	
	@RequestMapping("rentalProc")
	public String rentalProc(String no) {
			
		String sessionId = (String) session.getAttribute("id");
		if (sessionId == null || sessionId.trim().isEmpty()) 
		return "redirect:bookContent";
		
		service.rentalProc(no, sessionId);
		return "redirect:bookContent";	
	}
	
	@RequestMapping("returnProc")
	public String returnProc(String no) {
			
		
		String sessionId = (String) session.getAttribute("id");
		if (sessionId == null || sessionId.trim().isEmpty()) 
		return "redirect:bookContent";
		
		service.returnProc(no);
		return "redirect:bookContent";	
	}
	
	@RequestMapping("bookDeleteProc")
	public String bookDeleteProc(String no) {
		String sessionId = (String) session.getAttribute("id");
		if (sessionId == null || !sessionId.equals("admin") || sessionId.trim().isEmpty()) {
			return "redirect:bookForm";
		}
		
		service.bookDeleteProc(no);
		return "redirect:bookForm";	
	}

	@RequestMapping("apiBookRegist") //도서 등록 url
	public String apiBookRegist(Model model) {
		
		String sessionId = (String)session.getAttribute("id");
		if (sessionId == null || !sessionId.equals("admin") || sessionId.trim().isEmpty()) {
			System.out.println(sessionId);
			return "redirect:login";
		}
	//관리자가 아니면 등록 불가, 회원이 url을 직접적으로 치고 들어올 경우 반환하기 위하여 설정. 회원가입 확인 되면 주석 풀것
		model.addAttribute("menu", "board3");
		return "book/apiBookRegist";
	}
	
	@RequestMapping("apiRegistProc") //도서 등록
	public String apiRegistProc(String pageNum, String select, String search, Model model) {

		if(search == null || search.trim().isEmpty()) {
			return "redirect:apiBookRegist";
		}
		if(select == null || select.trim().isEmpty()) {
			return "redirect:apiBookRegist";
		}

		service.apiRegistProc(pageNum, select, search, model);
		System.out.println("등록");

		return "book/apiAlert";
	}
	
	//로그인 해야 할 때 요청 보내는 Mapping 및 sessionID 받는 값 필요.
	@RequestMapping("requestLogin")
	public void requestLogin() {
		
		
	}
	
	
	
	
	//공지사이드바 템플릿
	@RequestMapping("bookheader")
	public String bookheader() {
		return "book/bookheader";
	}
	
	@RequestMapping("bookfooter")
	public String bookfooter() {
		return "book/bookfooter";
	}
}
