package com.kg.library.requestBook;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.kg.library.book.BookDTO;
import com.kg.library.book.BookService;

@RestController
public class requestBookController {

	@Autowired requestBookService service;
	
	@Autowired
    private BookService bookservice;
	
/*
    @GetMapping("bookdata") //DB 자료 가공해서 return으로 데이터를 넘김
    public BookDTO bookdata() {
        BookDTO dataFromDB = bookservice.bookdata();
        return dataFromDB;
    }
*/  
	@RequestMapping("requestlogin")
	public String requestlogin(){
		
		service.requestlogin();
		return "redirect:bookfrom";
	}
	
    @PostMapping("requestbookForm") //return으로 데이터를 넘김
    public ResponseEntity<String> requestbookForm(@RequestBody String requestBody) {//외부 서버 데이터 받기
        // 요청 바디를 받아서 처리하는 로직을 작성
        System.out.println("Received request body: " + requestBody);

        // 응답 생성 (예: "Request received successfully"라는 메시지를 응답)
        String responseBody = "Request received successfully";
        return ResponseEntity.ok(responseBody);
    }
    
    @RequestMapping(value = "/redirectToLogin", method = RequestMethod.GET) //페이지 연결 //redirect는 get으로 요청.
    public ModelAndView redirectToLogin() {
        return new ModelAndView("redirect:http://localhost:8085/login"); // 외부 서버의 jsp로 연결하고 싶으면 redirect 써야 됨.
    }
    
}
