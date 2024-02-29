package com.kg.library.Introduction;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.kg.library.book.BookDTO;
import com.kg.library.book.IBookMapper;

@Service
public class Introduction_Service {
	
	@Autowired
	private IBookMapper mapper;
	 @Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> dataStatus(BookDTO board) {
		
		String query = "SELECT category, COUNT(*) as bookCount, SUM(COUNT(*)) OVER() as totalBookCount FROM book GROUP BY category";
		List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
		
		for (Map<String, Object> row : result) {
			System.out.println("Category: " + row.get("category"));
	        System.out.println("Book Count: " + row.get("bookCount"));
	        System.out.println("Total Book Count: " + row.get("totalBookCount"));
			}	
		 return result;
	}
	}
	
