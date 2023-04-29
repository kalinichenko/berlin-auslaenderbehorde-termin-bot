package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.example.model.PersonalInfoFormTO;
import org.example.model.VisaFormTO;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IoUtils {

    private final static Logger logger = LoggerFactory.getLogger(IoUtils.class);


    public static String CLOUDWATCH_METRIC_FOR_CALENDER_OPENED = "calender opened";
    public static String CLOUDWATCH_METRIC_FOR_VERIFIED_TIMESLOT = "verified timeslot";
    public static String CLOUDWATCH_METRIC_FOR_RESERVATION_COMPLETED = "reservation completed";
    public static boolean isS3Enabled = false;
    public static String CLOUDWATCH_METRIC_NAMESPACE = "termin-bot";
    public static boolean isLocalSaveEnabled = true;
    public static boolean isCloudwatchEnabled = false;

    private IoUtils() {
    }

    public static PersonalInfoFormTO readPersonalInfoFromFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = PersonalInfoFormTO.class.getResourceAsStream("/DEFAULT_PERSONAL_INFO_FORM.json");
        return mapper.readValue(is, PersonalInfoFormTO.class);
    }

    public static VisaFormTO readVisaInfoFromFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = VisaFormTO.class.getResourceAsStream("/DEFAULT_VISA_APPLICATION_FORM.json");
        return mapper.readValue(is, VisaFormTO.class);
    }

    public static void savePage(WebDriver driver, String pageDescriber, String suffix) {
        if (!isLocalSaveEnabled) {
            logger.info("Saving is disabled");
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dateAsStr = dtf.format(now);
        String fileName = pageDescriber + "_" + dateAsStr + "_" + suffix;
        String pagesourceFileName = fileName + ".html";
        String screenshotFileName = fileName + ".png";
        logger.info("File name :{}, {}", pagesourceFileName, screenshotFileName);

        String content;
        try {
            logger.info("Getting the page content");
            content = driver.getPageSource();

        } catch (Exception exception) {
            logger.error("Error occurred during getting the page source. Reason: ", exception);
            return;
        }


        try {
            saveSourceCodeToFile(content, pagesourceFileName);
        } catch (IOException e) {
            logger.error("Error occurred during IO operation. Exception: ", e);
            return;
        }
        try {
            saveScreenshot(driver, screenshotFileName);
        } catch (IOException e) {
            logger.error("Error occurred during IO operation. Exception: ", e);
            return;
        }

    }

    private static File saveSourceCodeToFile(String content, String fileName) throws IOException {
        logger.info("Saving source code to file");
        File file = new File(fileName);
        FileWriter fw;
        fw = new FileWriter(file);
        fw.write(content);
        fw.close();
        return file;
    }

    private static File saveScreenshot(WebDriver driver, String fileName) throws IOException {
        logger.info("Saving screenshot");
        File scrFile1 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File file = new File(fileName);
        FileUtils.copyFile(scrFile1, file);
        return file;
    }

}
