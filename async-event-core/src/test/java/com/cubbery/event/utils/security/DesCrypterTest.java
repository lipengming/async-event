package com.cubbery.event.utils.security;

import org.testng.annotations.Test;

import static com.cubbery.event.utils.security.DesCrypter.decrypt;
import static com.cubbery.event.utils.security.DesCrypter.encrypt;

public class DesCrypterTest {

    @Test
    public void testEncrypt() throws Exception {
        long start = System.nanoTime();
        String result = encrypt("{\n" +
                "\t\"certificateNo\": \"110101198804011930\",\n" +
                "\t\"systemId\": \"INTG\",\n" +
                "\t\"certificateType\": \" ID\"\n" +
                "}");
        long spend = System.nanoTime() - start;
        System.out.println(formatNS(spend) + "======" + result.length());

        System.out.println("==============================");


        start = System.nanoTime();
        result = encrypt("{\"appNo\":\"1000\",\"caller\":\"ADI\",\"ip\":\"127.0.0.1\",\"pin\":\"EV_903347\",\"signature\":\"023977c8a81df660a7ab0c5a32d151c9\",\"sysBizType\":1,\"systemNo\":\"1\",\"token\":\"12345\"}");
        spend = System.nanoTime() - start;
        System.out.println(formatNS(spend) + "======" + result.length());


        System.out.println("==============================");


        start = System.nanoTime();
        result = encrypt("{\"customerVo\":{\"id\":56,\"customerId\":\"1511702198506123795\",\"name\":\"沈英杰\",\"credentialsType\":1,\"credentialsNo\":\"511702198506123795\",\"authStatus\":1,\"authType\":12,\"authTime\":\"2016-04-06 09:50:31\",\"level\":40,\"mobile\":\"123456789\",\"status\":0,\"systemNo\":1,\"sysBizType\":1,\"pin\":\"EV_903956\",\"validFlag\":1,\"encryptId\":\"f2f086df4d6931610698131870ebdd3f\",\"createTime\":\"2016-03-21 16:40:56\",\"updateTime\":\"2016-04-06 09:59:46\",\"remark\":\"WY-SYNC\"},\"userVo\":{\"userId\":\"1_1_EV_903347\",\"systemNo\":1,\"sysBizType\":1,\"pin\":\"EV_903347\",\"credentialsType\":1,\"credentialsNo\":\"511702198506123795\",\"secScore\":2,\"secLevel\":2,\"verifyMobile\":0,\"verifyEmail\":0,\"validFlag\":1,\"status\":1,\"createTime\":\"2016-03-21 16:44:43\",\"updateTime\":\"2016-03-24 16:08:42\",\"callertoken\":\"ADI_12345\"},\"success\":true,\"responseCode\":\"00000\",\"responseMessage\":\"成功\"}");
        spend = System.nanoTime() - start;
        System.out.println(formatNS(spend) + "======" + result.length());

    }

