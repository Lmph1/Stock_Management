import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.IOException;
import java.util.Scanner;

public class Helper {

    static String inputText(String title) {
        System.out.print(title);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    static int inputInteger(String title) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(title);
            String number = scanner.next();
            if (!number.matches("[0-9]+")) {
                System.out.println("INPUT IS INVALID.");
            } else {
                return Integer.parseInt(number);
            }
        }
    }

    static int parseInt(String text) throws Exception {
        if (!text.matches("[0-9]+")) {
            throw new Exception("INPUT IS INVALID.");
        } else return Integer.parseInt(text);
    }

    static double inputDouble(String title) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(title);
            String number = scanner.next();
            if (!number.matches("\\d+\\.\\d*|\\.?\\d+")) {
                System.out.println("INPUT IS INVALID.");
            } else {
                return Double.parseDouble(number);
            }
        }
    }

    static double parseDouble(String text) throws Exception {
        if (!text.matches("\\d+\\.\\d*|\\.?\\d+")) {
            throw new Exception("INPUT IS INVALID.");
        } else return Double.parseDouble(text);
    }

    static int yesNoMessage(String title) {
        Table table = new Table(1, BorderStyle.DESIGN_PAPYRUS, ShownBorders.SURROUND);
        CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
        table.setColumnWidth(0, 30, 500);
        table.addCell(title, cellStyle);
        table.addCell("(1)YES      (0)NO)", cellStyle);
        System.out.println(table.render());
        return inputInteger("=> Please enter: ");
    }

    static void showMessage(String title) {
        try {
            Table table = new Table(1, BorderStyle.DESIGN_PAPYRUS, ShownBorders.SURROUND);
            CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
            table.setColumnWidth(0, 30, 500);
            if (title != null)
                table.addCell(title, cellStyle);
            table.addCell("Press 'Enter' to continue", cellStyle);
            System.out.println(table.render());
            System.in.read();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
