package cl.dlab.pid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Clase principal de aplicacion excluye DataSource Configuration
 * 
 * @author fgonzalez
 *
 */
@SpringBootApplication()
public class App extends SpringBootServletInitializer {
	/**
	 * M\u00E9todo principal de la aplicacion
	 * 
	 * @param args Los argumentos env\u00EDados
	 */
	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "true");
		SpringApplication.run(App.class, args);
	}
	
}
