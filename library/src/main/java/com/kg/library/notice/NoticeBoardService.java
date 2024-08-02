package com.kg.library.notice;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.kg.library.PageService;

import jakarta.servlet.http.HttpSession;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class NoticeBoardService {
	@Autowired
	private NoticeBoardMapper mapper;
	@Autowired
	private HttpSession session;
	//private String filePath = "C:\\Users\\user1\\git\\kglibrary\\library\\src\\main\\resources\\static\\img\\";
	
	private String bucketName = "kglibrary2"; // S3 버킷 이름
    private String s3FilePath = "static/img/"; // S3에 업로드할 경로

	 @Autowired
	 private S3Client s3Client; // AWS S3 클라이언트 주입

//	 @Value("${aws.s3.bucketName}")
//	 private String s3BucketName;
	 

	public void noticeBoard(String search_select, String search, String cp, Model model) {
		int currentPage = 1;// 현재 페이지

		try {
			currentPage = Integer.parseInt(cp);
		} catch (Exception e) {
			currentPage = 1;
		}
		
		if (search_select == null) {
			search_select = "";
		}
		
		int pageBlock = 10;// 한 페이지에 보일 데이터의 수.
		int end = pageBlock * currentPage;// 테이블에서 가져올 마지막 행 번호.
		int begin = end - pageBlock + 1; // 테이블에서 가져올 시작 행 번호.
		
		ArrayList<NoticeBoardDTO> boards = mapper.noticeBoard(begin,end,search_select,search);
		
		
		int totalCount = mapper.totalCount(search_select, search); // 테이블의 행의 갯수 를 구해 오기위함
		if (totalCount == 0) {
			return;
		}
		String url = "noticeBoard?search_select=" + search_select + "&search=" + search + "&currentPage=";
		String result = PageService.printPage(url, totalCount, pageBlock, currentPage);
		model.addAttribute("count", totalCount);
		model.addAttribute("boards", boards);
		model.addAttribute("result", result);
		model.addAttribute("search_select", search_select);
		model.addAttribute("search", search);
		
	}

	public String noticeboard_writeProc(MultipartHttpServletRequest multi) {
		String sessionId = (String) session.getAttribute("id");
		if (sessionId == null)
			return "redirect:login";
	
	
	String title = multi.getParameter("title");
	String content = multi.getParameter("content");
	content = content.replace("\r\n","<br>");
	if (title == null || title.trim().isEmpty()) {
		return "redirect:noticeBoard";
	}

	NoticeBoardDTO board = new NoticeBoardDTO();
	board.setTitle(title);
	board.setContent(content);
	board.setId(sessionId);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	board.setWriteDate(sdf.format(new Date()));
	board.setImage("");

	MultipartFile file = multi.getFile("upfile");

	if (file.getSize() != 0) {
		// 파일이름
		sdf = new SimpleDateFormat("yyyyMMddHHmmss-");
		String fileTime = sdf.format(new Date());
		String fileName = file.getOriginalFilename();

		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		System.out.println("BoardService-boardWriteProc-suffix : " + suffix);
		if (suffix.equalsIgnoreCase("jpg") == false && suffix.equalsIgnoreCase("png") == false
				&& suffix.equalsIgnoreCase("jpeg") == false) {
			System.out.print("파일명이 다릅니다.");
			return "redirect:noticeBoard";
		}

		// 파일의 저장 경로
//		String fileSaveDirectory = sessionId;
		
		String fileSaveDirectory = "kglibrary/" + sessionId;  // S3 버킷 경로
		String fullPath = fileSaveDirectory + "/" + fileTime + fileName;
		 // 파일명만 추출
	    String uploadedFileName = extractFileName(fullPath);
		// AWS S3에 업로드할 경로 설정
        String s3Key = s3FilePath + sessionId + "/" + fileTime + fileName;
		

        // 파일 업로드를 위한 코드
        try (InputStream fileInputStream = file.getInputStream()) {
            // S3에 업로드할 파일의 크기
            long fileSize = file.getSize();

            // S3에 업로드할 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            // S3에 파일 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(fileInputStream, fileSize));
            board.setImage(s3Key);
        }catch (IOException e) {
            e.printStackTrace();
            board.setImage("");
        }
    }
	
	mapper.noticeboard_writeProc(board);
	return "redirect:noticeBoard";
	}
	// 파일 경로에서 파일명만 추출하는 메서드
	private String extractFileName(String filePath) {
	    Path path = Paths.get(filePath);
	    if (path.getNameCount() > 0) {
	        return path.getFileName().toString();
	    } else {
	        return filePath;
	    }
	}
//	public String noticeboard_writeProc(MultipartHttpServletRequest multi) {
//		String sessionId = (String) session.getAttribute("id");
//		if (sessionId == null)
//			return "redirect:login";
//	
//	
//	String title = multi.getParameter("title");
//	String content = multi.getParameter("content");
//	content = content.replace("\r\n","<br>");
//	if (title == null || title.trim().isEmpty()) {
//		return "redirect:noticeBoard";
//	}
//
//	NoticeBoardDTO board = new NoticeBoardDTO();
//	board.setTitle(title);
//	board.setContent(content);
//	board.setId(sessionId);
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//	board.setWriteDate(sdf.format(new Date()));
//	board.setImage("");
//
//	MultipartFile file = multi.getFile("upfile");
//
//	if (file.getSize() != 0) {
//		// 파일이름
//		sdf = new SimpleDateFormat("yyyyMMddHHmmss-");
//		String fileTime = sdf.format(new Date());
//		String fileName = file.getOriginalFilename();
//
//		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
//		System.out.println("BoardService-boardWriteProc-suffix : " + suffix);
//		if (suffix.equalsIgnoreCase("jpg") == false && suffix.equalsIgnoreCase("png") == false
//				&& suffix.equalsIgnoreCase("jpeg") == false) {
//			System.out.print("파일명이 다릅니다.");
//			return "redirect:noticeBoard";
//		}
//
//		// 파일의 저장 경로
//		String fileSaveDirectory = filePath + sessionId;
//		File f = new File(fileSaveDirectory);// 파일 객체 생성 으로 sessionId위치에 폴더가 있는지 없는지 확인 하기
//		if (f.exists() == false) {
//			// 저장경로가 없을 경우
//			// f.mkdir();
//			f.mkdirs();// 자동으로 만들어 줄 수 있게 한다.
//		}
//		// \\(역슬러시)를 /(슬러시로)
//		String fullPath = fileSaveDirectory + "/" + fileTime + fileName;
//		board.setImage(fullPath);
//		f = new File(fullPath); // 파일 객체에 저장경로에 대한 정보 넣어주기.
//		try {
//			file.transferTo(f);// 파일 저장.
//		} catch (IOException e) {
//			e.printStackTrace();
//			board.setImage("");// 예외가 발생하면 빈문자열을 넣어줄때.
//		}
//	}
//	
//	mapper.noticeboard_writeProc(board);
//	return "redirect:noticeBoard";
//	}


	public NoticeBoardDTO noticeboard_Content(String no, Model model) {
		int n = 1;
		try {
			n = Integer.parseInt(no);
		}catch (Exception e) {
			return null;
		}
		
		NoticeBoardDTO board = mapper.noticeboard_Content(n);
		
		if(board != null) {
			mapper.incrementHits(n);
			board.setHits(board.getHits()+1);
			if(board.getImage()!=null) {
			//s3 url 주소 얻기
			String imageUrl = getS3ObjectUri(board.getImage());
//			String imageUrl = "https://kglibrary.s3.ap-northeast-2.amazonaws.com/"+board.getImage();
			System.out.println("이미지 주소: " + imageUrl);
			model.addAttribute("imageUrl",imageUrl);
					
			}
		}
		
		return board;
	}
	
	private String getS3ObjectUri(String s3Key) {
		 if (s3Key == null || s3Key.trim().isEmpty()) {
		        // s3Key가 null 또는 빈 문자열인 경우에 대한 예외 처리
		        return ""; // 또는 다른 기본값 설정
		    }
		 
		GetUrlRequest  getUrlRequest  = GetUrlRequest .builder()
	            .bucket(bucketName)
	            .key(s3Key)
	            .build();

	    return s3Client.utilities().getUrl(getUrlRequest ).toExternalForm();
	}
	
//	public NoticeBoardDTO noticeboard_Content(String no) {
//		int n = 1;
//		try {
//			n = Integer.parseInt(no);
//		}catch (Exception e) {
//			return null;
//		}
//		
//		NoticeBoardDTO board = mapper.noticeboard_Content(n);
//		
//		if(board != null) {
//			mapper.incrementHits(n);
//			board.setHits(board.getHits()+1);
//			if(board.getImage()!=null) {
//			String[] names=board.getImage().split("/");
////			for(String name : names) {
////			System.out.println("컨텐츠 name: " +name);
////			}
//			String[] fileNames = names[1].split("-",2);
////			for(String fileName : fileNames) {
////				System.out.println("fileName: " +fileName);
////				}
//			board.setImage(names[1]);
//			}
//		}
//		
//		return board;
//	}


	public String ContentDeleteProc3(String no) {
		int n = 0;
		try {
			n = Integer.parseInt(no);
		} catch (Exception e) {
			return "게시글 번호에 문제가 발생했습니다. 다시 시도하세요.";
		}
		NoticeBoardDTO board = mapper.noticeboard_Content(n);
		if(board == null)
			return "게시글 번호에 문제가 발생했습니다. 다시 시도하세요.";
		
		// 로그인한 아이디와 작성자 아이디가 같은지 확인
		String sessionId = (String)session.getAttribute("id");
		if(board.getId().equals(sessionId) == false)
			return "작성자만 삭제 할 수 있습니다.";
				
		String fullPath = board.getImage();
		if (fullPath != null) {
	        // S3에서 객체 삭제
	        String s3Key = s3FilePath + sessionId + "/" + extractFileName(fullPath);
	        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
	                .bucket(bucketName)
	                .key(s3Key)
	                .build();
	        s3Client.deleteObject(deleteObjectRequest);
	    }

		// 테이블에서 게시글번호와 일치하는 행(row)삭제
		mapper.ContentDeleteProc3(n);
		return "게시글 삭제 완료";
	}
//	public String ContentDeleteProc3(String no) {
//		int n = 0;
//		try {
//			n = Integer.parseInt(no);
//		} catch (Exception e) {
//			return "게시글 번호에 문제가 발생했습니다. 다시 시도하세요.";
//		}
//		NoticeBoardDTO board = mapper.noticeboard_Content(n);
//		if(board == null)
//			return "게시글 번호에 문제가 발생했습니다. 다시 시도하세요.";
//		
//		// 로그인한 아이디와 작성자 아이디가 같은지 확인
//		String sessionId = (String)session.getAttribute("id");
//		if(board.getId().equals(sessionId) == false)
//			return "작성자만 삭제 할 수 있습니다.";
//		
//		String fullPath = board.getImage();
//		if(fullPath != null) { // 테이블에 파일의 경로와 이름이 있다면
//			File f = new File(fullPath);
//			if(f.exists() == true) // 파일 저장소에 파일이 존재한다면
//				f.delete();
//		}
//		
//		// 테이블에서 게시글번호와 일치하는 행(row)삭제
//		mapper.ContentDeleteProc3(n);
//		return "게시글 삭제 완료";
//	}


    public String noticeboard_Modify(String no, Model model) {
		int n = 0;
		try {
			n = Integer.parseInt(no);
		} catch (Exception e) {
			return "redirect:noticeBoard";
		}
		NoticeBoardDTO board = mapper.noticeboard_Content(n);
		String fileName = board.getImage() != null ? extractFileName(board.getImage()) : "";
		String content = board.getContent().replaceAll("<br>", "\r\n");
		board.setContent(content);
		System.out.println("컨텐츠 = " + board.getContent());
		if(board == null)
			return "redirect:noticeBoard";
		
		model.addAttribute("board",board);
		model.addAttribute("fileName", fileName);
		return "notice/noticeboard_Modify";
		
	}
    
//	public String noticeboard_Modify(String no, Model model) {
//		int n = 0;
//		try {
//			n = Integer.parseInt(no);
//		} catch (Exception e) {
//			return "redirect:noticeBoard";
//		}
//		NoticeBoardDTO board = mapper.noticeboard_Content(n);
//		String content = board.getContent().replaceAll("<br>", "\r\n");
//		board.setContent(content);
//		System.out.println("컨텐츠 = " + board.getContent());
//		if(board == null)
//			return "redirect:noticeBoard";
//		
////		if(board.getImage() != null) {
////			String[] names = board.getImage().split("\\\\");
//////			for(String name : names) {
//////				System.out.println("게시글 수정 name: " +name);
//////				}
////			String[] fileNames = names[11].split("-", 2);
//////			for(String fileName : fileNames) {
//////				System.out.println("게시글 수정 fileName: " +fileName);
//////				}
////			board.setImage(fileNames[1]);
////		}
//
//		model.addAttribute("board",board);
//		return "notice/noticeboard_Modify";
//		
//	}

    public String noticeboard_ModifyProc(NoticeBoardDTO board, MultipartHttpServletRequest multi) {
		NoticeBoardDTO check = mapper.noticeboard_Content(board.getNo());
		if(check == null)
			return "게시글 번호에 문제가 발생했습니다. 다시 시도하세요.";
		String sessionId = (String)session.getAttribute("id");	
		
		if(check.getId().equals(sessionId) == false)
			return "작성자만 수정 할 수 있습니다.";
		if(board.getTitle() == null || board.getTitle().trim().isEmpty()) {
			return "제목을 입력하세요.";
		}

		String content = multi.getParameter("content");
		content = content.replace("\r\n","<br>");
		board.setContent(content);
		board.setImage(check.getImage());
		
		System.out.println("컨텐츠2 = " + board.getContent());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		board.setWriteDate(sdf.format(new Date()));

		MultipartFile file = multi.getFile("upfile");

		if (file.getSize() != 0) {
			// 파일이름
			sdf = new SimpleDateFormat("yyyyMMddHHmmss-");
			String fileTime = sdf.format(new Date());
			String fileName = file.getOriginalFilename();

			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			System.out.println("BoardService-boardWriteProc-suffix : " + suffix);
			if (suffix.equalsIgnoreCase("jpg") == false && suffix.equalsIgnoreCase("png") == false
					&& suffix.equalsIgnoreCase("jpeg") == false) {
				System.out.print("파일명이 다릅니다.");
				return "redirect:animal_info";
			}

			// 파일의 저장 경로
			String fileSaveDirectory = "kglibrary/" + sessionId;  // S3 버킷 경로
			String fullPath = fileSaveDirectory + "/" + fileTime + fileName;
			// AWS S3에 업로드할 경로 설정
		    String s3Key = s3FilePath + sessionId + "/" + fileTime + fileName;
		    
		    // 파일 업로드를 위한 코드
	        try (InputStream fileInputStream = file.getInputStream()) {
	            // S3에 업로드할 파일의 크기
	            long fileSize = file.getSize();

	            // S3에 업로드할 객체 생성
	            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
	                    .bucket(bucketName)
	                    .key(s3Key)
	                    .build();

	            // S3에 파일 업로드
	            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(fileInputStream, fileSize));
	            board.setImage(s3Key);
	        }catch (IOException e) {
	            e.printStackTrace();
	            board.setImage("");
	        }
		}
		
		if(board.getImage()==null) {
			board.setImage("");
		}
		
		int result = mapper.noticeboard_ModifyProc(board);
		if(result == 0)
			return "게시글 수정에 실패했습니다. 다시 시도하세요.";
		
		return "게시글 수정 성공";
	}
    
