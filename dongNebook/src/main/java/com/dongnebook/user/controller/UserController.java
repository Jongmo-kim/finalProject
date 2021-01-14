package com.dongnebook.user.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dongnebook.book.model.vo.Book;
import com.dongnebook.category.model.vo.Category;
import com.dongnebook.mail.Mail;
import com.dongnebook.mail.MailController;
import com.dongnebook.mail.MailException;
import com.dongnebook.mail.MailService;
import com.dongnebook.rental.model.vo.BookRental;
import com.dongnebook.user.model.service.UserService;
import com.dongnebook.user.model.vo.UpdateException;
import com.dongnebook.user.model.vo.User;
import com.dongnebook.user.model.vo.UserException;
import com.google.gson.JsonObject;

/**
 * @author 김종모
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService service;
	
	@RequestMapping("/signupFrm.do")
	public String signupFrm() {
		return "/user/signupFrm";
	}
	
	@RequestMapping("/signup.do")
	public String signup(User u,Category c,Model model) {
		u.setCategory(c);
		int result = service.insertUser(u);
		if(result > 0 ) {
			model.addAttribute("msg", "회원가입 성공");
			model.addAttribute("result", "true");
		} else {
			model.addAttribute("msg", "회원가입 성공");
		}
		model.addAttribute("loc", "/");
		return "common/msg";
	}
	
	@RequestMapping("/loginFrm.do")
	public String loginFrm() {
 		return "/user/loginFrm";
	}
	
	@RequestMapping("/login.do")
	public String login(Model model, User u, HttpSession session) {
		User loginUser = service.loginUser(u);
		if(loginUser != null) {
			int[] arr = returnAlert(loginUser);
			
			//null값 넘어오면 반납 예정 도서가 없는 것
			ArrayList<Book> bookList = new ArrayList<Book>();
			
			if(arr.length!=0) {
				bookList = service.selectBookList(loginUser, arr);
			}
			for(Book b : bookList) {
				System.out.println("반납할 책 이름 >"+b.getBookName());
			}
			model.addAttribute("msg", "로그인 성공");
			session.setAttribute("loginUser", loginUser);
			model.addAttribute("result", "true");
		}else {
			model.addAttribute("msg", "로그인 실패");
		}
		model.addAttribute("loc", "/");
		return "common/msg";
	}
	
	@ResponseBody
	public int[] returnAlert(User u) {
		ArrayList<BookRental> list = service.returnAlert(u);
		SimpleDateFormat format = new SimpleDateFormat ("yyyy/MM/dd");
		Date date = new Date();
		String today = format.format(date);
		
		// 4. 기준이 되는 날짜(format에 맞춘)
		Date setDate = null;
		try {
			setDate = format.parse(today);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 5. 한국 날짜 기준 Calendar 클래스 선언
		Calendar cal = Calendar.getInstance();

		// 6. 선언된 Calendar 클래스에 기준 날짜 설정
		cal.setTime(setDate);

		// 7. 하루 후로 날짜 설정
		cal.add(Calendar.DATE, +1);

		// 8. 하루후으로 설정된 날짜를 설정된 format으로 String 타입 변경
		String nextDate = format.format(cal.getTime());
		
//		//반납일이 하루 뒤인 책의 번호를 저장하는 list
//		ArrayList<BookRental> returnList = new ArrayList<BookRental>();
//		for(BookRental br : list) {
//			Date tmp = br.getReturnDate();
//			String returnDate = format.format(tmp);
//			if(nextDate.equals(returnDate)) {
//				returnList.add(br);
//			}
//		}
		
		//반납일이 하루 뒤인 책의 번호를 저장하는 list
		//1인당 책 3권 빌릴 수 있어서 int[3]
		int[] returnList = new int[3];
		int cnt = 0;
		for(BookRental br : list) {
			Date tmp = br.getReturnDate();
			String returnDate = format.format(tmp);
			if(nextDate.equals(returnDate)) {
				returnList[cnt] = br.getBookRentalNo();
				cnt++;
			}
		}
		return returnList;
	}
	
	
	@RequestMapping("/logout.do")
	public String logout(HttpSession session,Model model) {
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser != null) {
			model.addAttribute("msg", "로그아웃 성공");
			session.invalidate();
			model.addAttribute("result", "true");
		}else {
			model.addAttribute("msg", "로그아웃 실패");
		}
		model.addAttribute("loc", "/");
		return "common/msg";
	}
	@RequestMapping("/update.do")
	public String update(User u,Model model,HttpSession session) {
		int result = service.updateUser(u);
		if(result >0) {
			model.addAttribute("msg", "수정 성공");
			session.setAttribute("loginUser", service.selectOneUser(u));
			model.addAttribute("result", "true");
		}else {
			model.addAttribute("msg", "수정 실패");
		}
		model.addAttribute("loc", "/");
		return "common/msg";
	}
	@RequestMapping("/mypageFrm.do")
	public String mypage() {
		return "user/mypageFrm";
	}
	@RequestMapping("/delete.do")
	public String delete(User u,Model model,HttpSession session) {
		int result = service.deleteUser(u);
		String msg = "";
		try {
			isUpdateFail(result);
			model.addAttribute("result", "true");
			msg = "탈퇴 성공";
			session.invalidate();
		} catch (Exception e) {
			msg = e.getMessage();
		} finally {
			model.addAttribute("msg", msg);
			model.addAttribute("loc", "/");
		}
		return "common/msg";
	}
	@RequestMapping("/pwChangeFrm.do")
	public String pwChangeFrm() {
		return "user/pwChangeFrm";
	}
	
	@RequestMapping("/idSearchFrm.do")
	public String searchIdFrm() {
		return "user/idSearchFrm";
	}
	
	@Autowired
	MailController mailController;
	
	@RequestMapping("/searchId.do")
	public String searchId(Model model,User u) {
		User searchUser = service.selectOneUser(u);
		String msg = "";
		try {
			isUserNull(searchUser);
			sendMail(searchUser.getEmail(), "아이디 찾기", "귀하의 아이디는" + searchUser.getUserId());
			model.addAttribute("result", "true");
		} catch(Exception e){
			msg = e.getMessage();
		}finally {
			model.addAttribute("loc", "/");
			model.addAttribute("msg", msg);
		}
		return "common/msg";
	}
	
	@RequestMapping("/pwSearchFrm.do")
	public String pwSearchFrm() {
		return "user/pwSearchFrm";
	}
	
	@RequestMapping("/pwSearch.do")
	public String pwSearch(User u,Model model) {
		User searchUser = service.selectOneUser(u);
		String tempPw = RandomStringUtils.random(8,"0123123456qwertyuiopasdfghjklzxcvbnm");
		String msg = "";
		try {
			isUserNull(searchUser);
			searchUser.setUserPw(tempPw);
			int result = service.updateUser(searchUser);
			isUpdateFail(result);
			sendMail(searchUser.getEmail(), "비밀번호 찾기", "귀하의 비밀번호는" + tempPw);
			msg = "메일 전송 성공  메일을 확인해주세요";
			model.addAttribute("result", "true");
		}catch (Exception e) {
			msg = e.getMessage();
		}finally {
			model.addAttribute("loc", "/");
			model.addAttribute("msg", msg);
		}
		return "common/msg";
	}
	@ResponseBody
	@RequestMapping("/idNestedCheck.do")
	public JsonObject idNestedCheck(String inputId) {
		User u = new User();
		u.setUserId(inputId);
		User result = service.selectOneUser(u);
		
		JsonObject o = new JsonObject();
		if(result == null) {
			o.addProperty("isNested", false);
		} else {
			o.addProperty("isNested", true);
		}
		
		return o;
	}
	private void sendMail(String email, String title, String content) throws MailException {
		if(!mailController.mailSend(email, title, content)) {
			throw new MailException("메일을 보내지 못했습니다.");
		}
		
	}

	private void isUpdateFail(int result) throws UpdateException {
			if(result==0) {
				throw new UpdateException("수정이 진행되지 않았습니다.");
			}
		
	}

	private void isUserNull(User u) throws UserException{
			if(u==null) {
				throw new UserException("해당 정보로 검색된 회원이 없습니다.");
			}
	}
}
