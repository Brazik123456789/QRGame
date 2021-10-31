package com.kolomin.balansir.Services;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kolomin.balansir.Config.SecurityConfig;
import com.kolomin.balansir.Entities.User;
import com.kolomin.balansir.Repositoeirs.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private HashMap<String, Date> tokenDateMap;
    private HashMap<String, User> tokenUserMap;
    private UserRepository userRepository;
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    @Autowired
    public UserService(UserRepository userRepository) {
        this.tokenDateMap = new HashMap<>();
        this.tokenUserMap = new HashMap<>();
        this.userRepository = userRepository;
    }

    public User getUserByLogin(String login){
        return userRepository.getByLogin(login);
    }

    public String login(HttpEntity<String> rq) {
        JsonElement request = new JsonParser().parse(rq.getBody());
        String login = request.getAsJsonObject().get("login").toString().replaceAll("\"","");
        String password = request.getAsJsonObject().get("password").toString().replaceAll("\"","");
        try {
            User user = getUserByLogin(login);
            if (SecurityConfig.bCryptPasswordEncoder().matches(password, user.getPassword())){
                String token = generateNewToken();
                tokenDateMap.put(token, new Date());
                tokenUserMap.put(token,user);
                System.out.println("tokenMap = " + tokenDateMap);
                System.out.println("tokenMap.get(token) = " + tokenDateMap.get(token));
                return "{\"success\": true, \"token\": \"" + token + "\", \"text\": \"Успех\", \"role\": \"" + user.getRole() + "\"}";
            } else {
                return "{\"success\": false, \"token\": \"\", \"text\": \"Неверный пароль\", \"role\": \"\"}";
            }
        } catch (Exception e){
            return "{\"success\": false, \"token\": \"\", \"text\": \"Пользователь не найден\", \"role\": \"\"}";
        }
    }

    public boolean chekToken(HttpEntity<String> rq) {
        updateTokenMaps();
        String token = rq.getHeaders().get("authToken").get(0);
        if (tokenDateMap.containsKey(token)){
            Date newDate = new Date();
//            System.out.println("newDate = " + newDate);
            long milliseconds = newDate.getTime() - tokenDateMap.get(token).getTime();
//            System.out.println("milliseconds = " + milliseconds);
//            System.out.println("TimeUnit.MILLISECONDS.toMinutes(minutes) = " + TimeUnit.MILLISECONDS.toMinutes(milliseconds));
            if (TimeUnit.MILLISECONDS.toMinutes(milliseconds) >= 1){    //  задаем время сессии в минутах
                System.out.println("Сессия сгорела");
                tokenDateMap.remove(token);
                tokenUserMap.remove(token);
                return false;
            } else {
                tokenDateMap.get(token).setTime(newDate.getTime());
                System.out.println("Сессия не сгорела, обновляю дату = " + tokenDateMap.get(token));
                return true;
            }
        } else {
            return false;
        }
    }

    private void updateTokenMaps() {
        Date newDate = new Date();
        ArrayList<String> tokens = new ArrayList<>();
        for (Map.Entry<String, Date> entry: tokenDateMap.entrySet()) {
            if (TimeUnit.MILLISECONDS.toHours(newDate.getTime() - entry.getValue().getTime()) > 1){     //  постоянно будут мониториться мапы и удаляться оттуда те записи, которые более часа висят
                tokens.add(entry.getKey());
            }
        }

        for (String token: tokens) {
            if (tokenDateMap.containsKey(token))
                tokenDateMap.remove(token);
        }

        for (String token: tokens) {
            if (tokenUserMap.containsKey(token))
                tokenUserMap.remove(token);
        }
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public String adduser(HttpEntity<String> rq) {
        String response = "{\"success\": ";
        JsonElement request = new JsonParser().parse(rq.getBody());
        String login = request.getAsJsonObject().get("login").toString().replaceAll("\"","");
        String password = request.getAsJsonObject().get("password").toString().replaceAll("\"","");
        User user = new User(login, SecurityConfig.bCryptPasswordEncoder().encode(password), "USER");
        try {
            userRepository.save(user);
            System.out.println("Добавили пользователя " + user);
            return response + true + "}";
        } catch (Exception e){
            return response + false + "}";
        }
    }

    public String deleteuser(HttpEntity<String> rq) {
        System.out.println("rq \n" + rq);
        JsonElement request = new JsonParser().parse(rq.getBody());
        String id = request.getAsJsonObject().get("id").toString().replaceAll("\"","");
        userRepository.deleteById(Long.valueOf(id));
        return "{\"success\": true}";
    }

    public boolean checkadmin(HttpEntity<String> rq) {
        String token = rq.getHeaders().get("authToken").get(0);
        if (tokenUserMap.containsKey(token)){
            if (tokenUserMap.get(token).getRole().equals("ADMIN")){    //  задаем логин админа
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String getusers() {
        List<User> users = userRepository.findAll();
        System.out.println("users = " + users);
        System.out.println("users.size() = " + users.size());
        List<User> users2 = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            if (!users.get(i).getRole().equals("ADMIN"))
                users2.add(users.get(i));
        }
        System.out.println("users2 = " + users2);
        return users2.toString();
    }
}
