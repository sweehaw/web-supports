package io.github.sweehaw.websupports.util;


import io.github.sweehaw.websupports.exception.ISOException;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author sweehaw
 */
public class ISOUtils {
    public static final String[] hexStrings = new String[256];
    /**
     * @deprecated
     */
    public static final String ENCODING = "ISO8859_1";
    public static final Charset CHARSET;
    public static final Charset EBCDIC;
    public static final byte STX = 2;
    public static final byte FS = 28;
    public static final byte US = 31;
    public static final byte RS = 29;
    public static final byte GS = 30;
    public static final byte ETX = 3;

    public ISOUtils() {
    }

    public static String ebcdicToAscii(byte[] e) {
        return EBCDIC.decode(ByteBuffer.wrap(e)).toString();
    }

    public static String ebcdicToAscii(byte[] e, int offset, int len) {
        return EBCDIC.decode(ByteBuffer.wrap(e, offset, len)).toString();
    }

    public static byte[] ebcdicToAsciiBytes(byte[] e) {
        return ebcdicToAsciiBytes(e, 0, e.length);
    }

    public static byte[] ebcdicToAsciiBytes(byte[] e, int offset, int len) {
        return ebcdicToAscii(e, offset, len).getBytes(CHARSET);
    }

    public static byte[] asciiToEbcdic(String s) {
        return EBCDIC.encode(s).array();
    }

    public static byte[] asciiToEbcdic(byte[] a) {
        return EBCDIC.encode(new String(a, CHARSET)).array();
    }

    public static void asciiToEbcdic(String s, byte[] e, int offset) {
        System.arraycopy(asciiToEbcdic(s), 0, e, offset, s.length());
    }

    public static void asciiToEbcdic(byte[] s, byte[] e, int offset) {
        asciiToEbcdic(new String(s, CHARSET), e, offset);
    }

    public static String padleft(String s, int len, char c) throws ISOException {
        s = s.trim();
        if (s.length() > len) {
            throw new ISOException("invalid len " + s.length() + "/" + len);
        } else {
            StringBuilder d = new StringBuilder(len);
            int var4 = len - s.length();

            while (var4-- > 0) {
                d.append(c);
            }

            d.append(s);
            return d.toString();
        }
    }

    public static String padright(String s, int len, char c) throws ISOException {
        s = s.trim();
        if (s.length() > len) {
            throw new ISOException("invalid len " + s.length() + "/" + len);
        } else {
            StringBuilder d = new StringBuilder(len);
            int fill = len - s.length();
            d.append(s);

            while (fill-- > 0) {
                d.append(c);
            }

            return d.toString();
        }
    }

    public static String trim(String s) {
        return s != null ? s.trim() : null;
    }

    public static String zeropad(String s, int len) throws ISOException {
        return padleft(s, len, '0');
    }

    public static String zeropad(long l, int len) {
        try {
            return padleft(Long.toString((long) ((double) l % Math.pow(10.0D, (double) len))), len, '0');
        } catch (ISOException var4) {
            return null;
        }
    }

    public static String strpad(String s, int len) {
        StringBuilder d = new StringBuilder(s);

        while (d.length() < len) {
            d.append(' ');
        }

        return d.toString();
    }

    public static String zeropadRight(String s, int len) {
        StringBuilder d = new StringBuilder(s);

        while (d.length() < len) {
            d.append('0');
        }

        return d.toString();
    }

    public static byte[] str2bcd(String s, boolean padLeft, byte[] d, int offset) {
        int len = s.length();
        int start = (len & 1) == 1 && padLeft ? 1 : 0;

        for (int i = start; i < len + start; ++i) {
            d[offset + (i >> 1)] = (byte) (d[offset + (i >> 1)] | s.charAt(i - start) - 48 << ((i & 1) == 1 ? 0 : 4));
        }

        return d;
    }

    public static byte[] str2hex(String s, boolean padLeft, byte[] d, int offset) {
        int len = s.length();
        int start = (len & 1) == 1 && padLeft ? 1 : 0;

        for (int i = start; i < len + start; ++i) {
            d[offset + (i >> 1)] = (byte) (d[offset + (i >> 1)] | Character.digit(s.charAt(i - start), 16) << ((i & 1) == 1 ? 0 : 4));
        }

        return d;
    }

