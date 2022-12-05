package org.randomcoder.website.bo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.dao.RoleDao;
import org.randomcoder.website.dao.UserDao;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Role;
import org.randomcoder.website.data.User;
import org.randomcoder.website.data.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@Singleton
public class UserBusinessImpl implements UserBusiness {

    private static final Logger logger = LoggerFactory.getLogger(UserBusinessImpl.class);

    private static final int RANDOM_VERSION = 1;
    private static final long TOKEN_MAX_AGE_MS = Duration.ofHours(25).toSeconds() * 1000;
    private static final long TOKEN_SLEW_MS = Duration.ofMinutes(10).toSeconds() * 1000;

    private final RandomGenerator.SplittableGenerator random;

    private final byte[] randomKey;

    @Inject
    RoleDao roleDao;

    @Inject
    UserDao userDao;

    public UserBusinessImpl() throws Exception {
        var secure = SecureRandom.getInstanceStrong();
        randomKey = secure.generateSeed(64);
        var splitSeed = secure.nextLong();
        random = RandomGeneratorFactory.<RandomGenerator.SplittableGenerator>of("L128X256MixRandom")
                .create(splitSeed);
    }

    @Override
    public void changePassword(String userName, String password) {
        userDao.changePassword(userName, User.hashPassword(password));
    }

    @Override
    public void createUser(Consumer<User> visitor) {
        User user = new User();
        visitor.accept(user);
        userDao.save(user);
    }

    @Override
    public void updateUser(Consumer<User> visitor, Long userId) {
        User user = loadUser(userId);
        visitor.accept(user);
        userDao.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userDao.deleteById(userId);
    }

    @Override
    public void loadUserForEditing(Consumer<User> consumer, Long userId) {
        User user = loadUser(userId);
        consumer.accept(user);
    }

    private User loadUser(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Override
    public String generateAuthToken(User user) {
        String username = user.getUserName();
        if (username.length() > 255) {
            throw new IllegalArgumentException("Can't handle usernames > 255 bytes");
        }

        long seed = random.split().nextLong();
        long loginTime = System.currentTimeMillis();
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);

        byte[] digestBytes = hashToken(randomKey, seed, loginTime, usernameBytes);

        try (var bos = new ByteArrayOutputStream()) {
            try (var dos = new DataOutputStream(bos)) {
                dos.writeInt(RANDOM_VERSION); // algorithm version
                dos.writeInt(digestBytes.length); // digest length
                dos.write(digestBytes); // digest
                dos.writeLong(seed); // seed
                dos.writeLong(loginTime); // login time
                dos.writeInt(username.length()); // username length
                dos.write(username.getBytes(StandardCharsets.UTF_8)); // username
            }
            return Base64.getUrlEncoder().encodeToString(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error generating authentication information", e);
        }
    }

    @Override
    public User validateAuthToken(String securityToken) {
        if (securityToken == null) {
            logger.info("Got null token");
            return null;
        }

        String username;
        try {
            var bytes = Base64.getUrlDecoder().decode(securityToken);
            try (var bis = new ByteArrayInputStream(bytes)) {
                try (var dis = new DataInputStream(bis)) {
                    int version = dis.readInt();
                    if (version != RANDOM_VERSION) {
                        throw new IllegalArgumentException(String.format(
                                "Unsupported token version: %s", version));
                    }
                    int digestLength = dis.readInt();
                    if (digestLength > 1024) {
                        throw new IllegalArgumentException(String.format("Unsupported digest length: %d", digestLength));
                    }
                    byte[] digestBytes = dis.readNBytes(digestLength);
                    if (digestLength != digestBytes.length) {
                        throw new IllegalArgumentException(String.format(
                                "Expected username read of %d bytes, got %d", digestLength, digestBytes.length));
                    }

                    long seed = dis.readLong(); // seed
                    long loginTime = dis.readLong(); // login time

                    int usernameLength = dis.readInt();
                    byte[] usernameBytes = dis.readNBytes(usernameLength);
                    if (usernameLength != usernameBytes.length) {
                        throw new IllegalArgumentException(String.format(
                                "Expected username read of %d bytes, got %d", usernameLength, usernameBytes.length));
                    }
                    username = new String(usernameBytes, StandardCharsets.UTF_8);

                    // calculate digest
                    byte[] expectedDigestBytes = hashToken(randomKey, seed, loginTime, usernameBytes);

                    if (!Arrays.equals(digestBytes, expectedDigestBytes)) {
                        logger.debug("Token failed to validate");
                        return null;
                    }

                    long now = System.currentTimeMillis();
                    long notBefore = loginTime - TOKEN_SLEW_MS;
                    long notAfter = loginTime + TOKEN_MAX_AGE_MS;

                    if (now < notBefore || now > notAfter) {
                        logger.debug("Token expired: {}", Instant.ofEpochMilli(loginTime));
                        return null; // not a parse exception
                    }
                }
            }
        } catch (Exception e) {
            logger.info("Failed to parse token", e);
            return null;
        }

        // mark as logged in
        userDao.updateLoginTime(username);

        User user = userDao.findByName(username, false);
        if (user == null) {
            logger.info("Decoded token for nonexistent user {}", username);
            return null;
        }

        return user;
    }

    @Override
    public List<Role> listRoles() {
        return roleDao.listByDescription();
    }

    @Override
    public Role findRoleByName(String name) {
        return roleDao.findByName(name);
    }

    @Override
    public User findUserByName(String name) {
        return userDao.findByName(name, true);
    }

    @Override
    public User findUserByNameEnabled(String name) {
        return userDao.findByName(name, false);
    }

    @Override
    public Page<User> findAll(long offset, long length) {
        return userDao.listByName(offset, length);
    }

    @Override
    public void auditUsernamePasswordLogin(String userName) {
        userDao.updateLoginTime(userName);
    }

    private static MessageDigest sha512() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] hashToken(byte[] randomKey, long seed, long loginTime, byte[] usernameBytes) {
        var digest = sha512();
        digest.update(randomKey); // private key
        ByteBuffer buf = ByteBuffer.allocate(16 + usernameBytes.length);
        buf.putLong(seed);
        buf.putLong(loginTime);
        buf.put(usernameBytes);
        byte[] encoded = buf.array();
        digest.update(encoded);
        byte[] digestBytes = digest.digest();
        return digestBytes;
    }

}
