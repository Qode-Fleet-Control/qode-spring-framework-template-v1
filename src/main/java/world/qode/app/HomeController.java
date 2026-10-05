package world.qode.app;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public Map<String, String> home() {
		return Map.of("app", "spring-framework-template", "status", "ok");
	}

	// The fleet's health check (fleet.conf HEALTH_PATH).
	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of("status", "ok");
	}

}
