package edu.example.CurrencyConverter.telegrambot;

import edu.example.CurrencyConverter.model.Country;
import edu.example.CurrencyConverter.model.Currency;
import edu.example.CurrencyConverter.service.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.*;

@Component
@RequiredArgsConstructor
@Setter
public class CurrencyConverterBot extends TelegramLongPollingBot {
    private final CurrencyConverterService currencyConverterService;
    private final MessageSource messageSource;
    private final CurrencyService currencyService;
    private final CountriesTrackerService countriesTrackerService;
    private final CountryService countryService;
    private String country = "";
    private int page = 0;
    private String lang = "";
    private String toCurrency = "";
    private String fromCurrency = "";
    private double numberOfCurrency = 0.0;
    private int choiceOfClient = 0;
    private int messageId;
    @Value("${bot-username}")
    private String botUsername;
    @Value("${bot-token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            if ("/start".equals(messageText)) {
                sendChoiceLangMessage(chatId);
                lang = "";
                setFieldsToNull();
            } else if (!fromCurrency.isEmpty() && !toCurrency.isEmpty()) {
                try {
                    numberOfCurrency = Double.parseDouble(messageText);
                    sendResultMessage(chatId);
                    setFieldsToNull();
                } catch (NumberFormatException e) {
                    sendNumberFormatExceptionMessage(chatId);
                }
            }

        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

            if (callBackData.equals("->")) {
                page++;
            } else if (callBackData.equals("<-")) {
                page--;
            }

