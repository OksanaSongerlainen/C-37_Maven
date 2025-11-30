package Lesson_23_1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    /*
        Написать программу для парсинга xml документа. Необходимо распарсить xml документ и
        содержимое тегов line записать в другой документ. Название файла для записи должно
        состоять из значений тегов и имеет вид: <firstName>_<lastName>_<title>.txt
    */
    /* Задача *:
    Дополнительно реализовать следующий функционал: если с консоли введено значение
    1 - распарсить документ с помощью SAX, если с консоли введено значение
    2 - распарсить документ с помощью DOM*/

    static class Sonnet {
        public String firstName;
        public String lastName;
        public String title;
        public List<String> lines = new ArrayList<>();
    }

    static class SonnetSAXHandler extends DefaultHandler {
        private Sonnet sonnet = new Sonnet();
        private StringBuilder currentText = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            currentText.setLength(0);
        }

        public void characters(char[] ch, int start, int length) {
            currentText.append(ch, start, length); // Накопляем текст
        }
        @Override
        public void endElement(String uri, String localName, String qName) {
            String text = currentText.toString().trim();
            switch (qName) {
                case "firstName":
                    sonnet.firstName = text;
                    break;
                case "lastName":
                    sonnet.lastName = text;
                    break;
                case "title":
                    sonnet.title = text;
                    break;
                case "line":
                    sonnet.lines.add(text);
                    break;
            }
        }

        public Sonnet getSonnet() {
            return sonnet;
        }
    }

    private static Sonnet parseWithDOM(String filename) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filename));
        doc.getDocumentElement().normalize();

        Sonnet sonnet = new Sonnet();

        sonnet.firstName = getElementText(doc, "firstName");
        sonnet.lastName = getElementText(doc, "lastName");
        sonnet.title = getElementText(doc, "title");

        NodeList lineNodes = doc.getElementsByTagName("line");
        for (int i = 0; i < lineNodes.getLength(); i++) {
            sonnet.lines.add(lineNodes.item(i).getTextContent());
        }

        return sonnet;
    }

    private static Sonnet parseWithSAX(String filename) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        SonnetSAXHandler handler = new SonnetSAXHandler();
        saxParser.parse(new File(filename), handler);
        return handler.getSonnet();
    }

    private static String getElementText(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : "";
    }

    private static void writeToFile(Sonnet sonnet, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String line : sonnet.lines) {
                writer.write(line + System.lineSeparator());
            }
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите метод парсинга (1 - SAX, 2 - DOM):");
        int choice = scanner.nextInt();
        scanner.close();

        try {
            Sonnet sonnet = new Sonnet();

            if (choice == 1) {
                sonnet = parseWithSAX("sonnet.xml");
                System.out.println("Использован SAX парсер");
            } else {
                sonnet = parseWithDOM("sonnet.xml");
                System.out.println("Использован DOM парсер");
            }
            String fileName = sonnet.firstName + "_" + sonnet.lastName + "_" + sonnet.title + ".txt";
            writeToFile(sonnet, fileName);

            System.out.println("Файл " + fileName + " успешно создан!");
            System.out.println("Записано строк: " + sonnet.lines.size());

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}