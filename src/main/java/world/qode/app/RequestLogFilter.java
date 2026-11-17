package world.qode.app;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Writes one JSON line to stdout for every finished HTTP request, health checks included:
 * {"method":"GET","path":"/health","status":200,"durationMs":1.23,"ua":"curl/8.5.0"}
 *
 * KEEP THIS: one log line per request (status + duration) is what the workspace Monitoring tab charts.
 * Never add request/response bodies, cookies, Authorization headers or query strings here.
 * Registered on Tomcat in {@link Application} for every path, ahead of the DispatcherServlet.
 */
public class RequestLogFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		long start = System.nanoTime();
		int status = 500;
		try {
			chain.doFilter(request, response);
			status = response.getStatus();
		}
		finally {
			double durationMs = Math.round((System.nanoTime() - start) / 10_000.0) / 100.0;
			System.out.println("{\"method\":\"" + json(request.getMethod()) + "\",\"path\":\""
					+ json(request.getRequestURI()) + "\",\"status\":" + status + ",\"durationMs\":" + durationMs
					+ ",\"ua\":\"" + json(request.getHeader("User-Agent")) + "\"}");
		}
	}

	private static String json(String s) {
		if (s == null) {
			return "";
		}
		StringBuilder b = new StringBuilder(s.length());
		for (char c : s.toCharArray()) {
			switch (c) {
				case '"' -> b.append("\\\"");
				case '\\' -> b.append("\\\\");
				default -> {
					if (c < 0x20) {
						b.append(String.format("\\u%04x", (int) c));
					}
					else {
						b.append(c);
					}
				}
			}
		}
		return b.toString();
	}

}