    @Test
    public void testDecrypt() throws Exception {
        String txt = "NEM6VFWPMBZ3ZOA6ODGOKHBEZHZXAJVQFPQ6XVNM2KZKRM5JJ2M76UIDBIFUTGPA2ELYHN6AHLH6AEFBA3UMVA2WUFECNT7ZZPQJPANMMDRXSQMMAWJB4HFRONDWR3ESQMWELEDPB7CILKT3ED3M7GFFQFQ5Y4LJW2Q23YZI7GLBIKMH5RTHPKDUR24H6EPTS5KKPIH3NXHM3IQ2SHXJLYL3CWUQCHKZT6M43FYFF5PDKHRUQPLBN3MOVKG6IWMUYB6MILKLY2IKNMYCADWXLG77GL627U3Q3QVSEJWGXYHRO5PGAX2OMIVJJJUHDQGTFCNAH77HZIMXNDAU6C4XGD6LPRQJPXGSWIYSTQUCFEH4STPDD4LVYN6SUPQBPFOMQTTOD4N7QSWZD6ONDKBKY225NNYGH6GGQCFF4SPMLUDNUYPHKG3MAAU6Z5MNTVRHBHVPO3WLT2A73HH4RUZVFLI7EDZ2MDD3LKT5IPRFP35JCQ5VGI6Y72G57PHH2VDIOPVS7CHFVVTERYVYDTX6LI52UTDWTP4JDKARNRAL55F76TQEZ73MX6ZCJ6RI6LDJQB6GSEA4WEZNYFJADPQRXF2FAC6CS5XPRCGCCRYLLOWAO733Y5YCBCK4EOVUOH3TBWBJUOWJ6I6N22XUQXIJORCNKFAPMZRGNHFZE3L6VQYN3IR7ZTCBZ6HTCKUCBBSGLQIDDHFVEBFKK3HKNRB4U6K7KNEDIK63BYRJOQU3G2JNCM2TDSH33IXRWBPPBR6B6UOK3GI3WCF6X5B2KPZ7UURGLKFQ3EM74UY62EBD6M2M5KUPOZ557XOU7U2NYW3GX34LMJ57X4WL2P7STDWZ63PJSH426LQ7GQOS65RDDMIGN5RNGPVBGRAKKXKDVPEYQEI7PG4TTYCCJDBIT4MCRWOUTKX7AAPH3F4H2A3XAHKPNSRXJGQXNZVXCBCZ77EC4GXI4ENOXE7QCTC5ME6DOYBQWVIUZGK6FKSJP57PLYTD4TGHV7C4GCTPPOGTIQAJJQSG3C67MPDFP3RCUE4Q763QNO3HQQR3LDBBYFDDYWLEIKRESTKRCIBSCFOWBWS4TZI4HSOTFHRKHNKYVWTAG5TL62AJFNIW6Q67C3GOJ3ZPIX6RN2J27FMK2WATBZUSUFFVGIFA5ZV5T47XS5GPOT4YIILPJHW3JB7UAHMHUXBZGOTQT3MJRNYLM3FI37KTECIJ5AJIRQEXBASNOYY6JP4NQY5A5KYD6FZ5BK6UT7X7ZODK2EVUDYA3JHRLGYJTTL6FWYLO2LDZRNKR4HFCKTPJFWY5JJV7C3HN3XN4Q6W2S4I53OOL2CUR57YAFPB5X4XADML33YJ24VMA5LR2IP7AFPR4OWVZ6OCQTPCRWQQ7RZKUPRUFH4N3WIB4THRPPQBMXGYNR3WC75AJE34XH26KEQU3WIAE2TWWKSLIZ5SZFVEZ63SART4GBPVN67VJSGCTDW53Y2LOVBKGYESPXQJRRZKSLUXXXWF7NJOVDCE4C7NPLR4A";
        System.out.println(decrypt(txt) + "======" + txt.length());
        System.out.println("{\"flowParam\":{\"id\":37775,\"customerId\":\"360000000000178374\",\"flowId\":\"2\",\"transition\":{\"transitionId\":\"bankInfoCheck\",\"state\":-1,\"param\":{\"expressSignNum\":\"2014\",\"certificateType\":\"ID\",\"certificateNo\":\"511322199008054617\",\"validityPeriod\":\"1105\",\"name\":\"msp-t\",\"telephone\":\"123456789\",\"bankCode\":\"CGB\",\"bankCardType\":\"DE\",\"bankCardNumber\":\"11212244442555\",\"cvv\":\"12131\",\"bankCardValidityPeriod\":\"2014\"}},\"preLevel\":\"000\",\"curLevel\":\"400\"},\"certificateEntity\":{\"customerId\":\"360000000000178374\",\"realnameId\":37775,\"way\":\"2\",\"status\":1,\"flow\":\"2\",\"rootCustomerId\":\"360000000000178374\",\"flowId\":\"2\",\"certType\":\"ID\",\"certNo\":\"511322199008054617\",\"certNoEn\":\"XH34NLR4DJW2R3PS5Y2MEWVIN3SWTS5HJC77YCI\",\"certNoMd5\":\"3FA25F4DAD5A5E1E76AF19CCD804C762\",\"effectiveCertNo\":\"511322199008054617\",\"effectiveCertNoEn\":\"XH34NLR4DJW2R3PS5Y2MEWVIN3SWTS5HJC77YCI\",\"createdDate\":\"2016-04-08 14:22:53\",\"modified\":\"360000000000178374\",\"modifiedDate\":\"2016-04-08 14:22:55\"}}".length());;
    }


    /**
     * 把纳秒的输出增加千分位，方便人工读数
     * 1234567 => 1,234,567
     * @param ns 1234567
     * @return 1,234,567
     */
    public static String formatNS(long ns) {
        String src = String.valueOf(ns);
        int len = src.length();
        int count = len / 3;
        int first = len % 3;
        if (count < 1 || (count == 1 && first == 0)) {
            return src;
        }
        if (first == 0) {
            first = 3;
            count--;
        }
        StringBuilder sb = new StringBuilder(len + count);
        for (int i = 0; i < len; i++) {
            sb.append(src.charAt(i));
            if ((i+1) == first) {
                sb.append(',');
            } else if (i > first && ((i+1-first)%3) == 0 && (i+1) < len) {
                sb.append(',');
            }
        }
        String fmt = sb.toString();
        //assert fmt.length() == (len+count);
        return fmt;
    }
}