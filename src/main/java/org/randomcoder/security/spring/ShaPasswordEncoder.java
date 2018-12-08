package org.randomcoder.security.spring;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaPasswordEncoder implements PasswordEncoder {

  private MessageDigest digester() {
    try {
      return MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException("Unable to get SHA-1 digest");
    }
  }

  @Override public String encode(CharSequence rawPassword) {
    return new String(Hex.encode(digester().digest(Utf8.encode(rawPassword))));
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return encode(rawPassword).equals(encodedPassword);
  }

}
