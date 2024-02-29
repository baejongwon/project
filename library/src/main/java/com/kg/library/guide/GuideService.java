package com.kg.library.guide;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.kg.library.book.BookDTO;
import com.kg.library.book.IBookMapper;
import com.kg.library.reservation.ReservationDTO;
import com.kg.library.reservation.ReservationMapper;

import jakarta.servlet.http.HttpSession;

@Service
public class GuideService {
	
}
