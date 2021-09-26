package com.kolomin.balansir;

import com.kolomin.balansir.Services.AdminService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.ViewResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис по генерации QR-кодов для мероприятий и балансировкой пользователей между внешними ресурсами (боты телеграм, ссылки на личку ватсап и тд)
 * @author Жиеналин Азамат
 * */
@SpringBootApplication
@ComponentScan
@EnableScheduling
@EnableAutoConfiguration
public class BalansirApplication extends SpringBootServletInitializer {

	public static String thisUrl = "http://192.168.1.83:80/";

	public static Map<String, Map> events = new HashMap<>();
	public static Map<String, ArrayList<String>> urls = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(BalansirApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BalansirApplication.class);
	}
}