package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharacterEncodingFilter implements Filter {
	FilterConfig config;
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain nextFilter) throws IOException, ServletException { // 필터 매개변수는 다음 필터 가리킴
		
		request.setCharacterEncoding("UTF-8"); 
		// 서블릿 컨테이너가 서블릿을 실행하기전 항상 하는 작업으로 매번 서블릿을 실행할때마다 반복해서 집어넣으므로 필터를 이용할 수 있다.
		
		nextFilter.doFilter(request, response); // 필터를 계속해서 호출하나 만약 더 이상 호출할 필터가 없다면 service() 메소드를 호출한다.
		// 서블릿을 실행한 후 수행할 작업
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

}
