package it.hackerinside.etk.GUI;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A password cache that stores passwords in memory for a limited time.
 * The cache automatically expires entries after a configurable time period.
 *
 * <p><b>IMPORTANT SECURITY NOTE:</b>
 * This implementation uses XOR obfuscation with a random key, which provides only minimal
 * protection against casual inspection. This is NOT encryption and does NOT provide strong
 * security guarantees.
 *
 * <p>It makes no sense to use a real cipher as there is no way to protect the encryption key
 * without proper hardware (TEE/Secure Enclave), which is the reason for using the XOR cipher.
 *
 * Thread-safety notes:
 * - ConcurrentHashMap is used for the cache data structure.
 * - A ReentrantLock protects the lifecycle of the scheduler (cold start / stop).
 *
 * @author Francesco Valentini
 */
public class PasswordCache {
    /**
     * Record representing a password entry in the cache
     * @param password byte array containing the obfuscated password
     * @param eta timestamp when the entry was created
     */
    private record PasswordEntry(
        byte[] password,
        long eta
    ){
        /**
         * Performs zeroization of the password data by overwriting with zeros
         */
        public void zeroize(){
            if (this.password != null) {
                Arrays.fill(this.password, (byte) 0x00);
            }
        }
    }
    
    private final Map<String, PasswordEntry> cache;
    private final long maxEtaMillis;
    private final byte[] obfuscationKey;
    
    // Scheduler management (recreated during cold-start)
    private ScheduledExecutorService scheduler;
    // Lock protecting scheduler lifecycle and schedulerActive flag
    private final ReentrantLock schedulerLock = new ReentrantLock();
    // Volatile flag to allow cheap reads of scheduler state
    private volatile boolean schedulerActive = false;
    
