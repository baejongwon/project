package com.kg.library.reservation;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReservationController {
	@Autowired
	private ReservationService service;
	@Autowired
	private HttpSession session;

	@GetMapping("reservation")
	public String newindex(Model model) {
		model.addAttribute("menu", "reservation");
		return "/reservation/new_reservation";
	}

	@ResponseBody
	@PostMapping(value = "day", produces = "application/json; charset=utf-8")
	public String ex1Post(@RequestBody AjaxDTO allData) {
		String result = "";
		if (allData.getType().equals("date")) {
			result = "<div class=\"date_month\">\r\n";
			int year = Integer.parseInt(allData.getYear());
			int month = Integer.parseInt(allData.getMonth());
			if (month == 1) {
				result += "<a href=\"#none\" class=\"month_prev\" onclick=\"get_cal('" + (year - 1) + "','" + 12
						+ "')\">\r\n";
			} else {
				result += "<a href=\"#none\" class=\"month_prev\" onclick=\"get_cal('" + year + "','" + (month - 1)
						+ "')\">\r\n";
			}
			result += "<i class=\"fas fa-chevron-circle-left\"></i></a>\r\n" + "<p>\r\n" + "<span class=\"c_year\">"
					+ year + "년</span><span class=\"c_month\">" + month + "월</span>\r\n" + "</p>\r\n";
			if (month == 12) {
				result += "<a href=\"#none\" class=\"month_next\" onclick=\"get_cal('" + (year + 1) + "','" + 1
						+ "')\">\r\n";
			} else {
				result += "<a href=\"#none\" class=\"month_next\" onclick=\"get_cal('" + year + "','" + (month + 1)
						+ "')\">\r\n";
			}
			result += "<i class=\"fas fa-chevron-circle-right\"></i></a>\r\n" + "</div>\r\n"
					+ "<div class=\"select_list\">\r\n" + "<ul class=\"step_date_ul\">\r\n";
			LocalDate date = LocalDate.of(year, month, 1);
			LocalDate now = LocalDate.now();
			for (int i = 1; i <= date.lengthOfMonth(); i++) {
				date = LocalDate.of(year, month, i);
				String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
				if(date.isAfter(now)) {
					result+="<li><a href=\"#none\" class=\"\" rel=\""+i+"\">"+i+"("+day+") </a></li>\r\n";;
				}else {
					result+="<li><a href=\"#none\" class=\" none xx \" rel=\""+i+"\">"+i+"("+day+") - 대여불가</a></li>\r\n";
				}
			}
			result += "</ul>\r\n" + "</div>";
		} else {
			for (int i = 9; i < 18; i++) {
				result += "<li><a href=\"#none\" rel=" + i + ">" + i + ":00-" + (i + 1) + ":00</a></li>\r\n";
			}
		}
		return result;
	}

	@PostMapping("new_result")
	public String new_result(Model model, String room, String year_t, String month_t, String day_t, String time_t) {
		ReservationDTO dto = new ReservationDTO();
		String sessionId = (String) session.getAttribute("id");
		if (sessionId == null)
			return "redirect:login";
		dto.setMember(sessionId);
		dto.setReservation_date(year_t + "-" + month_t + "-" + day_t);
		dto.setRoom_num(room);
		String[] time = time_t.split("##");
		dto.setReservation_time(Integer.parseInt(time[0]));
		dto.setDuration(time.length);
		int res = service.insert(dto);
		if (res != 0) {
			List<Integer> list = service.getReservations2(dto);
			model.addAttribute("list", list);
			model.addAttribute("dto", dto);
			return "myReservation";
		}
		return "index";
	}

	@RequestMapping("reservationheader")
	public String reservationheader() {
		return "reservation/reservationheader";
	}

	@RequestMapping("reservationfooter")
	public String reservationfooter() {
		return "reservation/reservationfooter";
	}
}
