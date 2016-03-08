import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jinsihou on 15/11/3.
 */
public class Algorithmic {
    public static String getFileMD5(File lic) {
        return messageDigest(lic, "MD5");
    }

    public static String getFileSHA1(File lic) {
        return messageDigest(lic, "SHA-1");
    }

    public static String getFileSHA256(File lic) {
        return messageDigest(lic, "SHA-256");
    }

    private static String messageDigest(File lic, String algorithm) {
        try {
            byte[] target = new byte[1024];
            FileInputStream fileInputStream = new FileInputStream(lic);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            int len;
            while ((len = bufferedInputStream.read(target)) != -1) {
                messageDigest.update(target, 0, len);
            }
            byte[] result = messageDigest.digest();
            bufferedInputStream.close();
            fileInputStream.close();
            //使用BigInteger转换符号整数
            BigInteger bigInteger = new BigInteger(1, result);
            return bigInteger.toString(16).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
