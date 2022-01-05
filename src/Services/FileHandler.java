package Services;

import java.io.*;
import java.util.Scanner;

public class FileHandler {

    /**
     * Đọc dữ liệu từ File có đường dẫn path
     * @param path đường dẫn FILE
     * @return String - dữ liệu đọc từ FIle, trả về NULL nếu không thể mở file
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfStringBuilder")
    public static String read(String path) {
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            Scanner scanner = new Scanner(fileInputStream);
            //Đọc và thêm từng dòng dữ liệu từ file vào StringBuilder
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            fileInputStream.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File not found !");
            return ""; //trả về chuỗi null nếu không thể đọc file
        }
    }

    /**
     * Viết dữ liệu vào File có đường dẫn path
     * @param path đường dẫn FILE
     * @param data dữ liệu đầu vào kiểu String muốn viết vào FILE
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void write(String path, String data, boolean isAppend) {
        try {
            File yourFile = new File(path);
            yourFile.createNewFile(); //Tạo file nếu ko tồn tại
            FileOutputStream fileOutputStream = new FileOutputStream(yourFile, isAppend);
            //Chuyễn dữ liệu kiểu String sang byte[] để viết vào file
            byte[] bytes = data.getBytes();
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File not found !");
        }
    }
}
