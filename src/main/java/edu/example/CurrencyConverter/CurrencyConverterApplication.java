package edu.example.CurrencyConverter;

import edu.example.CurrencyConverter.telegrambot.CurrencyConverterBot;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@Component
@Setter
@RequiredArgsConstructor
public class CurrencyConverterApplication implements ApplicationRunner {

	private final CurrencyConverterBot currencyConverterBot;

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConverterApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		try {
			botsApi.registerBot(currencyConverterBot);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}
}
