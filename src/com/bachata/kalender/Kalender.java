package com.bachata.kalender;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class Kalender {

    static String[] monthName = {null, "Januari", "Februari", "Mars", "April", "Maj", "Juni", "Juli", "Augusti", "September", "Oktober", "November", "December"};
    static PDType0Font font;

    public static void main(String[] args) throws IOException {
        try (PDDocument document = new PDDocument()) {

            font = PDType0Font.load(document, new File("fonts/NotoSerif-Italic.ttf"));
            for (int month=1; month<=1; month++) {
                addImagePage(document, month, 2024);
                addCalendarPage(document, month, 2024);
            }

            document.save("kalender1.pdf");
        }


    }

    private static final int DEFAULT_USER_SPACE_UNIT_DPI = 72;
    private static final float MM_TO_UNITS = 1/(10*2.54f)*DEFAULT_USER_SPACE_UNIT_DPI;

    static float mm(float v) {
        return v*MM_TO_UNITS;
    }

    private static void addImagePage(PDDocument document, int month, int year) throws IOException {
        PDPage page = new PDPage(PDRectangle.A5);
        document.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            PDRectangle bBox = page.getBBox();

            contentStream.setNonStrokingColor(new Color(0x6C6A6A));
            contentStream.addRect(bBox.getLowerLeftX(), bBox.getLowerLeftY(), bBox.getWidth(), bBox.getHeight());
            contentStream.fill();

            // rita bild här
            PDImageXObject pdImage = PDImageXObject.createFromFile("img/januari.jpeg", document);

            float imageWidthMM = 95.0f;
            float imageHeightMM = 150.0f;
            /*contentStream.setStrokingColor(new Color(0));
            contentStream.setLineWidth(0.1f);
            contentStream.setNonStrokingColor(new Color(0xffffff));
            contentStream.addRect((bBox.getWidth() - mm(imageWidthMM)) / 2.0f, bBox.getHeight() - mm(imageHeightMM + 15.0f), mm(imageWidthMM), mm(imageHeightMM));
            contentStream.fillAndStroke();*/
            contentStream.drawImage(pdImage, (bBox.getWidth() - mm(imageWidthMM)) / 2.0f, bBox.getHeight() - mm(imageHeightMM + 15.0f), mm(imageWidthMM), mm(imageHeightMM));


            contentStream.setStrokingColor(new Color(0));
            contentStream.setNonStrokingColor(new Color(0));
            contentStream.beginText();
            centerText(page, contentStream, monthName[month] , 70, font, 44);
            contentStream.endText();
            contentStream.beginText();
            centerText(page, contentStream, Integer.toString(year) , 35, font, 24);
            contentStream.endText();
        }
    }

    private static void centerText(PDPage page, PDPageContentStream contentStream, String text, float y, PDType0Font font, float fontSize) throws IOException {

        float textWidth = font.getStringWidth(text) / 1000.0f * fontSize;

        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset((page.getMediaBox().getWidth()-textWidth)/2.0f, y);
        contentStream.showText(text);
    }

    private static void rightText(PDPage page, PDPageContentStream contentStream, String text, float x, float y, PDFont font, float fontSize) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000.0f * fontSize;
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x-textWidth, y);
        contentStream.showText(text);
    }

    private static void addCalendarPage(PDDocument document, int month, int year) throws IOException {
        PDPage page = new PDPage(PDRectangle.A5);
        document.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            PDRectangle bBox = page.getBBox();
            contentStream.setNonStrokingColor(new Color(0x6C6A6A));
            contentStream.addRect(bBox.getLowerLeftX(), bBox.getLowerLeftY(), bBox.getWidth(), bBox.getHeight());
            contentStream.fill();

            float imageWidthMM = 95.0f + 26.5f;
            float fullHeight = 190f;
            float cardGap = 2.0f;
            float dayHeight = (fullHeight - cardGap*5.0f) / 31.0f;
            float top = bBox.getHeight() - mm(15);
            float left = (bBox.getWidth() - mm(imageWidthMM)) / 2.0f;
            float right = left + mm(imageWidthMM);


            LocalDate date = LocalDate.of(year, month, 1);

            int daysInMonth = date.getMonth().length(date.isLeapYear());
            int weekday = date.getDayOfWeek().getValue() - 1;
            TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
            int weekNumber = date.get(woy);

            for (int o = 0; o<daysInMonth; o++) {

                // draw the card
                if (o == 0 || weekday == 0) {
                    float cardHeight = 7 * dayHeight;
                    if (o == 0) {
                        cardHeight = (7 - weekday) * dayHeight;
                    }
                    if (o+7>=daysInMonth) {
                        cardHeight -= (o+7-daysInMonth) * dayHeight;
                    }
                    contentStream.setNonStrokingColor(new Color(0xffffff));
                    contentStream.addRect(left, top-mm(cardHeight), mm(imageWidthMM), mm(cardHeight));
                    contentStream.fill();

                    // week number
                    if (cardHeight > dayHeight*2.5) {
                        Color color = new Color(0xe0e0e0);
                        contentStream.setStrokingColor(color);
                        contentStream.setNonStrokingColor(color);
                        contentStream.beginText();
                        rightText(page, contentStream, Integer.toString(weekNumber), right-mm(7), top-mm(15), font, 50);
                        contentStream.endText();
                    }
                }


                // draw numbers & text
                {
                    float offset = mm(1.2f);
                    boolean isWeekend = weekday == 5 || weekday == 6;
                    boolean isRed = weekday == 6;
                    Color textColor = isRed ? new Color(0xff0000) : new Color(0x777777);
                    Color numberColor = isRed ? new Color(0xff0000) : new Color(0x0);
                    PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                    if (isWeekend) {
                        font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                    }
                    contentStream.setStrokingColor(numberColor);
                    contentStream.setNonStrokingColor(numberColor);
                    contentStream.beginText();
                    rightText(page, contentStream, Integer.toString(o + 1), left + mm(10f), top - mm(dayHeight) + offset, font, 14);
                    contentStream.endText();

                    contentStream.setStrokingColor(textColor);
                    contentStream.setNonStrokingColor(textColor);
                    contentStream.setFont(font, 8);
                    String[] text = {"Må", "Ti", "On", "To", "Fr", "Lö", "Sö"};
                    contentStream.beginText();
                    contentStream.newLineAtOffset(left + mm(11), top - mm(dayHeight) + offset);
                    contentStream.showText(text[weekday]);
                    contentStream.endText();
                }

                // draw lines
                if (weekday<6) {
                    contentStream.setStrokingColor(new Color(0xc0c0c0));
                    float lineMargin = mm(7);
                    contentStream.moveTo(left + 0*lineMargin/2.0f, top - mm(dayHeight));
                    contentStream.lineTo(right - lineMargin, top - mm(dayHeight));
                    contentStream.stroke();
                }

                // move to next day
                if (weekday == 6) {
                    top -= cardGap;
                    weekNumber += 1;
                }
                top -= mm(dayHeight);
                weekday = (weekday + 1) % 7;
            }
        }
    }
    private static void addCalendarPageOld(PDDocument document, int month, int year) throws IOException {
        PDPage page = new PDPage(PDRectangle.A5);
        document.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            PDRectangle bBox = page.getBBox();
            contentStream.setNonStrokingColor(new Color(0x6C6A6A));
            contentStream.addRect(bBox.getLowerLeftX(), bBox.getLowerLeftY(), bBox.getWidth(), bBox.getHeight());
            contentStream.fill();

            int daysInMonth = 31;

            float imageWidthMM = 95.0f + 26.5f;
            float heightFor31Days = 190f;
            float heightFor1Day = heightFor31Days / 31;

            float imageHeightMM = daysInMonth * heightFor1Day;
            float bottom = mm(5 + (31-daysInMonth)*heightFor1Day);
            float top = bottom + mm(imageHeightMM);
            float left = (bBox.getWidth() - mm(imageWidthMM)) / 2.0f;
            float right = left + mm(imageWidthMM);
            contentStream.setNonStrokingColor(new Color(0xffffff));
            contentStream.addRect(left, bottom, mm(imageWidthMM), mm(imageHeightMM));
            contentStream.fill();

            for (int day = 1; day <= daysInMonth; day++) {
                boolean isMonday = day % 7 == 0;
                boolean isFriday = day % 7 == 4;
                boolean isSaturday = day % 7 == 5;
                boolean isSunday = day % 7 == 6;

                if (isMonday || day==1) {
                    Color color = new Color(0xe0e0e0);
                    contentStream.setStrokingColor(color);
                    contentStream.setNonStrokingColor(color);
                    contentStream.setFont(font, 50);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(right - mm(28), top-mm(15));
                    contentStream.showText(Integer.toString(40 + (day / 7)));
                    contentStream.endText();
                }


                if (isSaturday) {
                    /*contentStream.setNonStrokingColor(new Color(0xDADADA));
                    contentStream.addRect(left+mm(7), top - mm(heightFor1Day*2.0f), right-left-mm(14), mm(heightFor1Day*2.0f));
                    contentStream.fill();*/

                }


                Color color = isSunday ? new Color(0xff0000) : new Color(0x777777);
                contentStream.setStrokingColor(color);
                contentStream.setNonStrokingColor(color);
                if (isSaturday || isSunday) {
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 8);
                } else {
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
                }

                String[] text = {"Må", "Ti", "On", "To", "Fr", "Lö", "Sö"};
                contentStream.beginText();
                contentStream.newLineAtOffset(left + mm(11), top - mm(heightFor1Day) + mm(1.4f));
                contentStream.showText(text[day%7]);
                contentStream.endText();

                color = isSunday ? new Color(0xff0000) : new Color(0);
                contentStream.setStrokingColor(color);
                contentStream.setNonStrokingColor(color);
                PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                if (isSaturday || isSunday) {
                    font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                }
                contentStream.beginText();
                rightText(page, contentStream, Integer.toString(day), left+mm(10f), top - mm(heightFor1Day) + mm(1.4f), font, 14);
                contentStream.endText();

                //contentStream.newLineAtOffset(left + mm(3), top - mm(heightFor1Day) + mm(2.0f));
                //contentStream.showText(Integer.toString(day));

                if (isSunday) {
                    float margin = 2.0f;
                    contentStream.setNonStrokingColor(new Color(0x6C6A6A));
                    contentStream.addRect(left-10, top - mm(heightFor1Day + margin), right-left+20, mm(margin));
                    contentStream.fill();
                    top -= mm(margin);
                } else if (false && isFriday) {
                    contentStream.setStrokingColor(new Color(0x404040));
                    float lineMargin = mm(7);
                    contentStream.moveTo(left + lineMargin, top - mm(heightFor1Day));
                    contentStream.lineTo(right - lineMargin, top - mm(heightFor1Day));
                    contentStream.setLineWidth(1.0f);
                    contentStream.stroke();
                    contentStream.setLineWidth(1.0f);

                } else if (day == daysInMonth) {

                } else {
                    contentStream.setStrokingColor(new Color(0xc0c0c0));
                    float lineMargin = mm(7);
                    contentStream.moveTo(left + 0*lineMargin/2.0f, top - mm(heightFor1Day));
                    contentStream.lineTo(right - lineMargin, top - mm(heightFor1Day));
                    contentStream.stroke();
                }


                top -= mm(heightFor1Day);
            }
        }
    }
}
