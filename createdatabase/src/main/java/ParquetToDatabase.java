import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.schema.MessageType;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import com.google.gson.Gson; // 新增 Gson 导入

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//mvn clean compile exec:java

public class ParquetToDatabase {
    public static List<Group> readParquetFile(String filePath) throws IOException {
    List<Group> records = new ArrayList<>();
    Configuration conf = new Configuration();
    Path path = new Path(filePath);

    try (ParquetFileReader reader = ParquetFileReader.open(conf, path)) {
        System.out.println("Schema: " + reader.getFooter().getFileMetaData().getSchema());
    }

    try (ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), path).withConf(conf).build()) {
        Group record;
        while ((record = reader.read()) != null) {
            records.add(record);
        }
    }
    return records;
}

    public static void insertToDatabase(List<Group> records, String dbUrl, String user, String password) throws SQLException {
    String insertSql = "INSERT INTO qa (image, question, choices, answer, hint, task, grade, subject, topic, category, skill, lecture, solution) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    Gson gson = new Gson(); // 用于 choices 转 JSON
    try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
         PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
        conn.setAutoCommit(false);
        for (Group record : records) {
            // image: 获取嵌套 group 的 bytes (binary) 或 path (string)
            byte[] imageBytes = null;
            if (record.getFieldRepetitionCount("image") > 0) {
                Group imageGroup = record.getGroup("image", 0);
                if (imageGroup.getFieldRepetitionCount("bytes") > 0) {
                    imageBytes = imageGroup.getBinary("bytes", 0).getBytes();
                }
            }
            if (imageBytes != null) {
                pstmt.setBytes(1, imageBytes);
            } else {
                pstmt.setNull(1, java.sql.Types.BLOB);
            }

            // question
            pstmt.setString(2, record.getString("question", 0));

            // choices: 获取嵌套 list 并转为 JSON
            String choicesJson = "[]";
            if (record.getFieldRepetitionCount("choices") > 0) {
                Group choicesGroup = record.getGroup("choices", 0);
                int listCount = choicesGroup.getFieldRepetitionCount("list");
                List<String> choiceList = new ArrayList<>();
                for (int i = 0; i < listCount; i++) {
                    Group listItemGroup = choicesGroup.getGroup("list", i);
                    if (listItemGroup.getFieldRepetitionCount("element") > 0) {
                        choiceList.add(listItemGroup.getString("element", 0)); // ✅ 正确访问 element
                    }
                }
                choicesJson = gson.toJson(choiceList);
            }
            pstmt.setString(3, choicesJson);

            // answer
            pstmt.setInt(4, record.getInteger("answer", 0));

            // hint
            pstmt.setString(5, record.getString("hint", 0));

            // task
            pstmt.setString(6, record.getString("task", 0));

            // grade
            pstmt.setString(7, record.getString("grade", 0));

            // subject
            pstmt.setString(8, record.getString("subject", 0));

            // topic (使用默认值如果缺失)
            pstmt.setString(9, record.getFieldRepetitionCount("topic") > 0 ? record.getString("topic", 0) : "N/A");

            // category
            pstmt.setString(10, record.getFieldRepetitionCount("category") > 0 ? record.getString("category", 0) : "N/A");

            // skill
            pstmt.setString(11, record.getFieldRepetitionCount("skill") > 0 ? record.getString("skill", 0) : "N/A");

            // lecture
            pstmt.setString(12, record.getFieldRepetitionCount("lecture") > 0 ? record.getString("lecture", 0) : "N/A");

            // solution
            pstmt.setString(13, record.getFieldRepetitionCount("solution") > 0 ? record.getString("solution", 0) : "N/A");

            pstmt.addBatch();
        }
        pstmt.executeBatch();
        conn.commit();
    }
}

    public static void main(String[] args) {
        // String parquetFilePath = "D:\\ADproject\\parquet\\scienceqa.parquet";
        String parquetFilePath = "D:\\ADproject\\parquet\\scienceqa.parquet";
        String dbUrl = "jdbc:mysql://localhost:3306/collaborative_practice_platform?useSSL=false";
        String user = "root";
        String password = "wqx021201";

        try {
            List<Group> records = readParquetFile(parquetFilePath);
            insertToDatabase(records, dbUrl, user, password);
            System.out.println("数据成功插入数据库！");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}