//	public String noticeboard_ModifyProc(NoticeBoardDTO board, MultipartHttpServletRequest multi) {
//		NoticeBoardDTO check = mapper.noticeboard_Content(board.getNo());
//		if(check == null)
//			return "게시글 번호에 문제가 발생했습니다. 다시 시도하세요.";
//		String sessionId = (String)session.getAttribute("id");	
//		
//		if(check.getId().equals(sessionId) == false)
//			return "작성자만 수정 할 수 있습니다.";
//		if(board.getTitle() == null || board.getTitle().trim().isEmpty()) {
//			return "제목을 입력하세요.";
//		}
//
//		String content = multi.getParameter("content");
//		content = content.replace("\r\n","<br>");
//		board.setContent(content);
//		board.setImage(check.getImage());
//		
//		System.out.println("컨텐츠2 = " + board.getContent());
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		board.setWriteDate(sdf.format(new Date()));
//
//		MultipartFile file = multi.getFile("upfile");
//
//		if (file.getSize() != 0) {
//			// 파일이름
//			sdf = new SimpleDateFormat("yyyyMMddHHmmss-");
//			String fileTime = sdf.format(new Date());
//			String fileName = file.getOriginalFilename();
//
//			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
//			System.out.println("BoardService-boardWriteProc-suffix : " + suffix);
//			if (suffix.equalsIgnoreCase("jpg") == false && suffix.equalsIgnoreCase("png") == false
//					&& suffix.equalsIgnoreCase("jpeg") == false) {
//				System.out.print("파일명이 다릅니다.");
//				return "redirect:animal_info";
//			}
//
//			// 파일의 저장 경로
//			String fileSaveDirectory = filePath + sessionId;
//			File f = new File(fileSaveDirectory);// 파일 객체 생성 으로 sessionId위치에 폴더가 있는지 없는지 확인 하기
//			if (f.exists() == false) {
//				// 저장경로가 없을 경우
//				// f.mkdir();
//				f.mkdirs();// 자동으로 만들어 줄 수 있게 한다.
//			}
//			// \\(역슬러시)를 /(슬러시로)
//			String fullPath = fileSaveDirectory + "/" + fileTime + fileName;
//			board.setImage(fullPath);
//			f = new File(fullPath); // 파일 객체에 저장경로에 대한 정보 넣어주기.
//			try {
//				file.transferTo(f);// 파일 저장.
//			} catch (IOException e) {
//				e.printStackTrace();
//				board.setImage("");// 예외가 발생하면 빈문자열을 넣어줄때.
//			}
//		}
//		
//		if(board.getImage()==null) {
//			board.setImage("");
//		}
//		int result = mapper.noticeboard_ModifyProc(board);
//		if(result == 0)
//			return "게시글 수정에 실패했습니다. 다시 시도하세요.";
//		
//		return "게시글 수정 성공";
//	}


	public void main_board(Model model) {
	
	ArrayList<NoticeBoardDTO> boards = mapper.main_board();
//	for (NoticeBoardDTO board : boards) {
//	    System.out.println(board.getTitle());
//	    System.out.println(board.getNo());
//	    System.out.println(board.getWriteDate());
//		}
	model.addAttribute("boards", boards);
	}	

	}
	