            if (callBackData.startsWith("lang")) {
                lang = callBackData.substring(5);
                sendHelloMessage(chatId, false);
            } else if (callBackData.equals("menu_button")) {
                if(choiceOfClient == 2) {
                    deleteMessage(chatId, messageId);
                }
                setFieldsToNull();
                sendHelloMessage(chatId, true);
            } else if (callBackData.equals("calculate_button") || (fromCurrency.isEmpty() && choiceOfClient == 1 && (callBackData.equals("<-") || callBackData.equals("->")))) {
                choiceOfClient = 1;
                sendCalculateMessage(chatId);
            } else if (callBackData.equals("exchange_rate_button")) {
                choiceOfClient = 2;
                sendAllRatesMessage(chatId);
            } else if (callBackData.equals("countries_button") || choiceOfClient == 3 && (callBackData.equals("<-") || callBackData.equals("->"))) {
                choiceOfClient = 3;
                sendAllCountriesMessage(chatId);
            } else if (choiceOfClient == 1) {
                if (callBackData.startsWith("currency") && fromCurrency.isEmpty() || (toCurrency.isEmpty() && (callBackData.equals("<-") || callBackData.equals("->")))) {
                    if (fromCurrency.isEmpty()) {
                        page = 0;
                        fromCurrency = callBackData.substring(9);
                    }
                    sendCalculateMessageTwo(chatId, fromCurrency);
                } else if (callBackData.startsWith("currency") && toCurrency.isEmpty()) {
                    page = 0;
                    toCurrency = callBackData.substring(9);
                    sendCalculateMessageThree(chatId);
                } else {
                    sendDoNotKnowMessage(chatId);
                }
            } else if (choiceOfClient == 3 && callBackData.startsWith("country")) {
                country = callBackData.substring(8);
                sendCountryInfoMessage(chatId);
            } else {
                sendDoNotKnowMessage(chatId);
            }
        }
    }

    private void setFieldsToNull() {
        fromCurrency = "";
        toCurrency = "";
        numberOfCurrency = 0.0;
        choiceOfClient = 0;
        page = 0;
        country = "";
    }

    private void deleteMessage(String chatId, int previousMessageId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, previousMessageId);
        try {
            execute(deleteMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageForAllRates(String chatId, String text, InlineKeyboardMarkup keyboardMarkup) {
        deleteMessage(chatId, messageId);

        EditMessageText message = new EditMessageText();
        message.setMessageId(messageId);
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            SendMessage messageEx = new SendMessage();
            messageEx.setChatId(chatId);
            messageEx.setText(text);
            messageEx.setReplyMarkup(keyboardMarkup);
            try {
                messageId = execute(messageEx).getMessageId();
            } catch (TelegramApiException ex) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendMessage(String chatId, String text, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageCaption message = new EditMessageCaption();
        message.setMessageId(messageId);
        message.setChatId(chatId);
        message.setCaption(text);
        if(keyboardMarkup != null) {
            message.setReplyMarkup(keyboardMarkup);
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            SendPhoto messageEx = new SendPhoto();
            messageEx.setChatId(chatId);
            messageEx.setCaption(text);
            InputFile photo = new InputFile(new File("src/main/resources/logo/PUQSAqKLIx55dEcq.png"));
            messageEx.setPhoto(photo);
            if(keyboardMarkup != null) {
                messageEx.setReplyMarkup(keyboardMarkup);
            }
            try {
                messageId = execute(messageEx).getMessageId();
            } catch (TelegramApiException ex) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendChoiceLangMessage(String chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton keyboardButtonEnLang = new InlineKeyboardButton("English");
        InlineKeyboardButton keyboardButtonRuLang = new InlineKeyboardButton("Русский");
        keyboardButtonEnLang.setCallbackData("lang en");
        keyboardButtonRuLang.setCallbackData("lang ru");
        keyboardMarkup.setKeyboard(List.of(List.of(keyboardButtonEnLang, keyboardButtonRuLang)));
        String text = "Please choose a language.";
        sendMessage(chatId, text, keyboardMarkup);
    }

    private void sendHelloMessage(String chatId, boolean hasMenu) {
        String text;
        if (hasMenu) {
            text = messageSource.getMessage("menu", null, new Locale(lang));
        } else {
            text = messageSource.getMessage("greetings", null, new Locale(lang));
        }
        sendMessage(chatId, text, getHelloKeyBoardMarkup());
    }

    private void sendCalculateMessage(String chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(List.of(getCurrencyCodeKeyBoardRow(), getServiceLineKeyboardRow()));
        String text = messageSource.getMessage("choice_of_first_currency", null, new Locale(lang));
        sendMessage(chatId, text, keyboardMarkup);
    }

    private void sendCalculateMessageTwo(String chatId, String currencyName) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> inlineKeyboardButtons = getCurrencyCodeKeyBoardRow();
        for (int i = 0; i < inlineKeyboardButtons.size(); i++) {
            if (inlineKeyboardButtons.get(i).getText().equals(currencyName)) {
                inlineKeyboardButtons.remove(inlineKeyboardButtons.get(i));
            }
        }
        keyboardMarkup.setKeyboard(List.of(inlineKeyboardButtons, getServiceLineKeyboardRow()));
        String text = messageSource.getMessage("choice_of_second_currency", null, new Locale(lang));
        sendMessage(chatId, text, keyboardMarkup);
    }

    private List<InlineKeyboardButton> getServiceLineKeyboardRow() {
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButtonBack = new InlineKeyboardButton("<-");
        inlineKeyboardButtonBack.setCallbackData("<-");
        InlineKeyboardButton inlineKeyboardButtonAhead = new InlineKeyboardButton("->");
        inlineKeyboardButtonAhead.setCallbackData("->");
        InlineKeyboardButton inlineKeyboardButtonMenu = new InlineKeyboardButton(messageSource.getMessage("menu_button", null, new Locale(lang)));
        inlineKeyboardButtonMenu.setCallbackData("menu_button");
        inlineKeyboardButtons.add(inlineKeyboardButtonBack);
        inlineKeyboardButtons.add(inlineKeyboardButtonAhead);
        inlineKeyboardButtons.add(inlineKeyboardButtonMenu);
        return inlineKeyboardButtons;
    }

    private List<InlineKeyboardButton> getCurrencyCodeKeyBoardRow() {
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<Currency> currencies = currencyService.findAll(page);
        for (Currency currency : currencies) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(currency.getName());
            inlineKeyboardButton.setCallbackData("currency " + currency.getName());
            inlineKeyboardButtons.add(inlineKeyboardButton);
        }
        return inlineKeyboardButtons;
    }

    private void sendCalculateMessageThree(String chatId) {
        String text = messageSource.getMessage("put_number_of_currency", null, new Locale(lang))
                + " " + fromCurrency + " "
                + messageSource.getMessage("example", null, new Locale(lang));
        sendMessage(chatId, text, null);
    }

    private void sendResultMessage(String chatId) {
        double result = currencyConverterService.convertCurrency(numberOfCurrency, fromCurrency, toCurrency);
        deleteMessage(chatId, messageId);
        messageId = 0;
        String text = numberOfCurrency + " " + fromCurrency + " = " + result + " " + toCurrency;
        sendMessage(chatId, text, getMenuKeyBoardMarkup());

    }

    private void sendAllRatesMessage(String chatId) {
        StringBuilder stringBuilder = currencyConverterService.getAllRates();
        String text = messageSource.getMessage("done", null, new Locale(lang))
                + "\n"
                + messageSource.getMessage("all_rates", null, new Locale(lang))
                + "\n\n"
                + stringBuilder;
        sendMessageForAllRates(chatId, text, getMenuKeyBoardMarkup());
    }

    private List<List<InlineKeyboardButton>> getAllCountriesKeyboardLine() {
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<Country> countries = countryService.findAll(page);
        for (Country country : countries) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(messageSource.getMessage(country.getName(), null, new Locale(lang)));
            inlineKeyboardButton.setCallbackData("country" + " " + country.getName());
            inlineKeyboardButtons.add(List.of(inlineKeyboardButton));
        }
        inlineKeyboardButtons.add(getServiceLineKeyboardRow());
        return inlineKeyboardButtons;
    }

    private void sendAllCountriesMessage(String chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(getAllCountriesKeyboardLine());
        String text = messageSource.getMessage("all_countries_first", null, new Locale(lang));
        sendMessage(chatId, text, keyboardMarkup);
    }

    private void sendCountryInfoMessage(String chatId) {
        Map<String, String> resultMap = countriesTrackerService.getInfoAboutCountry(country);
        String text = messageSource.getMessage("country_info", null, new Locale(lang))
                + messageSource.getMessage(country, null, new Locale(lang)) + "\n\n"
                + messageSource.getMessage("inflation", null, new Locale(lang)) + resultMap.get("Inflation") + "%.\n"
                + messageSource.getMessage("debt", null, new Locale(lang)) + resultMap.get("State debt") + "%.\n"
                + messageSource.getMessage("gdp", null, new Locale(lang)) + " - " + resultMap.get("GDP growth") + " " + resultMap.get("Currency") + "\n"
                + messageSource.getMessage("unemployment", null, new Locale(lang)) + resultMap.get("Unemployment") + "%.\n"
                + messageSource.getMessage("rate", null, new Locale(lang)) + resultMap.get("Rate") + "%.";
        sendMessage(chatId, text, getMenuKeyBoardMarkup());
    }

    private InlineKeyboardMarkup getMenuKeyBoardMarkup() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButtonMenu = new InlineKeyboardButton(messageSource.getMessage("menu_button", null, new Locale(lang)));
        inlineKeyboardButtonMenu.setCallbackData("menu_button");
        keyboardMarkup.setKeyboard(List.of(List.of(inlineKeyboardButtonMenu)));
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getHelloKeyBoardMarkup() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton keyboardButtonOne = new InlineKeyboardButton(messageSource.getMessage("calculate_button", null, new Locale(lang)));
        InlineKeyboardButton keyboardButtonTwo = new InlineKeyboardButton(messageSource.getMessage("exchange_rate_button", null, new Locale(lang)));
        InlineKeyboardButton keyboardButtonThree = new InlineKeyboardButton(messageSource.getMessage("countries_button", null, new Locale(lang)));
        keyboardButtonOne.setCallbackData("calculate_button");
        keyboardButtonTwo.setCallbackData("exchange_rate_button");
        keyboardButtonThree.setCallbackData("countries_button");
        keyboardMarkup.setKeyboard(List.of(List.of(keyboardButtonOne, keyboardButtonTwo, keyboardButtonThree)));

        return keyboardMarkup;
    }

    private void sendDoNotKnowMessage(String chatId) {
        String text = messageSource.getMessage("do_not_know_message", null, new Locale(lang));
        sendMessage(chatId, text, null);
    }

    private void sendNumberFormatExceptionMessage(String chatId) {
        String text = messageSource.getMessage("number_format_error_message", null, new Locale(lang));
        sendMessage(chatId, text, null);
    }
}
