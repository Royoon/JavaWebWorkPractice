package spms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MemberAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override 	// Ctrl + Space 누르면 Override할 메소드 목록 쫙 뜨고 클릭하면 모든 정보 다 완성됨.
	protected void doGet(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>회원 등록</title></head>");
		out.println("<body><h1>회원 신규 등록</h1>");
		out.println("<form action='add' method='post'>");
		out.println("이름: <input type='text' name='name'><br>");
		out.println("이메일: <input type='text' name='email'><br>");
		out.println("암호: <input type='password' name='password'><br>");
		out.println("<input type='submit' value='추가'>");
		out.println("<input type='reset' value='취소'>");
		out.println("</form>");
		out.println("</body></html>");
	}
	
	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//request.setCharacterEncoding("UTF-8"); // 이것이 getParameter를 사전에 변환하는 과정.
		// 다만 GET 요청으로 데이터를 URL을 통해 보내면 여전히 한글이 깨지는데 이는 이 메소드로는 해결이 안된다.
		// 이는 톰캣 서버의 server.xml에서 <Connector connectionTimeout= ~~~ > 절을 찾아 URIEncoding="UTF-8"/>을 추가해주어야 한다.
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			//DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			ServletContext ctx = this.getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			conn = DriverManager.getConnection(
					ctx.getInitParameter("url"), //JDBC URL
					ctx.getInitParameter("username"),	// DBMS 사용자 아이디
					ctx.getInitParameter("password"));	// DBMS 사용자 암호
			stmt = conn.prepareStatement( // 임파라미터('?') 아직 정해지지 않은 값을 넣어주는 것. 
					// prepareStatement을 쓸 경우 SQL 문을 미리 준비하여 컴파일해 두기 때문에 실행 속도가 Statement보다 빠르며 유용하다.
					"INSERT INTO MEMBERS(EMAIL,PWD,MNAME,CRE_DATE,MOD_DATE)"
					+ " VALUES (?,?,?,NOW(),NOW())");
			stmt.setString(1, request.getParameter("email"));
			stmt.setString(2, request.getParameter("password"));
			stmt.setString(3, request.getParameter("name"));
			stmt.executeUpdate();
			
			response.sendRedirect("list");
			// 리다이렉트. 즉시 다른 화면으로 전송하는 방법. 뒤에 아무리 내용이 있어봐야 이것이 호출되면 뒤에 호출 내용 건너뜀. 
			
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<html><head><title>회원등록결과</title>"
					+ "<meta http-equiv='Refresh' content='1; url=list'>"
					+ "</head>");
			out.println("<body>");
			out.println("<p>등록 성공입니다!</p>");
			out.println("</body></html>");
			// 한글 입력 값이 깨지는 이유?
			// 회원 등록란에서 추가로 POST 요청을 보내면 입력폼 파라미터값(한 칸 건너뛰어서 보내는 형식)을 보내게 된다.
			// 한글은 URL인코딩이란 형식으로 만들어져서 보낸다. UTF-8을 URL 인코딩 한 것.
			// 서버에서 이 값을 받게 되면 톰캣 서버는 URL 인코딩한 것을 URL 디코딩하여 UTF-8로 만든 다음에 서블릿에 전달한다.
			// 하지만 getParameter 호출할 때 이미 ISO로 생각하여 이미 잘못 변환된 유니코드이므로 이것을 해결해야 한다.
			// 그래서 getParameter을 호출할 때 사전에 UTF-8로 변환해야 한다.
			
			//response.setHeader("Refresh", "1;url=list"); // response.addHeader("Refresh", "1;url=list");
			// add는 추가, set은 덮어쓰기 따라서 set이 낫다.
			
		} catch (Exception e) {
			throw new ServletException(e);
			
		} finally {
			try {if (stmt != null) stmt.close();} catch(Exception e) {}
			try {if (conn != null) conn.close();} catch(Exception e) {}
		}

	}
}
