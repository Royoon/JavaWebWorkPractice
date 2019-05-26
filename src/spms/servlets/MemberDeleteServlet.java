package spms.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MemberDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		Connection conn = null; // DBMS 연결을 위한 변수
		Statement stmt = null; // DBMS에 쿼리를 던지기 위한 변수
		ResultSet rs = null; // DBMS에서 쿼리 결과를 가져오기 위한 변수 -> 여기선 필요 없음.
		try {
			ServletContext ctx = this.getServletContext(); // DD파일에서 DBMS 정보를 얻어오기 위한 객체 생성
			Class.forName(ctx.getInitParameter("driver")); // 이 방법을 사용하여 이름만 가져와 일일이 driver명을 안적어줘도 됨
			conn = DriverManager.getConnection( // connection 변수에 DBMS의 url, username, password를 연결함
					ctx.getInitParameter("url"),
					ctx.getInitParameter("username"),
					ctx.getInitParameter("password"));
			stmt = conn.createStatement(); // 연결된 conn 변수에서 쿼리를 위한 객체 생성
			stmt.executeUpdate(
					"DELETE FROM MEMBERS WHERE MNO =" +
					request.getParameter("no"));
			
			response.sendRedirect("list");
			
			
			
		} catch (Exception e) {
			throw new ServletException(e); //  ClassNotFoundException와 SQLException 예외처리
		} finally {
			try {if (rs != null) rs.close();} catch(Exception e) {}
			try {if (stmt != null) stmt.close();} catch(Exception e) {}
			try {if (conn != null) conn.close();} catch(Exception e) {}
			// 차례로 자원 할당 해제
		}
	}
}
