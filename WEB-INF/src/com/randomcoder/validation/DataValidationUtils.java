package com.randomcoder.validation;

import java.util.Locale;

/**
 * Convenience classes to validate common data types.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 *         
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *         
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *             
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS &quot;AS IS&quot;
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public abstract class DataValidationUtils
{

	/**
	 * Returns the canonical representation of a domain name.
	 * @param domain domain nam
	 * @return domain name with 
	 */
  public static String canonicalizeDomainName(String domain) {
    if (domain == null) return null;
    domain = domain.toLowerCase(Locale.US).trim();
    if (domain.length() == 0) return null;
    return domain;
  }
	
  /**
   * Determines whether a domain name is valid or not.
   * 
   * <p>
   * <strong>NOTE:</strong> This method does not handle internationalized
   * domain names.
   * </p>
   * @param domain domain name
   * @return true if valid, false otherwise
   */
  public static boolean isValidDomainName(String domain) {
    domain = canonicalizeDomainName(domain);
    if (domain == null) return false;
    if (domain.length() > 255) return false;

    String dom = "([a-z0-9]+|([a-z0-9]+[a-z0-9\\-]*[a-z0-9]+))";
    if (!domain.matches("^(" + dom + "\\.)+" + dom + "+$")) return false;
    String[] parts = domain.split("\\.");
    for (String part : parts) if (part.length() > 67) return false;
    return true;
  }
  
  /**
   * Determines if the given string matches a domain name wildcard.
   * @param domain domain name
   * @return true if valid, false otherwise
   */
  public static boolean isValidDomainWildcard(String domain) {
    domain = canonicalizeDomainName(domain);
    if (domain == null) return false;
    if (domain.length() > 255) return false;

    // make sure domain contains only legal characters
    if (!domain.matches("^[a-z0-9\\.\\-\\*\\?]+$")) return false;
    
    // make sure each part isn't too long
    String[] parts = domain.split("\\.");
    for (String part : parts) if (part.length() > 67) return false;
    
    return true;
  }
  
  /**
   * Validates local email account names.
   * @param email email account
   * @return true if valid email address, false otherwise
   */
  public static boolean isValidLocalEmailAccount(String email) {
    if (!email.matches("^[A-Za-z0-9_\\-\\.]+$")) return false;
    if (email.length() > 64) return false;

    return true;
  }
  
  /**
   * Determines if a specified IP address is valid.
   * 
   * <p>
   * <strong>NOTE:</strong> This is IPv4 only.
   * </p>
   * @param ipAddress ip address to test
   * @return true if valid, false otherwise
   */
  public static boolean isValidIpAddress(String ipAddress) {
    if (ipAddress == null) return false;
    ipAddress = ipAddress.trim();
    if (!ipAddress.matches("^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$")) return false;
    String[] parts = ipAddress.split("\\.");
    if (parts.length != 4) return false;
    for (int i = 0; i < 4; i++) {
      int value = Integer.parseInt(parts[i]);
      if (value < 0 || value > 255) return false;
    }
    return true;
  }
  
  /**
   * Splits an email address into its local and domain parts.
   * @param email email address
   * @return String[] { local, domain }
   */
  public static String[] splitEmailAddress(String email) {
    String[] results = new String[] { null, null };
    if (email == null) return results;
    email = email.trim();
      
    // split into local-name@domain-name, taking into account escapes
    boolean quoted = false;
    int loc = -1;        
    for (int i = 0; i < email.length(); i++) {
      char c = email.charAt(i);
      if (c == '\\') {i++; continue; }            
      if (i == 0 && c == '\"') { quoted = true; continue; }
      if (c == '\"') { quoted = false; continue; }
      if (c == '@' && !quoted) { loc = i; break; }
    }
      
    if (loc >= 0) {           
      results[0] = email.substring(0, loc);
      results[1] = email.substring(loc+1); 
    } else {
      results[0] = email;
      results[1] = null;
    }
    return results;
  }
  
  /**
   * Validates email addresses according to RFC2822.
   * 
   * <p>
   * This is equivalent to calling {@link #isValidEmailAddress(String, boolean, boolean, boolean)}
   * with strict, local, and wildcard set to false.
   * </p>
   * @param email email address
   * @return true if valid email address, false otherwise.
   */
  public static boolean isValidEmailAddress(String email)
  {
  	return isValidEmailAddress(email, false, false, false);
  }
  
  /**
   * Validates email addresses according to RFC2822.
   * @param email email address
   * @param strict if name must be usable in DNS zone file
   * @param local if local addresses are to be allowed
   * @param wildcard if wildcards are to be allowed
   * @return true if valid email address, false otherwise
   */
  public static boolean isValidEmailAddress(String email, boolean strict, boolean local, boolean wildcard) {
    String[] parts = splitEmailAddress(email);
    
    email = parts[0];      
    if (email == null) return false;

    if (parts[1] == null) {
      // no domain specified
      if (!local) return false;
    } else {
      // validate domain
      if (wildcard) {
        if (!isValidDomainWildcard(parts[1]) && !isValidIpAddress(parts[1])) return false;
      } else {
        if (!isValidDomainName(parts[1]) && !isValidIpAddress(parts[1])) return false;
      }
    }
    
    // validate local-name
    
    if (strict && (email.contains(".") || email.contains("@"))) return false;        
    if (email.matches("^\\\".+\\\"$")) {
      // quoted-string
      email = email.substring(1, email.length() - 1);
      for (int i = 0; i < email.length(); i++) {
        char c = email.charAt(i);
        
        if (c == '\\') {
          // peek at next character
          i++;
          if (i >= email.length()) return false;
          c = email.charAt(i);
          
          if (c >= 1 && c <= 9) continue;
          if (c == 11) continue;
          if (c == 12) continue;
          if (c >= 14 && c <= 127) continue;            
        } else {          
          if (c >= 1 && c <= 8) continue;
          if (c == 11) continue;
          if (c == 12) continue;
          if (c >= 14 && c <= 31) continue;
          if (c >= 33 && c <= 90) continue;
          if (c >= 94 && c <= 127) continue;            
        }
        return false;
      }
      return true;
    }
    
    // replace \? with benign character to handle escapes
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < email.length(); i++) {
      char c = email.charAt(i);
      
      if (c == '\\') {
        // peek at next character
        i++;
        if (i >= email.length()) return false;
        c = email.charAt(i);
        
        if (c >= 1 && c <= 9) { buf.append("x"); continue; }
        if (c == 11) { buf.append("x"); continue; }
        if (c == 12) { buf.append("x"); continue; }
        if (c >= 14 && c <= 127) { buf.append("x"); continue; }
        
        return false; // illegal escape
      }
      
      buf.append(c);
    }
    
    email = buf.toString();
    
    // atom = alphanumeric, plus various punctuation
    String atom = "[A-Za-z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]";
    
    // either an atom, or atom(.atom)*
    String pattern = "^(" + atom + ")+(\\.(" + atom + ")+)*$";
    
    return email.matches(pattern);
  }
  
}
