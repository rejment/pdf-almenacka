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
import org.apache.pdfbox.util.Matrix;

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
    static PDType0Font boldfont;
    static PDType0Font regularfont;

    public static void main(String[] args) throws IOException {
        try (PDDocument document = new PDDocument()) {


            font = PDType0Font.load(document, new File("fonts/NotoSerif-Italic.ttf"));
            boldfont = PDType0Font.load(document, new File("fonts/NotoSerif-Bold.ttf"));
            regularfont = PDType0Font.load(document, new File("fonts/NotoSerif-Regular.ttf"));

            addFrontPage(document, 2024);

            for (int month=1; month<=12; month++) {
                addImagePage(document, month, 2024);
                addCalendarPage(document, month, 2024);
            }

            document.save("kalender1.pdf");
        }


    }

    private static void addFrontPage(PDDocument document, int year) throws IOException {
        PDPage page = createPage();
        document.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            addCropLines(page, contentStream);
            PDRectangle bBox = page.getTrimBox();
            contentStream.setNonStrokingColor(new Color(0xE5DADA));
            contentStream.setNonStrokingColor(new Color(0));
            addRectangle(contentStream, bBox);
            contentStream.fill();

            PDImageXObject pdImage = PDImageXObject.createFromFile("img/kalender1/darktable_exported/kalender.jpg", document);

            //contentStream.drawImage(pdImage, (bBox.getWidth() - mm(imageWidthMM)) / 2.0f, bBox.getHeight() - mm(imageHeightMM + 15.0f), mm(imageWidthMM), mm(imageHeightMM))

            float margin = mm(10);
            float width = bBox.getWidth() - margin*2;
            float ratio = pdImage.getHeight() / (float) pdImage.getWidth();
            float height = width * ratio;

            contentStream.drawImage(pdImage, margin, margin+mm(3), width, height);

            contentStream.setStrokingColor(new Color(0xE5DADA));
            contentStream.setNonStrokingColor(new Color(0xE5DADA));
            contentStream.beginText();
            //centerText(page, contentStream, "Grodor 2024", bBox.getHeight() - mm(20), boldfont, 44);
            rightText(page, contentStream, "Grodor 2024", bBox.getWidth()-margin,bBox.getHeight() - mm(30), boldfont, 42);
            contentStream.endText();
            contentStream.beginText();
            leftText(page, contentStream, "Paulina Hermansson", margin, margin-3, regularfont, 8);
            contentStream.endText();
            contentStream.beginText();
            leftText(page, contentStream, "Email: paulina.hermansson@gmail.com", margin, margin-10-3, regularfont, 6);
            contentStream.endText();


            contentStream.saveGraphicsState();
            contentStream.transform(Matrix.getRotateInstance(Math.toRadians(45), -30, 410));
            contentStream.setStrokingColor(new Color(0x858383));
            contentStream.setStrokingColor(new Color(0x0));
            contentStream.setNonStrokingColor(new Color(0xffffff));
            contentStream.setLineWidth(0.5f);
            contentStream.addRect(-20, 0, 340, 27);
            contentStream.fillAndStroke();

            Color c = new Color(0xC2692B);
            contentStream.setStrokingColor(new Color(0));
            contentStream.setNonStrokingColor(c);
            contentStream.beginText();
            leftText(page, contentStream, "Väggkalender", 68, 7, boldfont, 18);
            contentStream.endText();

            contentStream.restoreGraphicsState();

        }
    }

    private static final int DEFAULT_USER_SPACE_UNIT_DPI = 72;
    private static final float MM_TO_UNITS = 1/(10*2.54f)*DEFAULT_USER_SPACE_UNIT_DPI;

    static float mm(float v) {
        return v*MM_TO_UNITS;
    }

    private static void addImagePage(PDDocument document, int month, int year) throws IOException {
        PDPage page = createPage();
        document.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            addCropLines(page, contentStream);
            PDRectangle bBox = page.getTrimBox();

            //contentStream.setNonStrokingColor(new Color(0x6C6A6A));
            //addRectangle(contentStream, bBox);
            //contentStream.fill();

            // rita bild här
            PDImageXObject pdImage = PDImageXObject.createFromFile("img/kalender1/darktable_exported/_" + monthName[month].toLowerCase() + ".jpg", document);

            float ratio = 0.70476186f;
            float imageHeightMM = 190.0f;
            float imageWidthMM = imageHeightMM * ratio;
            /*contentStream.setStrokingColor(new Color(0));
            contentStream.setLineWidth(0.1f);
            contentStream.setNonStrokingColor(new Color(0xffffff));
            contentStream.addRect((bBox.getWidth() - mm(imageWidthMM)) / 2.0f, bBox.getHeight() - mm(imageHeightMM + 15.0f), mm(imageWidthMM), mm(imageHeightMM));
            contentStream.fillAndStroke();*/
            //contentStream.drawImage(pdImage, (bBox.getWidth() - mm(imageWidthMM)) / 2.0f, bBox.getHeight() - mm(imageHeightMM + 15.0f), mm(imageWidthMM), mm(imageHeightMM));

            contentStream.drawImage(pdImage, bBox.getLowerLeftX()-mm(3), bBox.getLowerLeftY()-mm(3), bBox.getWidth()+mm(6), bBox.getHeight()+mm(6));

            /*contentStream.setStrokingColor(new Color(0));
            contentStream.setNonStrokingColor(new Color(0));
            contentStream.beginText();
            centerText(page, contentStream, monthName[month] , 35, font, 44);
            contentStream.endText();*/
            /*contentStream.beginText();
            centerText(page, contentStream, Integer.toString(year) , 35, font, 24);
            contentStream.endText();*/
        }
    }

    private static void addCropLines(PDPage page, PDPageContentStream contentStream) throws IOException {
        PDRectangle mediaBox = page.getMediaBox();
        PDRectangle trimBox = page.getTrimBox();
        float dist = mm(3.5f);
        contentStream.setStrokingColor(new Color(0));
        contentStream.setLineWidth(0.2f);
        // lower left corner
        // down
        contentStream.moveTo(trimBox.getLowerLeftX(), trimBox.getLowerLeftY()-dist);
        contentStream.lineTo(trimBox.getLowerLeftX(), mediaBox.getLowerLeftY());
        // left
        contentStream.moveTo(trimBox.getLowerLeftX()-dist, trimBox.getLowerLeftY());
        contentStream.lineTo(mediaBox.getLowerLeftX(), trimBox.getLowerLeftY());
        // upper left corner
        // up
        contentStream.moveTo(trimBox.getLowerLeftX(), trimBox.getUpperRightY()+dist);
        contentStream.lineTo(trimBox.getLowerLeftX(), mediaBox.getUpperRightY());
        // left
        contentStream.moveTo(trimBox.getLowerLeftX()-dist, trimBox.getUpperRightY());
        contentStream.lineTo(mediaBox.getLowerLeftX(), trimBox.getUpperRightY());
        // upper right corner
        // up
        contentStream.moveTo(trimBox.getUpperRightX(), trimBox.getUpperRightY()+dist);
        contentStream.lineTo(trimBox.getUpperRightX(), mediaBox.getUpperRightY());
        // right
        contentStream.moveTo(trimBox.getUpperRightX()+dist, trimBox.getUpperRightY());
        contentStream.lineTo(mediaBox.getUpperRightX(), trimBox.getUpperRightY());
        // lower right corner
        // down
        contentStream.moveTo(trimBox.getUpperRightX(), trimBox.getLowerLeftY()-dist);
        contentStream.lineTo(trimBox.getUpperRightX(), mediaBox.getLowerLeftY());
        // right
        contentStream.moveTo(trimBox.getUpperRightX()+dist, trimBox.getLowerLeftY());
        contentStream.lineTo(mediaBox.getUpperRightX(), trimBox.getLowerLeftY());
        contentStream.stroke();
    }

    private static PDPage createPage() {
        PDRectangle a5 = PDRectangle.A5;
        float margin = mm(12f);
        PDRectangle mediaBox = new PDRectangle(-margin, -margin, a5.getWidth() + 2*margin, a5.getHeight() + 2*margin);
        //mediaBox = a5;
        PDPage page = new PDPage(mediaBox);
        page.setTrimBox(a5);
        return page;
    }

    private static void centerText(PDPage page, PDPageContentStream contentStream, String text, float y, PDType0Font font, float fontSize) throws IOException {

        float textWidth = font.getStringWidth(text) / 1000.0f * fontSize;

        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset((page.getTrimBox().getWidth()-textWidth)/2.0f, y);
        contentStream.showText(text);
    }

    private static void leftText(PDPage page, PDPageContentStream contentStream, String text, float x, float y, PDFont font, float fontSize) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000.0f * fontSize;
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
    }
    private static void rightText(PDPage page, PDPageContentStream contentStream, String text, float x, float y, PDFont font, float fontSize) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000.0f * fontSize;
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x-textWidth, y);
        contentStream.showText(text);
    }

    private static void addCalendarPage(PDDocument document, int month, int year) throws IOException {
        PDPage page = createPage();
        document.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            addCropLines(page, contentStream);
            PDRectangle bBox = page.getTrimBox();
            contentStream.setNonStrokingColor(new Color(0xE5DADA));
            addRectangle(contentStream, bBox);
            contentStream.fill();

            float imageWidthMM = 95.0f + 26.5f;
            float fullHeight = 190f;
            float cardGap = 2.0f;
            float dayHeight = (fullHeight - cardGap*5.0f) / 31.0f;
            float top = bBox.getHeight() - mm(15) - mm(5);
            float left = (bBox.getWidth() - mm(imageWidthMM)) / 2.0f;
            float right = left + mm(imageWidthMM);

            contentStream.setStrokingColor(new Color(0x858383));
            contentStream.setNonStrokingColor(new Color(0x858383));
            contentStream.beginText();
            //leftText(page, contentStream, monthName[month] , right,bBox.getHeight() - mm(15), font, 24);
            leftText(page, contentStream, monthName[month] , left + mm(5),bBox.getHeight() - mm(15), font, 24);
            contentStream.endText();


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

    private static void addRectangle(PDPageContentStream contentStream, PDRectangle bBox) throws IOException {
        float bleed = mm(3);
        contentStream.addRect(bBox.getLowerLeftX()-bleed, bBox.getLowerLeftY()-bleed, bBox.getWidth()+bleed*2, bBox.getHeight()+bleed*2);
    }
}
