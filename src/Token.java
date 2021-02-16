

import java.io.InputStream;
//import java.security.Key;
import java.util.Base64;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

public class Token {
    String userId;
    String ip;
    String token;
    private String properties = "secret.properties";
    private static String SECRET_KEY = "";
    int fixedLength = 72;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (Integer.valueOf(userId) >= 1 && Integer.valueOf(userId) <= 9999)
            this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    //Creates a Token
    public Token(String userId, String ip) {
        this.userId = userId;
        this.ip = ip;
        try {
            //token get created in generateToken
            this.token = generateToken();
        } catch (Exception e) {
        }
    }

    // Decodes the Token
    public Token(String token) throws Exception {
        this.token = token;
        getSecurityKey();
        //Take out the information pieces
        byte[] secretDecodedBytes = Base64.getDecoder().decode(token.substring(token.indexOf("$") + 1));
        String secret = new String(secretDecodedBytes);
        //System.out.println("key:" + secret);
        //secret key is used to bring the additional authentication layer
        if (secret.equals(SECRET_KEY) && token.length() == fixedLength) {
            String userId = token.substring(0, token.indexOf("*"));
            byte[] userDecodedBytes = Base64.getDecoder().decode(userId);
            this.userId = new String(userDecodedBytes);

            String ip = token.substring(token.indexOf("*") + 1, token.indexOf("#"));
            byte[] ipDecodedBytes = Base64.getDecoder().decode(ip);
            this.ip = new String(ipDecodedBytes);
        }
    }

    //generate token with the needed information
    private String generateToken() throws Exception {
        //gets the key a from property file
        getSecurityKey();

        //create a fix length (72) string of random chars
        String token = getRandomString(fixedLength);

        //convert the needed information to base x
        String encodedUserId = Base64.getEncoder().encodeToString(userId.getBytes());
        String encodedIp = Base64.getEncoder().encodeToString(ip.getBytes());
        String encodedSecret = "$" + Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());

        // put together all encoded information
        String payload = encodedUserId + "*" + encodedIp + "#";
        payload += token.substring(payload.length(), token.length() - encodedSecret.length()) + encodedSecret;
        return payload;
    }

    //get the secret key from properties file
    //secret key is used to bring to additional layer of security
    private void getSecurityKey() throws Exception {
        try {
            InputStream input = this.getClass().getResourceAsStream(properties);
            Properties prop = new Properties();
            prop.load(input);
            this.SECRET_KEY = prop.getProperty("key");

        } catch (IOException e) {
            throw new Exception();
        }
    }

    //generates a string of length n with random values
    private static String getRandomString(int n) {
        // String to choose char at random index
        String saltString = "abcdefghijklmnopqrstuvxyz"
                + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            // generate a random number & append to target string
            int index = (int) (saltString.length() * Math.random());
            sb.append(saltString.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        //test
        Token t1 = new Token("9999", "91.10.240.150");
        System.out.println("encoded userId: " + t1.getUserId());
        System.out.println("encoded ip: " + t1.getIp());
        System.out.println("encoded token: " + t1.getToken());
        System.out.println("\n****************\n");
        Token t2 = new Token("OTk5OQ==*OTEuMTAuMjQwLjE1MA==#IxSQEDdldhPzuZeAiRQe7MC5rSpgxmBsG$VDNzNHQ=");
        System.out.println("decoded userId: " + t2.getUserId());
        System.out.println("decoded ip: " + t2.getIp());
        System.out.println("decoded token: " + t2.getToken());
    }
}
