package id.ac.unja.si.ktmscanner.common;

/**
 * Created by norman on 3/22/18.
 */

public class Url {

    private static final String IP_ADDRESS = "http://192.168.43.126";

    private static final String REAL_TIME_PATH = "/sireg/sireg-web/realtime";
    private static final String KEY_VERIFICATION_PATH = "/sireg/sireg-web/verifikasi_key";
    private static final String REGISTRATION_VERIFICATION_PATH = "/sireg/sireg-web/verifikasi_registrasi";
    private static final String NEW_MEMBER_PATH = "/sireg/sireg-web/new_member";

    public static String getRealTime() {
        return IP_ADDRESS + REAL_TIME_PATH + "/";
    }

    public static String getKeyVerification() {
        return IP_ADDRESS + KEY_VERIFICATION_PATH;
    }

    public static String getRegistrationVerification() {
        return IP_ADDRESS + REGISTRATION_VERIFICATION_PATH;
    }

    public static String getNewMember() {
        return IP_ADDRESS + NEW_MEMBER_PATH;
    }
}