    public static byte[] str2bcd(String s, boolean padLeft) {
        int len = s.length();
        byte[] d = new byte[len + 1 >> 1];
        return str2bcd(s, padLeft, d, 0);
    }

    public static byte[] str2bcd(String s, boolean padLeft, byte fill) {
        int len = s.length();
        byte[] d = new byte[len + 1 >> 1];
        Arrays.fill(d, fill);
        int start = (len & 1) == 1 && padLeft ? 1 : 0;

        for (int i = start; i < len + start; ++i) {
            d[i >> 1] = (byte) (d[i >> 1] | s.charAt(i - start) - 48 << ((i & 1) == 1 ? 0 : 4));
        }

        return d;
    }

    public static String bcd2str(byte[] b, int offset, int len, boolean padLeft) {
        StringBuilder d = new StringBuilder(len);
        int start = (len & 1) == 1 && padLeft ? 1 : 0;

        for (int i = start; i < len + start; ++i) {
            int shift = (i & 1) == 1 ? 0 : 4;
            char c = Character.forDigit(b[offset + (i >> 1)] >> shift & 15, 16);
            if (c == 100) {
                c = 61;
            }

            d.append(Character.toUpperCase(c));
        }

        return d.toString();
    }

    public static String hex2str(byte[] b, int offset, int len, boolean padLeft) {
        StringBuilder d = new StringBuilder(len);
        int start = (len & 1) == 1 && padLeft ? 1 : 0;

        for (int i = start; i < len + start; ++i) {
            int shift = (i & 1) == 1 ? 0 : 4;
            char c = Character.forDigit(b[offset + (i >> 1)] >> shift & 15, 16);
            d.append(Character.toUpperCase(c));
        }

        return d.toString();
    }

    public static String hexString(byte[] b) {
        StringBuilder d = new StringBuilder(b.length * 2);
        byte[] var2 = b;
        int var3 = b.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            byte aB = var2[var4];
            d.append(hexStrings[aB & 255]);
        }

