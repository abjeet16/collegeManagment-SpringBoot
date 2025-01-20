package com.mynotes.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service // Marks this class as a Spring service, which will be managed by Spring's IoC container.
public class JwtTokenService {

    // Secret key used for signing JWT tokens.
    public String SECRET_KEY = "33743677397A24432646294A404E635266556A576E5A7234753778214125442A";
    private Date CURRENT_TIME = new Date(System.currentTimeMillis()); // Sets the current time for the token issuance.
    private Date EXPIRATION_TIME = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 *10); // Sets the expiration time for the token (24 hours).

    /**
     * Extracts the username (subject) from the JWT token.
     * @param token The JWT token from which the username is to be extracted.
     * @return The username contained in the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Extracts the subject (username) from the token claims.
    }

    /**
     * Extracts the expiration date from the JWT token.
     * @param token The JWT token from which the expiration date is to be extracted.
     * @return The expiration date contained in the token.
     */
    public Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration); // Extracts the expiration date from the token claims.
        } catch (ExpiredJwtException e) {
            System.out.println("The token has expired.");
            return null;  // Return null if the token is expired
        } catch (Exception e) {
            System.out.println("Error parsing the token: " + e.getMessage());
            return null;  // Return null for any other exception
        }
    }

    /**
     * Generic method to extract any claim from the JWT token.
     * @param token The JWT token from which the claim is to be extracted.
     * @param claimsResolver A function that extracts the desired claim from the Claims object.
     * @param <T> The type of the claim to be extracted.
     * @return The extracted claim value.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Extracts all claims from the token.
        return claimsResolver.apply(claims); // Applies the claims resolver function to extract the specific claim.
    }

    /**
     * Extracts all claims from the JWT token.
     * @param token The JWT token from which all claims are to be extracted.
     * @return The Claims object containing all the claims from the token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Sets the signing key to verify the token's authenticity.
                .build()
                .parseClaimsJws(token) // Parses the JWT and returns the claims body.
                .getBody();
    }

    /**
     * Generates a signing key from the secret key.
     * @return The signing key.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decodes the secret key from Base64 encoding.
        return Keys.hmacShaKeyFor(keyBytes); // Generates the signing key using HMAC SHA-256.
    }

    /**
     * Checks whether the token has expired.
     * @param token The JWT token to be checked for expiration.
     * @return True if the token is expired, otherwise false.
     */
    public Boolean isTokenExpired(String token) {
        if (extractExpiration(token) == null) {
            return true;
        }
        return extractExpiration(token).before(new Date()); // Compares the expiration date with the current time.
    }

    /**
     * Generates a JWT token for the given user details.
     * @param userDetails The user details used to generate the token.
     * @return The generated JWT token as a String.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // A map to hold the claims.
        return createToken(claims, userDetails.getUsername()); // Creates the token with the provided claims and username.
    }

    /**
     * Creates a JWT token with the provided claims and subject (username).
     * @param claims The claims to be included in the token.
     * @param subject The subject (username) of the token.
     * @return The generated JWT token as a String.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Sets the claims.
                .setSubject(subject) // Sets the subject (username).
                .setIssuedAt(CURRENT_TIME) // Sets the token's issuance time.
                .setExpiration(EXPIRATION_TIME) // Sets the token's expiration time.
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Signs the token with the signing key and algorithm.
                .compact(); // Builds the token.
    }

    /**
     * Validates the JWT token by checking the username and expiration.
     * @param token The JWT token to be validated.
     * @param userDetails The user details to compare with the token's username.
     * @return True if the token is valid, otherwise false.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Extracts the username from the token.
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Checks if the token's username matches and the token is not expired.
    }
}
