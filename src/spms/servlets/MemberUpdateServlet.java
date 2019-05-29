package spms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
/* 애노테이션을 이용하여 서블릿 배치 정보 설정
 * - 서블릿 초기화 파라미터도 애노테이션으로 처리 
 *
@WebServlet(
  urlPatterns={"/member/update"},
  initParams={
	  @WebInitParam(name="driver",value="com.mysql.jdbc.Driver"),
	  @WebInitParam(name="url",value="jdbc:mysql://localhost/studydb"),
	  @WebInitParam(name="username",value="study"),
	  @WebInitParam(name="password",value="study")
  }
)
*/
public class MemberUpdateServlet extends HttpServlet {
	@Override
	protected void doGet(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			ServletContext ctx = this.getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			conn = DriverManager.getConnection(
						ctx.getInitParameter("url"),
						ctx.getInitParameter("username"),
						ctx.getInitParameter("password")); 
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
				"SELECT MNO,EMAIL,MNAME,CRE_DATE FROM MEMBERS" + 
				" WHERE MNO=" + request.getParameter("no"));	
			rs.next();
			
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<html><head><title>회원정보</title></head>");
			out.println("<body><h1>회원 정보 수정</h1>");
			out.println("<form action='update' method='post'>");
			out.println("번호: <input type='text' name='no' value='" +
				request.getParameter("no") + "' readonly><br>"); 
			// 회원 번호(MNO)는 주 키(Primary key)이기 때문에
			// 상자에는 정보를 변경할 수 없도록 readonly를 주었다. 이러면 변경 불가.
			// URL을 통해 넘겨받은 고유의 값 'no'를 통해 해당 데이터베이스의 정보를 출력한다.
			out.println("이름: <input type='text' name='name'" +
				" value='" + rs.getString("MNAME")  + "'><br>");
			out.println("이메일: <input type='text' name='email'" +
				" value='" + rs.getString("EMAIL")  + "'><br>");
			out.println("가입일: " + rs.getDate("CRE_DATE") + "<br>");
			// 가입일은 변경하지 않을 것이기에 일반 출력한다.
			out.println("<input type='submit' value='저장'>");
			out.println("<input type='button' value='삭제' onclick=\"location.href='delete?no=" +
					request.getParameter("no") + "';\">");
			out.println("<input type='button' value='취소'" + 
				" onclick='location.href=\"list\"'>");
			// 취소 버튼을 눌렀을 때 회원 목록 페이지로 이동하고자 onclick 속성에 자바스크립트 문구.
			// location은 웹 브라우저의 페이지 이동을 관리하는 자바스크립트 객체
			// href 프로퍼티는 웹 브라우저가 출력할 페이지의 URL. 즉 /member/list로 이동.
			
			out.println("</form>");
			out.println("</body></html>");
			
		} catch (Exception e) {
			throw new ServletException(e);
			
		} finally {
			try {if (rs != null) rs.close();} catch(Exception e) {}
			try {if (stmt != null) stmt.close();} catch(Exception e) {}
			try {if (conn != null) conn.close();} catch(Exception e) {}
		}
	}
	
	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//request.setCharacterEncoding("UTF-8");
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			ServletContext ctx = this.getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			conn = DriverManager.getConnection(
						ctx.getInitParameter("url"),
						ctx.getInitParameter("username"),
						ctx.getInitParameter("password")); 
			stmt = conn.prepareStatement(
					"UPDATE MEMBERS SET EMAIL=?,MNAME=?,MOD_DATE=now()"
					+ " WHERE MNO=?");
			stmt.setString(1, request.getParameter("email"));
			stmt.setString(2, request.getParameter("name"));
			stmt.setInt(3, Integer.parseInt(request.getParameter("no")));
			// ?의 순서대로 setString의 첫 번째 파라미터에 parameterIndex 순서대로 준다.
			// 두 번째 파라미터는 넣을 스트링값.
			
			stmt.executeUpdate();
			// 업데이트 사항을 실행시키는 명령어.
			
			response.sendRedirect("list");
			
		} catch (Exception e) {
			throw new ServletException(e);
			
		} finally {
			try {if (stmt != null) stmt.close();} catch(Exception e) {}
			try {if (conn != null) conn.close();} catch(Exception e) {}
		}
	}
}
