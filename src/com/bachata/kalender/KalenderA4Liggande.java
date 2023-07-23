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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KalenderA4Liggande {

    static String[] monthName = {null, "Januari", "Februari", "Mars", "April", "Maj", "Juni", "Juli", "Augusti", "September", "Oktober", "November", "December"};
    static PDType0Font font;
    static PDType0Font boldfont;
    static PDType0Font boldregular;
    static PDType0Font regularfont;
    static Map<String, String> names = new HashMap<>();

    public static void main(String[] args) throws IOException {
        try (PDDocument document = new PDDocument()) {
            font = PDType0Font.load(document, new File("fonts/NotoSerif-Italic.ttf"));
            boldfont = PDType0Font.load(document, new File("fonts/NotoSerif-BoldItalic.ttf"));
            boldregular = PDType0Font.load(document, new File("fonts/NotoSerif-Bold.ttf"));
            regularfont = PDType0Font.load(document, new File("fonts/NotoSerif-Regular.ttf"));

            addFrontPage(document, 2024);
            List<String> strings = Files.readAllLines(Paths.get("namnsdag.txt"));
            strings.forEach(line -> {
                String[] s = line.split("    ");
                names.put(s[0], s[1]);
            });


            for (int month=1; month<=12; month++) {
                addImagePage(document, month, 2024);
                addCalendarPage(document, month, 2024);
            }

            document.save("kalender2.pdf");
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

            //PDImageXObject pdImage = PDImageXObject.createFromFile("img/kalender2.png", document);
            PDImageXObject pdImage = PDImageXObject.createFromFile("img/kalender2/darktable_exported/kalender.jpg", document);

            //contentStream.drawImage(pdImage, (bBox.getWidth() - mm(imageWidthMM)) / 2.0f, bBox.getHeight() - mm(imageHeightMM + 15.0f), mm(imageWidthMM), mm(imageHeightMM))

            float margin = mm(10);
            float height = bBox.getHeight() - margin*2;
            float ratio = pdImage.getHeight() / (float) pdImage.getWidth();
            float width = height / ratio;
            contentStream.drawImage(pdImage, margin, margin+mm(3), width, height);

            contentStream.setStrokingColor(new Color(0xE5DADA));
            contentStream.setNonStrokingColor(new Color(0xE5DADA));
            contentStream.beginText();
            rightText(page, contentStream, "Fäboden", bBox.getWidth()-margin-30-mm(5),bBox.getHeight() - mm(30)-mm(20), boldregular, 60);
            contentStream.endText();

            contentStream.beginText();
            rightText(page, contentStream, "Namnsdagskalender", bBox.getWidth()-margin-mm(15),bBox.getHeight() - mm(45)-mm(20), boldfont, 25);
            contentStream.endText();

            contentStream.beginText();
            leftText(page, contentStream, "Paulina Hermansson", margin, margin-3, regularfont, 8);
            contentStream.endText();
            contentStream.beginText();
            leftText(page, contentStream, "Email: paulina.hermansson@gmail.com", margin, margin-10-3, regularfont, 6);
            contentStream.endText();


            /*contentStream.saveGraphicsState();
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
            contentStream.restoreGraphicsState();*/

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

            Color BACKGROUND = new Color(0xB6BDB4);
            contentStream.setNonStrokingColor(BACKGROUND);
            addRectangle(contentStream, bBox);
            contentStream.fill();

            // rita bild här
            //PDImageXObject pdImage = PDImageXObject.createFromFile("img/januari3.png", document);
            PDImageXObject pdImage = PDImageXObject.createFromFile("img/kalender2/darktable_exported/_" + monthName[month].toLowerCase() + ".jpg", document);


            PDRectangle a4 = PDRectangle.A4;
            float imageWidthMM = (a4.getHeight()/MM_TO_UNITS) - 30;
            float imageHeightMM = (a4.getWidth()/MM_TO_UNITS) - 30;
            /*contentStream.setStrokingColor(new Color(0));
            contentStream.setLineWidth(0.1f);
            contentStream.setNonStrokingColor(new Color(0xffffff));
            contentStream.addRect((bBox.getWidth() - mm(imageWidthMM)) / 2.0f, bBox.getHeight() - mm(imageHeightMM + 15.0f), mm(imageWidthMM), mm(imageHeightMM));
            contentStream.fillAndStroke();*/
            contentStream.drawImage(pdImage, (bBox.getWidth() - mm(imageWidthMM)) / 2.0f, bBox.getHeight() - mm(imageHeightMM + 15.0f), mm(imageWidthMM), mm(imageHeightMM));


            /*contentStream.setStrokingColor(new Color(0));
            contentStream.setNonStrokingColor(new Color(0));
            contentStream.beginText();
            centerText(page, contentStream, monthName[month] , 70, font, 44);
            contentStream.endText();
            contentStream.beginText();
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
        PDRectangle a4 = PDRectangle.A4;
        PDRectangle pageSize = new PDRectangle(a4.getHeight(), a4.getWidth());
        float margin = mm(12f);
        PDRectangle mediaBox = new PDRectangle(-margin, -margin, pageSize.getWidth() + 2*margin, pageSize.getHeight() + 2*margin);
        //mediaBox = pageSize;
        PDPage page = new PDPage(mediaBox);
        page.setTrimBox(pageSize);
        return page;
    }

    private static void centerText(PDPage page, PDPageContentStream contentStream, String text, float y, PDType0Font font, float fontSize) throws IOException {

        float textWidth = font.getStringWidth(text) / 1000.0f * fontSize;

        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset((page.getTrimBox().getWidth()-textWidth)/2.0f, y);
        contentStream.showText(text);
    }

    private static void leftText(PDPage page, PDPageContentStream contentStream, String text, float x, float y, PDFont font, float fontSize) throws IOException {
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
            Color BACKGROUND = new Color(0xB6BDB4);
            contentStream.setNonStrokingColor(BACKGROUND);
            addRectangle(contentStream, bBox);
            contentStream.fill();

            float topMargin = mm(20);
            float imageWidthMM = 95.0f + 26.5f + 10f;
            float fullHeight = 320f;
            float cardGap = 2.0f;
            float dayHeight = (fullHeight - cardGap*5.0f) / 31.0f;
            float top = bBox.getHeight() - topMargin;
            float left = mm(15);
            float left2 = bBox.getWidth() - mm(15) - mm(imageWidthMM);
            float right = left + mm(imageWidthMM);

            LocalDate date = LocalDate.of(year, month, 1);

            int daysInMonth = date.getMonth().length(date.isLeapYear());
            int weekday = date.getDayOfWeek().getValue() - 1;

            contentStream.setNonStrokingColor(new Color(0xffffff));
            float cardHeight = 17 * dayHeight;
            contentStream.addRect(left, top-mm(cardHeight), mm(imageWidthMM), mm(cardHeight));
            cardHeight = (daysInMonth-17) * dayHeight;
            contentStream.addRect(left2, top-mm(cardHeight), mm(imageWidthMM), mm(cardHeight));
            contentStream.fill();


            for (int o = 0; o<daysInMonth; o++) {

                String nameOfDay = (o + 1) + " " + monthName[month].toLowerCase();
                String todaysName = names.get(nameOfDay);


                if (o == 17) {
                    top = bBox.getHeight() - topMargin;
                    left = left2;
                    right = left + mm(imageWidthMM);
                }


                // draw numbers & text
                {
                    float offset = mm(3.2f);
                    boolean isWeekend = false; // weekday == 5 || weekday == 6;
                    boolean isRed = false; // weekday == 6;
                    Color textColor = isRed ? new Color(0xff0000) : new Color(0x777777);
                    Color numberColor = isRed ? new Color(0xff0000) : new Color(0x0);
                    PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                    if (isWeekend) {
                        font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                    }
                    contentStream.setStrokingColor(numberColor);
                    contentStream.setNonStrokingColor(numberColor);
                    contentStream.beginText();
                    rightText(page, contentStream, Integer.toString(o + 1), left + mm(10f), top - mm(dayHeight) + offset-2, font, 16);
                    contentStream.endText();

                    contentStream.setStrokingColor(textColor);
                    contentStream.setNonStrokingColor(textColor);
                    contentStream.setFont(font, 7);
                    contentStream.beginText();
                    rightText(page, contentStream, todaysName, right - mm(3.5f), top - mm(dayHeight) + offset-2, font, 7);
                    //contentStream.newLineAtOffset(left + mm(12), top - mm(dayHeight) + offset);
                    //contentStream.showText(todaysName);
                    contentStream.endText();
                    /*
                    contentStream.setStrokingColor(textColor);
                    contentStream.setNonStrokingColor(textColor);
                    contentStream.setFont(font, 8);
                    String[] text = {"Må", "Ti", "On", "To", "Fr", "Lö", "Sö"};
                    contentStream.beginText();
                    contentStream.newLineAtOffset(left + mm(11), top - mm(dayHeight) + offset);
                    contentStream.showText(text[weekday]);
                    contentStream.endText();*/
                }

                // draw lines
                if (weekday<10) {
                    contentStream.setStrokingColor(new Color(0xc0c0c0));
                    float lineMargin = mm(7);
                    contentStream.moveTo(left + 1*lineMargin/2.0f, top - mm(dayHeight));
                    contentStream.lineTo(right - lineMargin/2.0f, top - mm(dayHeight));
                    contentStream.stroke();
                }

                top -= mm(dayHeight);
                weekday = (weekday + 1) % 7;
            }

            Color color = new Color(0x686C67);
            contentStream.setStrokingColor(color);
            contentStream.setNonStrokingColor(color);
            contentStream.beginText();
            rightText(page, contentStream, monthName[month] , bBox.getUpperRightX() - mm(15), 60, font, 75);
            contentStream.endText();
        }
    }

    private static void addRectangle(PDPageContentStream contentStream, PDRectangle bBox) throws IOException {
        float bleed = mm(3);
        contentStream.addRect(bBox.getLowerLeftX()-bleed, bBox.getLowerLeftY()-bleed, bBox.getWidth()+bleed*2, bBox.getHeight()+bleed*2);
    }
}
