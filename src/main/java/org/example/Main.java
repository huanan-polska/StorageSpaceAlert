package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.

public class Main {
    public static void main(String[] args) throws IOException {

        String[] setting = new String[0];
        try {
            List<String> allLines = Files.readAllLines(Paths.get("src/main/resources/settings.txt"));

            setting = new String[]{"a", "b", "c", "d"};

            var i = 0;

            for (String line : allLines) {
                System.out.println(line);
                setting[i] = line;
                i++;
            }
            System.out.println("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int availableSpaceThreshold = 1000; // top limit of free space before sending an alert (in Megabytes)

        String adminAddress = setting[0].replace("adminAddress=","");   // who to send the message to
        String smtpAddress = setting[1].replace("smtpAddress=","");     // address of real SMTP server
        String smtpUsername = setting[2].replace("smtpLogin=","");      // username for logging in
        String smtpPassword = setting[3].replace("smtpPassword=","");   // password for logging in

        File[] roots = File.listRoots();

        for (File root : roots) {
            System.out.println("Partycja: " + root.getAbsolutePath());
            System.out.println("Pojemnosc calkowita (w GB): " + root.getTotalSpace() / 1073741824);
            System.out.println("Wolna przestrzen (w GB): " + root.getFreeSpace() / 1073741824);
            System.out.println("Dostepna przestrzen (w GB): " + root.getUsableSpace() / 1073741824 + "\n");

            String driveLetter = root.getAbsolutePath();
            long freeSpace = root.getFreeSpace() / 1073741824;
            long freeSpaceMB = root.getFreeSpace() / 1048576;

            if (freeSpace < availableSpaceThreshold / 1000) {

                System.out.println(("Na partycji " + driveLetter + " pozostało " + freeSpaceMB + " MB wolnego miejsca.\n"));

                Email email = EmailBuilder.startingBlank()
                        .from("HUANAN STORAGE MONITOR", smtpUsername)
                        .to("SYSTEM ADMIN", adminAddress)
                        .withReplyTo("HUANAN POLSKA", "kontakt@huanan.pl")
                        .withSubject("DRIVE " + driveLetter + " IS ALMOST FULL!")
                        .withPlainText("EMPTY SPACE ON THIS DRIVE IS ONLY " + freeSpaceMB + " MB!")
                        .buildEmail();

                Mailer mailer = MailerBuilder
                        .withSMTPServer(smtpAddress, 587, smtpUsername, smtpPassword)
                        .buildMailer();

                mailer.sendMail(email);

            }
        }


    }
}