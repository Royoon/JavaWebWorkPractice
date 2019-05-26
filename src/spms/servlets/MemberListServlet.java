package spms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MemberListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			//******** 1. 사용할 JDBC 드라이버를 등록하라. ********
			//ServletConfig config = this.getServletConfig(); 
			// DD파일에 저장되어 있는 DBMS 정보를 꺼내오기 위해  ServletConfig 객체를 사용해야 한다.
			// 이 객체의 getInitParameter("파라미터명") 메소드를 사용하면 꺼내올 수 있다.
			//Class.forName(config.getInitParameter("driver"));
			// Class.forName()은 인자값으로 클래스 이름을 넘기면 해당 클래스를 찾아 로딩하는 방식이다.
			// 이 방식의 파라미터 값으로는 반드시 클래스명을 명시해줘야 하는데 이를 QName, 또는 fully qualified name이라 한다.
			// 클래스 방식이므로 역시 예외처리문을 추가해줘야한다.
			
			ServletContext ctx = this.getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			// GenericServlet은 ServletConfig를 이미 구현한 서블릿이기에 굳이 호출하지 않고 this.를 이용하면 된다.
			
			
			//DriverManager.registerDriver(new com.mysql.jdbc.Driver()); 
			// 드라이버 등록하는 것은 DriverManager를 사용. 여기에 registerDriver 메소드 사용. 
			// 여기에 들어갈 mysql 객체 중 com.mysql.jdbc.Driver()를 사용.
			// 그런데 DriverManager는 나중에 오류가 발생할 때 SQLException을 예외를 던질 수 있다. 따라서 예외 처리를 해준다.
			
			//******** 2. 드라이버를 사용하여 MySQL 서버와 연결하라. ********
			con = DriverManager.getConnection(
					ctx.getInitParameter("url"),
					ctx.getInitParameter("username"),
					ctx.getInitParameter("password"));
			// DriverManager의 getConection을 사용. 여기에 파라미터 값으로 url, id, pwd를 줘야함
			// url은 각 DBMS별로 다르기에 확인해야함. 
			// DriverManager은 Connection 객체를 리턴하므로 받아야한다.
			// java.sql.Connection 라이브러리 import
			// 마찬가지로 this.getInitParameter를 이용하여 DD파일에 저장된 변수 값으로 호출함
		
			//******** 3. 커넥션 객체로부터 SQL을 던질 객체를 준비하라. ********
			stmt = con.createStatement();
			// con 객체의 createStatement 메소드를 생성하고  이것이 반환하는 것은 java.sql.Statement 인터페이스의 구현체다.
			// 이 객체를 통해 데이터베이스에 SQL문을 보낼 수 있다.
			
			//******** 4. SQL을 던지는 객체를 사용하여 서버에 질의하라. ********
			rs = stmt.executeQuery(
					"select MNO, MNAME, EMAIL, CRE_DATE" + 
					" from MEMBERS" + 
					" order by MNO ASC");
			// MEMBERS 테이블에서 MNO, MNAME, EMAIL, CRE_DATE 어트리뷰트를 가져와 MNO 어트리뷰트를 기준으로 오름차순 정렬
			// executeQuery()가 반환하는 객체는 java.sql.ResultSet 인터페이스의 구현체이다.
			// 이 반환 객체를 통하여 서버에서 질의 결과를 가져올 수 있다.
			
			// first()는 서버에서 첫 번째 레코드를 가져온다.
			// last()는 서버에서 마지막 레코드를 가져온다.
			// previous()는 서버에서 이전 레코드를 가져온다.
			// next()는 서버에서 다음 레코드를 가져온다.
			// getXXX()는 레코드에서 특정 칼럼의 값을 꺼내며 XXX는 칼럼의 타입에 따라 다른 이름을 갖는다.
			// updateXXX()는 레코드에서 특정 칼럼의 값을 변경한다.
			// deleteRow()는 현재 레코드를 지운다.
			// 다음의 구현체를 이용하여 서버에 만들어진 SELECT 결과를 가져올 수 있고, 가져온 레코드에서 특정 칼럼 값을 꺼낼 수 있다.
			
			//******** 5. 서버에서 가져온 데이터를 사용하여 HTML을 만들어서 웹 브라우저로 출력하라. ********
			response.setContentType("text/html;charset=UTF-8");
			// 출력 스트림을 얻기 전 setContentType을 호출하여 데이터 형식(HTML)과 문자 집합(UTF-8)을 지정한다.
			PrintWriter out = response.getWriter();
			// 출력하려면 출력 스트림 선언
			
			out.println("<html><head><title>회원목록</title></head><body>");
			out.println("<h1>회원 목록</h1>");
			out.println("<p><a href='add'>신규 회원</a></p>"); 
			// a href = 'add'를 하면 상대경로로 준다. member 밑의 경로로
			while(rs.next()) {
				out.println(
						rs.getInt("MNO") + ". " +
						"<a href='update?no=" + rs.getInt("MNO") + "'>" +
						rs.getString("MNAME") + "</a>," +
						rs.getString("EMAIL") + "," + 
						rs.getDate("CRE_DATE") +
						"<a href='delete?no=" + rs.getInt("MNO") + "'>" +
						"[삭제]" + "</a>" + "<br>"
					);
			}
			out.println("</body></html>");
			// 다음과 같이 html 태그를 출력한다.
			
			/*
			try {rs.close();} catch (Exception e) {}
			try {stmt.close();} catch (Exception e) {}
			try {con.close();} catch (Exception e) {}
			*/
			// 자원 해제는 반드시 해줘야 한다. 자원을 해제할 때에는 생성한 것과 반대의 순서로 해주면 된다.
			// 다만 해제 도중 앞선 객체가 해제가 안되면 다음 객체도 해제가 안되니 예외처리를 통해 해제해주면 된다.
			// 하지만 앞서 html 태그를 출력하는 도중에 에러가 발생하면 바로 예외처리 되므로 다음 위치는 좋지 않다.
			// 따라서 finally에 넣어주면 위치에 상관 없이 자원은 항상 해제된다.
		} catch (Exception e) { // SQLException이 발생한다면 ServletException 예외를 톰캣 서버에 던진다.
			// ClassNotFoundException와 SQLException의 부모를 한번에 예외처리하면 두 번 안해도 됨.
			throw new ServletException(e);
			// 톰캣 서버는 해당 예외 처리를 받으면 ServletException에 있는 정보를 웹 브라우저에 출력한다.
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {stmt.close();} catch (Exception e) {}
			try {con.close();} catch (Exception e) {}
			// 근데 객체를 try 안에 두었으므로 finally 안에서 접근 불가능
			// 따라서 다음 객체들을 바깥쪽에 두어야 한다.
		}
	}
	
}
