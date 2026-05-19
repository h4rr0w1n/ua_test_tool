/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ANDH
 */
public class MessageUtils {

    private static final Logger logger = LoggerFactory.getLogger(MessageUtils.class);
    // private static final MLogger logger = MLogger.getLogger(MessageUtils.class);
    private static final SimpleDateFormat utcTime = new SimpleDateFormat("yyMMddHHmmss'Z'");
    private static final SimpleDateFormat localTime = new SimpleDateFormat("yyMMddHHmmssZ");
    private static final TimeZone utc = TimeZone.getTimeZone("UTC");

    private static final Map line01StartKeywords = new HashMap<String, String>();
    private static final Map line01IncludeKeywords = new HashMap<String, String>();

    public static void initialize() {
        utcTime.setTimeZone(utc);
        localTime.setTimeZone(utc);

//         final Map line01StartKeywords = new HashMap<String, String>();
//        line01StartKeywords.put("(FPL", "FPL"); // 12: FPL
//        line01StartKeywords.put("(DLA", "DLA"); // 13: DLA
//        line01StartKeywords.put("(CHG", "CHG"); // 14: CHG
//        line01StartKeywords.put("(ARR", "ARR"); // 15: ARR
//        line01StartKeywords.put("(DEP", "DEP"); // 16: DEP
//        line01StartKeywords.put("(ARR", "ARR"); // 17: ARR
//        line01StartKeywords.put("(EST", "EST"); // 19: EST
//        line01StartKeywords.put("(RCF", "RCF"); // 23: RCF
//        line01StartKeywords.put("(SPL", "SPL"); // 24: SPL
//        line01StartKeywords.put("(RQS", "RQS"); // 25: RQS
//        line01StartKeywords.put("(RQP", "RQP"); // 26: RQP
        line01StartKeywords.put("(AFP", "AFP");//11: AFP
        line01StartKeywords.put("AFP", "AFP");//11: AFP
        line01StartKeywords.put("(FPL", "FPL");//12: FPL
        line01StartKeywords.put("(DLA", "DLA");   //13: DLA
        line01StartKeywords.put("(CHG", "CHG");   //14: CHG
        line01StartKeywords.put("(CNL", "CNL");  //15: CNL
        line01StartKeywords.put("(DEP", "DEP");  //16: DEP
        line01StartKeywords.put("(ARR", "ARR");  //17: ARR
        line01StartKeywords.put("(CPL", "CPL");   //18: CPL
        line01StartKeywords.put("(EST", "EST");   //19: EST
        line01StartKeywords.put("(CDN", "CDN");  //20: CDN
        line01StartKeywords.put("(ACP", "ACP");   //21: ACP
        line01StartKeywords.put("(ALR", "ALR");   //22: ALR
        line01StartKeywords.put("(RCF", "RCF");   //23: RCF
        line01StartKeywords.put("(SPL", "SPL");  //24: SPL
        line01StartKeywords.put("(RQS", "RQS");   //25: RQS
        line01StartKeywords.put("(RQP", "RQP");   //26: RQP
        line01StartKeywords.put("RQM", "RQM");   //26: RQP
        line01StartKeywords.put("(RCVV", "RCR");   //27: RCR

        line01IncludeKeywords.put(" NOTAMR", "NOTAM"); // 08: NOTAM
        line01IncludeKeywords.put(" NOTAMC", "NOTAM"); // 08: NOTAM
        line01IncludeKeywords.put(" NOTAMN", "NOTAM"); // 08: NOTAM

    }

    public static String getCategory(String message) {

        /*
            12: FPL
            13: DLA
            14: CHG
            15: ARR
            16: DEP
            17: ARR
            19: EST
            23: RCF
            24: SPL
            25: RQS
            26: RQP
         */
        if (message == null || message.isEmpty()) {
            return "";
        }

        final String[] lines = message.split("\r\n");
        if (lines.length == 0) {
            return "";
        }

        String line01 = lines[0].trim();
        if (line01 == null || line01.isEmpty()) {
            return "";
        }

        if ((message.contains("(FPL-ON:")) | (message.contains("FPL ON:"))) {
            return "KHBN";
        }

        Iterator it = line01StartKeywords.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (line01.startsWith((String) pair.getKey())) {
                return (String) pair.getValue();
            }
        }

        // 08: NOTAM
        it = line01IncludeKeywords.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (line01.contains((String) pair.getKey())) {
                return (String) pair.getValue();
            }
        }

        // Get line02
        if (lines.length < 2) {
            return "";
        }
        final String line02 = lines[1];

        // 01: METAR
        if (line01.startsWith("SA") && line02.startsWith("MET")) {
            return "METAR";
        }

        // 02: SPECI
        if (line01.startsWith("SP") && line02.contains("SPECI")) {
            return "SPECI";
        }
        // 03: AIRMET 
        if (line01.startsWith("WA") && line02.contains("AIRMET")) {
            return "AIRMET";
        }
        // 04: SIGMET 
        if ((line01.startsWith("WS") || line01.startsWith("WC") || line01.startsWith("WV")) && line02.contains(" SIGMET ")) {
            return "SIGMET";
        }

        // 05: TAF
        if ((line01.startsWith("FC") || line01.startsWith("FT")) && line02.startsWith("TAF ")) {
            return "TAF";
        }

        // 06: SYNOP | //AAXX, BBXX
        if ((line01.startsWith("SYN") || line02.startsWith("BBXX")) && line02.startsWith("AAXX")) {
            return "SYNOP";
        }
        // 07: RQM
        if (line01.startsWith("RQM")) {
            return "RQM";
        }
        // 09: ASHTAM
        if ((line01.startsWith("VA")) && line02.contains("ASHTAM")) {
            return "ASHTAM";
        }

        // 10: SNOWTAM
        if ((line01.startsWith("SW")) && line02.contains("SNOWTAM")) {
            return "SNOWTAM";
        }

        // 11: AFP
        // 06: SYNOP
        // 07: RQM
        // 18: CPL
        // 20: CDN
        // 21: ACP
        // 22: ALR
        return "";
    }

    public static String checkDate(String strTime) {
        String strout = "";
        try {

            if (strTime == null || strTime.isEmpty()) {

                return null;
            }
            if (strTime.length() > 12) {
            } else {
                if (strTime.contains("Z")) {
                    String[] a = strTime.split("Z");
                    strTime = a[0];
                    if (strTime.length() < 12) {
                        for (int i = 0; i <= 12 - strTime.length(); i++) {
                            strTime = strTime + "0";
                        }
                    }
                    strTime = strTime + "Z";
                }
            }
            strout = strTime;

        } catch (Exception ex) {
            return null;
        }
        return strout;
    }

    public static Date parseDate(String time) {

        try {
            utcTime.setTimeZone(utc);

            if (time == null || time.isEmpty()) {
                return null;
            }
            
//            time = checkDate(time);
            return (time.contains("Z")) ? utcTime.parse(time) : localTime.parse(time);

        } catch (ParseException ex) {
            logger.error("Cannot parse {} to datetime ({})", time, ex.getCause().getMessage());
            return null;
        }
    }
    
      public static String normalizeMessageContent(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        while (content.startsWith(" ") || content.startsWith("\r") || content.startsWith("\n")) {
            content = new StringBuilder(content).deleteCharAt(0).toString().trim();
        }

        return content;
    }
    
    
}