        return d.toString();
    }

    public static String dumpString(byte[] b) {
        StringBuilder d = new StringBuilder(b.length * 2);
        byte[] var2 = b;
        int var3 = b.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            byte aB = var2[var4];
            char c = (char) aB;
            if (Character.isISOControl(c)) {
                switch (c) {
                    case '\u0000':
                        d.append("{NULL}");
                        break;
                    case '\u0001':
                        d.append("{SOH}");
                        break;
                    case '\u0002':
                        d.append("{STX}");
                        break;
                    case '\u0003':
                        d.append("{ETX}");
                        break;
                    case '\u0004':
                        d.append("{EOT}");
                        break;
                    case '\u0005':
                        d.append("{ENQ}");
                        break;
                    case '\u0006':
                        d.append("{ACK}");
                        break;
                    case '\u0007':
                        d.append("{BEL}");
                        break;
                    case '\b':
                    case '\t':
                    case '\u000b':
                    case '\f':
                    case '\u000e':
                    case '\u000f':
                    case '\u0011':
                    case '\u0012':
                    case '\u0013':
                    case '\u0014':
                    case '\u0017':
                    case '\u0018':
                    case '\u0019':
                    case '\u001a':
                    case '\u001b':
                    case '\u001d':
                    default:
                        d.append('[');
                        d.append(hexStrings[aB & 255]);
                        d.append(']');
                        break;
                    case '\n':
                        d.append("{LF}");
                        break;
                    case '\r':
                        d.append("{CR}");
                        break;
                    case '\u0010':
                        d.append("{DLE}");
                        break;
                    case '\u0015':
                        d.append("{NAK}");
                        break;
                    case '\u0016':
                        d.append("{SYN}");
                        break;
                    case '\u001c':
                        d.append("{FS}");
                        break;
                    case '\u001e':
                        d.append("{RS}");
                }
            } else {
                d.append(c);
            }
        }

        return d.toString();
    }

    public static String hexString(byte[] b, int offset, int len) {
        StringBuilder d = new StringBuilder(len * 2);
        len += offset;

        for (int i = offset; i < len; ++i) {
            d.append(hexStrings[b[i] & 255]);
        }

        return d.toString();
    }

    public static String bitSet2String(BitSet b) {
        int len = b.size();
        len = len > 128 ? 128 : len;
        StringBuilder d = new StringBuilder(len);

        for (int i = 0; i < len; ++i) {
            d.append((char) (b.get(i) ? '1' : '0'));
        }

        return d.toString();
    }

    public static byte[] bitSet2byte(BitSet b) {
        int len = b.length() + 62 >> 6 << 6;
        byte[] d = new byte[len >> 3];

        for (int i = 0; i < len; ++i) {
            if (b.get(i + 1)) {
                d[i >> 3] = (byte) (d[i >> 3] | 128 >> i % 8);
            }
        }

        if (len > 64) {
            d[0] = (byte) (d[0] | 128);
        }

        if (len > 128) {
            d[8] = (byte) (d[8] | 128);
        }

        return d;
    }

    public static byte[] bitSet2byte(BitSet b, int bytes) {
        int len = bytes * 8;
        byte[] d = new byte[bytes];

        for (int i = 0; i < len; ++i) {
            if (b.get(i + 1)) {
                d[i >> 3] = (byte) (d[i >> 3] | 128 >> i % 8);
            }
        }

        if (len > 64) {
            d[0] = (byte) (d[0] | 128);
        }

        if (len > 128) {
            d[8] = (byte) (d[8] | 128);
        }

        return d;
    }

    public static int bitSet2Int(BitSet bs) {
        int total = 0;
        int b = bs.length() - 1;
        if (b > 0) {
            int value = (int) Math.pow(2.0D, (double) b);

            for (int i = 0; i <= b; ++i) {
                if (bs.get(i)) {
                    total += value;
                }

                value >>= 1;
            }
        }

        return total;
    }

    public static BitSet int2BitSet(int value) {
        return int2BitSet(value, 0);
    }

    public static BitSet int2BitSet(int value, int offset) {
        BitSet bs = new BitSet();
        String hex = Integer.toHexString(value);
        hex2BitSet(bs, hex.getBytes(), offset);
        return bs;
    }

    public static BitSet byte2BitSet(byte[] b, int offset, boolean bitZeroMeansExtended) {
        int len = bitZeroMeansExtended ? ((b[offset] & 128) == 128 ? 128 : 64) : 64;
        BitSet bmap = new BitSet(len);

        for (int i = 0; i < len; ++i) {
            if ((b[offset + (i >> 3)] & 128 >> i % 8) > 0) {
                bmap.set(i + 1);
            }
        }

        return bmap;
    }

    public static BitSet byte2BitSet(byte[] b, int offset, int maxBits) {
        boolean b1 = (b[offset] & 128) == 128;
        boolean b65 = b.length > offset + 8 && (b[offset + 8] & 128) == 128;
        int len = maxBits > 128 && b1 && b65 ? 192 : (maxBits > 64 && b1 ? 128 : (maxBits < 64 ? maxBits : 64));
        BitSet bmap = new BitSet(len);

        for (int i = 0; i < len; ++i) {
            if ((b[offset + (i >> 3)] & 128 >> i % 8) > 0) {
                bmap.set(i + 1);
            }
        }

        return bmap;
    }

    public static BitSet byte2BitSet(BitSet bmap, byte[] b, int bitOffset) {
        int len = b.length << 3;

        for (int i = 0; i < len; ++i) {
            if ((b[i >> 3] & 128 >> i % 8) > 0) {
                bmap.set(bitOffset + i + 1);
            }
        }

        return bmap;
    }

    public static BitSet hex2BitSet(byte[] b, int offset, boolean bitZeroMeansExtended) {
        int len = bitZeroMeansExtended ? ((Character.digit((char) b[offset], 16) & 8) == 8 ? 128 : 64) : 64;
        BitSet bmap = new BitSet(len);

        for (int i = 0; i < len; ++i) {
            int digit = Character.digit((char) b[offset + (i >> 2)], 16);
            if ((digit & 8 >> i % 4) > 0) {
                bmap.set(i + 1);
            }
        }

        return bmap;
    }

    public static BitSet hex2BitSet(byte[] b, int offset, int maxBits) {
        int len = maxBits > 64 ? ((Character.digit((char) b[offset], 16) & 8) == 8 ? 128 : 64) : maxBits;
        if (len > 64 && maxBits > 128 && b.length > offset + 16 && (Character.digit((char) b[offset + 16], 16) & 8) == 8) {
            len = 192;
        }

        BitSet bmap = new BitSet(len);

        for (int i = 0; i < len; ++i) {
            int digit = Character.digit((char) b[offset + (i >> 2)], 16);
            if ((digit & 8 >> i % 4) > 0) {
                bmap.set(i + 1);
                if (i == 65 && maxBits > 128) {
                    len = 192;
                }
            }
        }

        return bmap;
    }

    public static BitSet hex2BitSet(BitSet bmap, byte[] b, int bitOffset) {
        int len = b.length << 2;

        for (int i = 0; i < len; ++i) {
            int digit = Character.digit((char) b[i >> 2], 16);
            if ((digit & 8 >> i % 4) > 0) {
                bmap.set(bitOffset + i + 1);
            }
        }

        return bmap;
    }

    public static byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];

        for (int i = 0; i < len * 2; ++i) {
            int shift = i % 2 == 1 ? 0 : 4;
            d[i >> 1] = (byte) (d[i >> 1] | Character.digit((char) b[offset + i], 16) << shift);
        }

        return d;
    }

    public static byte[] hex2byte(String s) {
        return s.length() % 2 == 0 ? hex2byte(s.getBytes(), 0, s.length() >> 1) : hex2byte("0" + s);
    }

    public static String byte2hex(byte[] bs) {
        return byte2hex(bs, 0, bs.length);
    }

    public static byte[] int2byte(int value) {
        return value < 0 ? new byte[]{(byte) (value >>> 24 & 255), (byte) (value >>> 16 & 255), (byte) (value >>> 8 & 255), (byte) (value & 255)} : (value <= 255 ? new byte[]{(byte) (value & 255)} : (value <= '\uffff' ? new byte[]{(byte) (value >>> 8 & 255), (byte) (value & 255)} : (value <= 16777215 ? new byte[]{(byte) (value >>> 16 & 255), (byte) (value >>> 8 & 255), (byte) (value & 255)} : new byte[]{(byte) (value >>> 24 & 255), (byte) (value >>> 16 & 255), (byte) (value >>> 8 & 255), (byte) (value & 255)})));
    }

    public static int byte2int(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);

            int i;
            for (i = 0; i < 4 - bytes.length; ++i) {
                byteBuffer.put((byte) 0);
            }

            for (i = 0; i < bytes.length; ++i) {
                byteBuffer.put(bytes[i]);
            }

            byteBuffer.position(0);
            return byteBuffer.getInt();
        } else {
            return 0;
        }
    }

    public static String byte2hex(byte[] bs, int off, int length) {
        if (bs.length > off && bs.length >= off + length) {
            StringBuilder sb = new StringBuilder(length * 2);
            byte2hexAppend(bs, off, length, sb);
            return sb.toString();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void byte2hexAppend(byte[] bs, int off, int length, StringBuilder sb) {
        if (bs.length > off && bs.length >= off + length) {
            sb.ensureCapacity(sb.length() + length * 2);

            for (int i = off; i < off + length; ++i) {
                sb.append(Character.forDigit(bs[i] >>> 4 & 15, 16));
                sb.append(Character.forDigit(bs[i] & 15, 16));
            }

        } else {
            throw new IllegalArgumentException();
        }
    }

    public static String formatDouble(double d, int len) {
        String prefix = Long.toString((long) d);
        String suffix = Integer.toString((int) (Math.round(d * 100.0D) % 100L));

        try {
            if (len > 3) {
                prefix = padleft(prefix, len - 3, ' ');
            }

            suffix = zeropad(suffix, 2);
        } catch (ISOException var6) {
            ;
        }

        return prefix + "." + suffix;
    }

    public static String formatAmount(long l, int len) throws ISOException {
        String buf = Long.toString(l);
        if (l < 100L) {
            buf = zeropad(buf, 3);
        }

        StringBuilder s = new StringBuilder(padleft(buf, len - 1, ' '));
        s.insert(len - 3, '.');
        return s.toString();
    }

    public static String normalize(String s, boolean canonical) {
        StringBuilder str = new StringBuilder();
        int len = s != null ? s.length() : 0;

        for (int i = 0; i < len; ++i) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\n':
                case '\r':
                    if (canonical) {
                        str.append("&#");
                        str.append(Integer.toString(ch & 255));
                        str.append(';');
                        break;
                    }
                default:
                    if (ch < 32) {
                        str.append("&#");
                        str.append(Integer.toString(ch & 255));
                        str.append(';');
                    } else if (ch > '\uff00') {
                        str.append((char) (ch & 255));
                    } else {
                        str.append(ch);
                    }
                    break;
                case '"':
                    str.append("&quot;");
                    break;
                case '&':
                    str.append("&amp;");
                    break;
                case '<':
                    str.append("&lt;");
                    break;
                case '>':
                    str.append("&gt;");
            }
        }

        return str.toString();
    }

    public static String normalize(String s) {
        return normalize(s, true);
    }

    public static String protect(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        int clear = len > 6 ? 6 : 0;
        int lastFourIndex = -1;
        if (clear > 0) {
            lastFourIndex = s.indexOf(61) - 4;
            if (lastFourIndex < 0) {
                lastFourIndex = s.indexOf(94) - 4;
            }

            if (lastFourIndex < 0 && s.indexOf(94) < 0) {
                lastFourIndex = s.indexOf(68) - 4;
            }

            if (lastFourIndex < 0) {
                lastFourIndex = len - 4;
            }
        }

        int i;
        for (i = 0; i < len; ++i) {
            if (s.charAt(i) != 61 && (s.charAt(i) != 68 || s.indexOf(94) >= 0)) {
                if (s.charAt(i) == 94) {
                    lastFourIndex = 0;
                    clear = len - i;
                } else if (i == lastFourIndex) {
                    clear = 4;
                }
            } else {
                clear = 1;
            }

            sb.append(clear-- > 0 ? s.charAt(i) : '_');
        }

        s = sb.toString();

        try {
            i = s.replaceAll("[^\\^]", "").length();
            if (i == 2) {
                s = s.substring(0, s.lastIndexOf("^") + 1);
                s = padright(s, len, '_');
            }
        } catch (ISOException var6) {
            ;
        }

        return s;
    }

    public static int[] toIntArray(String s) {
        StringTokenizer st = new StringTokenizer(s);
        int[] array = new int[st.countTokens()];

        for (int i = 0; st.hasMoreTokens(); ++i) {
            array[i] = Integer.parseInt(st.nextToken());
        }

        return array;
    }

    public static String[] toStringArray(String s) {
        StringTokenizer st = new StringTokenizer(s);
        String[] array = new String[st.countTokens()];

        for (int i = 0; st.hasMoreTokens(); ++i) {
            array[i] = st.nextToken();
        }

        return array;
    }

    public static byte[] xor(byte[] op1, byte[] op2) {
        byte[] result;
        if (op2.length > op1.length) {
            result = new byte[op1.length];
        } else {
            result = new byte[op2.length];
        }

        for (int i = 0; i < result.length; ++i) {
            result[i] = (byte) (op1[i] ^ op2[i]);
        }

        return result;
    }

    public static String hexor(String op1, String op2) {
        byte[] xor = xor(hex2byte(op1), hex2byte(op2));
        return hexString(xor);
    }

    public static byte[] trim(byte[] array, int length) {
        byte[] trimmedArray = new byte[length];
        System.arraycopy(array, 0, trimmedArray, 0, length);
        return trimmedArray;
    }

    public static byte[] concat(byte[] array1, byte[] array2) {
        byte[] concatArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, concatArray, 0, array1.length);
        System.arraycopy(array2, 0, concatArray, array1.length, array2.length);
        return concatArray;
    }

    public static byte[] concat(byte[] array1, int beginIndex1, int length1, byte[] array2, int beginIndex2, int length2) {
        byte[] concatArray = new byte[length1 + length2];
        System.arraycopy(array1, beginIndex1, concatArray, 0, length1);
        System.arraycopy(array2, beginIndex2, concatArray, length1, length2);
        return concatArray;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException var3) {
            ;
        }

    }

    public static String zeroUnPad(String s) {
        return unPadLeft(s, '0');
    }

    public static String blankUnPad(String s) {
        return unPadRight(s, ' ');
    }

    public static String unPadRight(String s, char c) {
        int end = s.length();
        if (end == 0) {
            return s;
        } else {
            while (0 < end && s.charAt(end - 1) == c) {
                --end;
            }

            return 0 < end ? s.substring(0, end) : s.substring(0, 1);
        }
    }

    public static String unPadLeft(String s, char c) {
        int fill = 0;
        int end = s.length();
        if (end == 0) {
            return s;
        } else {
            while (fill < end && s.charAt(fill) == c) {
                ++fill;
            }

            return fill < end ? s.substring(fill, end) : s.substring(fill - 1, end);
        }
    }

    public static boolean isZero(String s) {
        int i = 0;

        int len;
        for (len = s.length(); i < len && s.charAt(i) == 48; ++i) {
            ;
        }

        return i >= len;
    }

    public static boolean isBlank(String s) {
        return s.trim().length() == 0;
    }

    public static boolean isAlphaNumeric(String s) {
        int i = 0;

        int len;
        for (len = s.length(); i < len && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == 32 || s.charAt(i) == 46 || s.charAt(i) == 45 || s.charAt(i) == 95) || s.charAt(i) == 63; ++i) {
            ;
        }

        return i >= len;
    }

    public static boolean isNumeric(String s, int radix) {
        int i = 0;

        int len;
        for (len = s.length(); i < len && Character.digit(s.charAt(i), radix) > -1; ++i) {
            ;
        }

        return i >= len && len > 0;
    }

    public static byte[] bitSet2extendedByte(BitSet b) {
        int len = 128;
        byte[] d = new byte[len >> 3];

        for (int i = 0; i < len; ++i) {
            if (b.get(i + 1)) {
                d[i >> 3] = (byte) (d[i >> 3] | 128 >> i % 8);
            }
        }

        d[0] = (byte) (d[0] | 128);
        return d;
    }

    private static String hexOffset(int i) {
        i = i >> 4 << 4;
        int w = i > '\uffff' ? 8 : 4;

        try {
            return zeropad(Integer.toString(i, 16), w);
        } catch (ISOException var3) {
            return var3.getMessage();
        }
    }

    public static String hexdump(byte[] b) {
        return hexdump(b, 0, b.length);
    }

    public static String hexdump(byte[] b, int offset) {
        return hexdump(b, offset, b.length - offset);
    }

    public static String hexdump(byte[] b, int offset, int len) {
        StringBuilder sb = new StringBuilder();
        StringBuilder hex = new StringBuilder();
        StringBuilder ascii = new StringBuilder();
        String sep = "  ";
        String lineSep = System.getProperty("line.separator");
        len += offset;

        for (int i = offset; i < len; ++i) {
            hex.append(hexStrings[b[i] & 255]);
            hex.append(' ');
            char c = (char) b[i];
            ascii.append(c >= 32 && c < 127 ? c : '.');
            int j = i % 16;
            switch (j) {
                case 7:
                    hex.append(' ');
                    break;
                case 15:
                    sb.append(hexOffset(i));
                    sb.append(sep);
                    sb.append(hex.toString());
                    sb.append(' ');
                    sb.append(ascii.toString());
                    sb.append(lineSep);
                    hex = new StringBuilder();
                    ascii = new StringBuilder();
            }
        }

        if (hex.length() > 0) {
            while (hex.length() < 49) {
                hex.append(' ');
            }

            sb.append(hexOffset(len));
            sb.append(sep);
            sb.append(hex.toString());
            sb.append(' ');
            sb.append(ascii.toString());
            sb.append(lineSep);
        }

        return sb.toString();
    }

    public static String strpadf(String s, int len) {
        StringBuilder d = new StringBuilder(s);

        while (d.length() < len) {
            d.append('F');
        }

        return d.toString();
    }

    public static String trimf(String s) {
        if (s != null) {
            int l = s.length();
            if (l > 0) {
                do {
                    --l;
                } while (l >= 0 && s.charAt(l) == 70);

                s = l == 0 ? "" : s.substring(0, l + 1);
            }
        }

        return s;
    }

    public static String takeLastN(String s, int n) throws ISOException {
        return s.length() > n ? s.substring(s.length() - n) : (s.length() < n ? zeropad(s, n) : s);
    }

    public static String takeFirstN(String s, int n) throws ISOException {
        return s.length() > n ? s.substring(0, n) : (s.length() < n ? zeropad(s, n) : s);
    }

    public static String millisToString(long millis) {
        StringBuilder sb = new StringBuilder();
        if (millis < 0L) {
            millis = -millis;
            sb.append('-');
        }

        int ms = (int) (millis % 1000L);
        millis /= 1000L;
        int dd = (int) (millis / 86400L);
        millis -= (long) (dd * 86400);
        int hh = (int) (millis / 3600L);
        millis -= (long) (hh * 3600);
        int mm = (int) (millis / 60L);
        millis -= (long) (mm * 60);
        int ss = (int) millis;
        if (dd > 0) {
            sb.append(Long.toString((long) dd));
            sb.append("d ");
        }

        sb.append(zeropad((long) hh, 2));
        sb.append(':');
        sb.append(zeropad((long) mm, 2));
        sb.append(':');
        sb.append(zeropad((long) ss, 2));
        sb.append('.');
        sb.append(zeropad((long) ms, 3));
        return sb.toString();
    }

    public static String formatAmountConversionRate(double convRate) throws ISOException {
        if (convRate == 0.0D) {
            return null;
        } else {
            BigDecimal cr = new BigDecimal(convRate);
            int x = 7 - cr.precision() + cr.scale();
            String bds = cr.movePointRight(cr.scale()).toString();
            if (x > 9) {
                bds = zeropad(bds, bds.length() + x - 9);
            }

            String ret = zeropadRight(bds, 7);
            return Math.min(9, x) + takeFirstN(ret, 7);
        }
    }

    public static double parseAmountConversionRate(String convRate) {
        if (convRate != null && convRate.length() == 8) {
            BigDecimal bd = new BigDecimal(convRate);
            int pow = bd.movePointLeft(7).intValue();
            bd = new BigDecimal(convRate.substring(1));
            return bd.movePointLeft(pow).doubleValue();
        } else {
            throw new IllegalArgumentException("Invalid amount converion rate argument: '" + convRate + "'");
        }
    }

    public static String commaEncode(String[] ss) {
        StringBuilder sb = new StringBuilder();
        String[] var2 = ss;
        int var3 = ss.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            if (sb.length() > 0) {
                sb.append(',');
            }

            if (s != null) {
                int i = 0;

                while (i < s.length()) {
                    char c = s.charAt(i);
                    switch (c) {
                        case ',':
                        case '\\':
                            sb.append('\\');
                        default:
                            sb.append(c);
                            ++i;
                    }
                }
            }
        }

        return sb.toString();
    }

    public static String[] commaDecode(String s) {
        List<String> l = new ArrayList();
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (!escaped) {
                switch (c) {
                    case ',':
                        l.add(sb.toString());
                        sb = new StringBuilder();
                        continue;
                    case '\\':
                        escaped = true;
                        continue;
                }
            }

            sb.append(c);
            escaped = false;
        }

        if (sb.length() > 0) {
            l.add(sb.toString());
        }

        return (String[]) l.toArray(new String[l.size()]);
    }

    public static String commaDecode(String s, int i) {
        String[] ss = commaDecode(s);
        int l = ss.length;
        return i >= 0 && i < l ? ss[i] : null;
    }

    public static char calcLUHN(String p) {
        int odd = p.length() % 2;
        int crc = 0;

        for (int i = 0; i < p.length(); ++i) {
            char c = p.charAt(i);
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException("Invalid PAN " + p);
            }

            c = (char) (c - 48);
            if (i % 2 != odd) {
                crc += c * 2 >= 10 ? c * 2 - 9 : c * 2;
            } else {
                crc += c;
            }
        }

        return (char) ((crc % 10 == 0 ? 0 : 10 - crc % 10) + 48);
    }

    public static String getRandomDigits(Random r, int l, int radix) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < l; ++i) {
            sb.append(r.nextInt(radix));
        }

        return sb.toString();
    }

    public static String readableFileSize(long size) {
        if (size <= 0L) {
            return "0";
        } else {
            String[] units = new String[]{"Bi", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB"};
            int digitGroups = (int) (Math.log10((double) size) / Math.log10(1024.0D));
            return (new DecimalFormat("#,##0.#")).format((double) size / Math.pow(1024.0D, (double) digitGroups)) + " " + units[digitGroups];
        }
    }

    static {
        for (int i = 0; i < 256; ++i) {
            StringBuilder d = new StringBuilder(2);
            char ch = Character.forDigit((byte) i >> 4 & 15, 16);
            d.append(Character.toUpperCase(ch));
            ch = Character.forDigit((byte) i & 15, 16);
            d.append(Character.toUpperCase(ch));
            hexStrings[i] = d.toString();
        }

        CHARSET = StandardCharsets.ISO_8859_1;
        EBCDIC = Charset.forName("IBM1047");
    }
}
