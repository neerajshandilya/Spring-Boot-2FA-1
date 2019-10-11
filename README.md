# A Stripped-Down Example of TOTP 2FA with Spring Boot

This example uses Austin Delamar's JOTP: https://github.com/amdelamar/jotp
I found it to be the easiest to use, but others should produce the same results.

Run the app with `./mvnw spring-boot:run`

## Changes made over the basic login flow

[TOTP-based 2FA](https://en.wikipedia.org/wiki/Time-based_One-time_Password_algorithm) works like this:
 - Each user needs a **secret key** which is generated when they create their account.
 - After the account is created, the key is shared with the user in the form of a QR code which creates an account in their authenticator app.
 - When a user tries to log in they have to provide the **6-digit number** from their app, which is calculated from the **secret** and the **current time**.
 - The _same_ calculation happens server-side to verify that the user possesses the secret key.

## Changes made, in detail

The version of this app which does not have 2FA is in this repo, tagged [without-2fa]().
The 2FA-enabled version (ie _this_ version) is in the [totp-2fa]() tag

### Account Registration
1. Add a `superSecretSecret` field to `UserDto`. [[source]](https://github.com/mjg123/Spring-Boot-2FA/blob/totp-2fa/src/main/java/lol/gilliard/springboot2fa/UserDto.java#L25)
2. Set the `superSecretSecret` in `UserService#createNewUser` by calling `OTP.randomBase32(20)`. [[source]](https://github.com/mjg123/Spring-Boot-2FA/blob/totp-2fa/src/main/java/lol/gilliard/springboot2fa/UserService.java#L30-L32)
3. Add steps to the account creation flow, with a new page showing the QR code, and configuration in `WebController`. [[source]](https://github.com/mjg123/Spring-Boot-2FA/blob/totp-2fa/src/main/java/lol/gilliard/springboot2fa/WebController.java#L67-L85) [[template]](https://github.com/mjg123/Spring-Boot-2FA/blob/master/src/main/resources/templates/2faReg.html)

### User Login
4. Create `TwoFAAuthenticationProvider` extends `DaoAuthenticationProvider`, which checks the auth details in the `authenticate` method. [[source]](https://github.com/mjg123/Spring-Boot-2FA/blob/totp-2fa/src/main/java/lol/gilliard/springboot2fa/TwoFAAuthenticationProvider.java#L26-L49)
5. Create a `TwoFAAuthenticationDetailsSource`, which implements `AuthenticationDetailsSource`, essentially just a factory for the above. [[source]](https://github.com/mjg123/Spring-Boot-2FA/blob/totp-2fa/src/main/java/lol/gilliard/springboot2fa/TwoFAAuthenticationDetailsSource.java#L20-L23)
6. Add that into `WebSecurityConfig` (which extends `WebSecurityConfigurerAdapter` and is annotated `@Configuraion` and `@EnableWebSecurity`). [[source]](https://github.com/mjg123/Spring-Boot-2FA/blob/totp-2fa/src/main/java/lol/gilliard/springboot2fa/WebSecurityConfig.java#L30)
7. Make sure to require `.authenticated()` on any resources that need a logged-in user. [[source]](https://github.com/mjg123/Spring-Boot-2FA/blob/totp-2fa/src/main/java/lol/gilliard/springboot2fa/WebSecurityConfig.java#L26)