    /**
     * Constructs a new PasswordCache with the specified maximum entry lifetime
     *
     * @param maxEtaSeconds the maximum time in seconds that entries should remain in the cache
     * @throws IllegalArgumentException if maxEtaSeconds is negative
     */
    public PasswordCache(long maxEtaSeconds){
        if (maxEtaSeconds < 0) {
            throw new IllegalArgumentException("maxEtaSeconds cannot be negative");
        }

        SecureRandom rnd = new SecureRandom();
        this.obfuscationKey = new byte[512];
        rnd.nextBytes(obfuscationKey);

        this.cache = new ConcurrentHashMap<>();
        this.maxEtaMillis = maxEtaSeconds * 1000L;

        // Start scheduler initially (unless we prefer lazy start - here we start immediately)
        long periodSeconds = Math.max(1L, (maxEtaMillis / 1000L));
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "PasswordCache-cleaner");
            t.setDaemon(true);
            return t;
        });
        this.scheduler.scheduleAtFixedRate(
        		this::checkExpiredRecords,
                periodSeconds,
                periodSeconds,
                TimeUnit.SECONDS
        );
        this.schedulerActive = true;
    }
    /**
     * Performs in-place XOR operation on data with a mask
     *
     * @param data the data to be XORed (modified in-place)
     * @param mask the mask to XOR with
     */
    private void xor(byte[] data, byte[] mask) {
        final int m = mask.length;
        for (int i = 0; i < data.length; i++) {
            data[i] ^= mask[i % m];
        }
    }

    /**
     * Obfuscates a character array using XOR with the obfuscation key
     *
     * @param password the password characters to obfuscate
     * @return the obfuscated byte array
     * @throws RuntimeException if encoding fails
     */
    private byte[] obfuscate(char[] password) {
        byte[] plain = null;
        try {
            plain = encodeUtf8(password);
            xor(plain, obfuscationKey);
            return plain; // Caller/PasswordEntry is responsible for zeroization
        } catch (Exception e) {
            if (plain != null) Arrays.fill(plain, (byte)0);
            throw new RuntimeException("Failed to obfuscate password", e);
        }
    }

    /**
     * Deobfuscates a byte array back to character array
     *
     * @param masked the obfuscated byte array
     * @return the deobfuscated character array, or null if input is null
     * @throws RuntimeException if decoding fails
     */
    private char[] deobfuscate(byte[] masked) {
        if (masked == null) return null;
        byte[] tmp = Arrays.copyOf(masked, masked.length);
        try {
            xor(tmp, obfuscationKey);
            char[] result = decodeUtf8(tmp);
            return result;
        } finally {
            Arrays.fill(tmp, (byte) 0);
        }
    }

    /**
     * Converts UTF-8 encoded byte array to character array without creating permanent String objects
     *
     * @param bytes the UTF-8 encoded bytes to decode
     * @return the decoded character array
     * @throws RuntimeException if character decoding fails
     */
    private static char[] decodeUtf8(byte[] bytes) {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        try {
            CharBuffer cb = decoder.decode(bb);
            char[] out = new char[cb.remaining()];
            cb.get(out);
            // Zero CharBuffer backing array if present
            if (cb.hasArray()) {
                Arrays.fill(cb.array(), '\0');
            }
            return out;
        } catch (CharacterCodingException e) {
            throw new RuntimeException("UTF-8 decoding error", e);
        } finally {
            // Zero temporary byte buffer content
            if (bb.hasArray()) {
                Arrays.fill(bb.array(), (byte) 0);
            }
        }
    }

    /**
     * Converts character array to UTF-8 byte array without creating permanent String objects
     *
     * @param chars the character array to encode
     * @return the UTF-8 encoded byte array
     * @throws RuntimeException if character encoding fails
     */
    private static byte[] encodeUtf8(char[] chars) {
        CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
        CharBuffer cbuf = CharBuffer.wrap(chars);
        try {
            // Estimate size: maximum bytes per character
            int estimated = (int) Math.ceil(chars.length * encoder.maxBytesPerChar());
            ByteBuffer bb = ByteBuffer.allocate(estimated);
            CoderResult cr = encoder.encode(cbuf, bb, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            cr = encoder.flush(bb);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            bb.flip();
            byte[] out = new byte[bb.remaining()];
            bb.get(out);
            // Zero unused temporary buffer space
            if (bb.hasArray()) {
                Arrays.fill(bb.array(), (byte) 0);
            }
            return out;
        } catch (CharacterCodingException e) {
            throw new RuntimeException("UTF-8 encoding error", e);
        }
    }

    /**
     * Ensures the scheduled cleaner is running. If it was stopped (cold), this starts a new one.
     * Protected by schedulerLock.
     */
    private void ensureSchedulerRunning() {
        schedulerLock.lock();
        try {
            if (schedulerActive) return;
            long periodSeconds = Math.max(1L, (maxEtaMillis / 1000L));
            this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "PasswordCache-cleaner");
                t.setDaemon(true);
                return t;
            });
            this.scheduler.scheduleAtFixedRate(this::checkExpiredRecords, periodSeconds, periodSeconds, TimeUnit.SECONDS);
            this.schedulerActive = true;
        } finally {
            schedulerLock.unlock();
        }
    }

    /**
     * Checks for and removes expired records from the cache
     * This method is invoked periodically by the scheduler thread
     */
    private void checkExpiredRecords() {
        long now = System.currentTimeMillis();
        // Atomic per-key removal + zeroization
        for (String key : cache.keySet()) {
            cache.computeIfPresent(key, (k, pe) -> {
                if (maxEtaMillis > 0 && now - pe.eta() > maxEtaMillis) {
                    // zeroize while holding the reference, then return null to remove
                    pe.zeroize();
                    return null; // removes mapping atomically
                }
                return pe;
            });
        }

        // Try to stop scheduler only while holding schedulerLock and re-check emptiness inside lock
        schedulerLock.lock();
        try {
            if (cache.isEmpty() && schedulerActive && scheduler != null) {
                // rotate/overwrite key before stopping (optional)
            	SecureRandom rnd = new SecureRandom();
            	rnd.nextBytes(obfuscationKey);

                scheduler.shutdown();
                schedulerActive = false;
                scheduler = null;
            }
        } finally {
            schedulerLock.unlock();
        }
    }

    /**
     * Stores a password in the cache
     *
     * @param key the unique identifier for the password entry
     * @param password the password characters to store
     * @return the same password reference that was passed in
     * @throws IllegalArgumentException if key or password is null
     */
    public char[] set(String key, char[] password){
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        // Cold start: restart scheduler if it was stopped
        ensureSchedulerRunning();
        
        this.cache.put(key, new PasswordEntry(obfuscate(password), System.currentTimeMillis()));


        return password;
    }
    
    /**
     * Removes a password in the cache
     *
     * @param key the unique identifier for the password entry
     */
    public void remove(String key) {
    	if (!this.cache.containsKey(key)) return;
    	
    	this.cache.get(key).zeroize();
    	this.cache.remove(key);
    }

    /**
     * Retrieves a password from the cache
     *
     * @param key the unique identifier for the password entry
     * @return the password characters if found and not expired, null otherwise
     * @throws IllegalArgumentException if key is null
     */
    public char[] get(String key){
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        PasswordEntry e = cache.get(key);
        if (e == null) return null;

        // Check if entry has expired
        if (maxEtaMillis > 0 && System.currentTimeMillis() - e.eta() > maxEtaMillis) {
            cache.remove(key);
            e.zeroize();
            return null;
        }

        return deobfuscate(e.password);
    }

    /**
     * Securely destroys all sensitive data in the cache and shuts down the scheduler
     */
    public void zeroize(){
        // Stop scheduler and await termination (with a timeout) in a thread-safe way
        schedulerLock.lock();
        try {
            if (scheduler != null) {
                scheduler.shutdownNow();
                try {
                    // Wait briefly for termination
                    if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                        // best-effort; continue with zeroization
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            schedulerActive = false;
            scheduler = null;
        } finally {
            schedulerLock.unlock();
        }
        
        // Zero the obfuscation key
        Arrays.fill(obfuscationKey, (byte) 0);

        // Zeroize and clear cache
        for (PasswordEntry record : cache.values()) {
            if (record != null) record.zeroize();
        }
        cache.clear();
    }
}
