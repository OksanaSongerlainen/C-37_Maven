package Lesson_23;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class main {
    /*
        Написать программу для парсинга xml документа. Необходимо распарсить xml документ и
        содержимое тегов line записать в другой документ. Название файла для записи должно
        состоять из значений тегов и имеет вид: <firstName>_<lastName>_<title>.txt
    */

    static class Sonnet {
        public String firstName;
        public String lastName;
        public String title;
        public List<String> lines = new ArrayList<>();
    }

    public static void main(String[] args) {
        try {DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(new File("sonnet.xml"));
        doc.getDocumentElement().normalize();

        Sonnet sonnet = new Sonnet();

        NodeList authorNodes = doc.getElementsByTagName("author");
        if (authorNodes.getLength() > 0) {
            Element authorElement = (Element) authorNodes.item(0);

            NodeList firstNameNodes = authorElement.getElementsByTagName("firstName");
            if (firstNameNodes.getLength() > 0) {
                sonnet.firstName = firstNameNodes.item(0).getTextContent();
            }
            NodeList lastNameNodes = authorElement.getElementsByTagName("lastName");
            if (lastNameNodes.getLength() > 0) {
                sonnet.lastName = lastNameNodes.item(0).getTextContent();
            }
        }
NodeList titleNodes = doc.getElementsByTagName("title");
        if (titleNodes.getLength() > 0) {
            sonnet.title = titleNodes.item(0).getTextContent();
        }
        NodeList lineNodes = doc.getElementsByTagName("line");
        for (int i = 0; i < lineNodes.getLength(); i++) {
            String line = lineNodes.item(i).getTextContent();
            sonnet.lines.add(line);
        }

        String fileName = sonnet.firstName + "_" + sonnet.lastName + "_" + sonnet.title + ".txt";

        try (FileWriter writer = new FileWriter(fileName)) {
            for (String line : sonnet.lines) {
                writer.write(line + System.lineSeparator());
            }
            System.out.println("Файл " + fileName + " успешно создан!");
        }
    } catch (ParserConfigurationException | SAXException | IOException e) {
        System.err.println("Ошибка при обработке XML: " + e.getMessage());
    }
}
}
