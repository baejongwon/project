package com.kg.library.requestBook;


import org.springframework.http.*;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpSession;

@Service

public class requestBookService {

	private HttpSession session;
	
	public void requestlogin() {
				
			String loginPageUrl = "localhost:8085/login";

			RestTemplate restTemplate = new RestTemplate();
			
			String loginRQ = "loginRQ";
			
			loginPageUrl = UriComponentsBuilder.fromUriString(loginPageUrl)
	                .queryParam("loginRQ", loginRQ)
	                .build().toUriString();

	    }

	public void requestId() {  //ID 보낼 때
		
        // RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();
        
        // 전송할 ID 값        
        String userId = "id 전송 테스트"; // (String)session.getAttribute("id");

        // 서버에게 ID를 전송하는 URL
        String apiUrl = "http://localhost:80/requestbookForm";

        // POST 요청을 위한 헤더 및 본문 설정 (어떤 데이터 형식으로 보낼지 결정하는 부분) / 손잡이(재료)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); //Json 데이터, @RequestBody으로 받을 수 있음.
        //  headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); //HTML 폼 데이터, @RequestParam, @ModelAttribute으로 받을 수 있음
        //  headers.setContentType(MediaType.MULTIPART_FORM_DATA); //웹 폼 데이터, enctype="multipart/form-data로 받음.
        
        // JSON 데이터 설정 (보낼 값 형식 key,value) / 검날(재료)
        String jsonBody = "{\"userId\":\"" + userId + "\"}";
      
        // 요청 엔터티 생성 (header,body 합쳐서 보낼 데이터 덩어리 만드는 것.) / 검(완제품)
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // POST 요청 보내기 ()
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
        //응답 데이터를 어떻게 처리 할지 ResponseEntity에 정의 / apiUrl에, 정보(완제품)을, string형식으로(json이라면 이 부분을 커스텀 클래스를 만들어서 그 형식으로 보낼 수 있음), HTTP POST요청을 보낸다.

        // 응답 확인
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            System.out.println("Response body: " + responseBody);
        } else {
            System.out.println("Request failed. Status code: " + responseEntity.getStatusCode());
    }

}
	
	/*
	public void requestDataFromServer() { //DB json에서 받아오기 보내는 데이터가 없을 때 / DB 자료 달라고 요청
        // RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        // 서버에게 데이터를 요청하는 URL
        String apiUrl = "http://localhost:80/bookdata";

        // GET 요청 보내기
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

        // 응답 확인(응답으로 데이터 받음, 가공은 해야 됨)
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            System.out.println("Response body: 여기 맞음? " + responseBody);
            //가공 영역
            
        } else {
            System.out.println("Request failed. Status code: " + responseEntity.getStatusCode());
        }
    }
	
	*/
	
	
	
	
	
	
}

