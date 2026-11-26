package pe.edu.upeu.perfumeria.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/** Hash SHA-256 con sal aleatoria. Formato guardado: sal:hash (ambos en Base64). */
public class PasswordUtil {

    public static String hash(String plano) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal) + ":" + digest(sal, plano);
    }

    public static boolean verificar(String plano, String guardado) {
        if (plano == null || guardado == null || !guardado.contains(":")) {
            return false;
        }
        String[] partes = guardado.split(":", 2);
        byte[] sal = Base64.getDecoder().decode(partes[0]);
        return digest(sal, plano).equals(partes[1]);
    }

    private static String digest(byte[] sal, String plano) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sal);
            return Base64.getEncoder().encodeToString(md.digest(plano.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
