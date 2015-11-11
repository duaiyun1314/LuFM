package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGeneratorUtil {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.andy.LuFM.dbutil");
        //addEntity(schema);
        new DaoGenerator().generateAll(schema, "D:\\work\\myproject\\LuFm\\app\\src\\main\\java-gen");

    }

    private static void addEntity(Schema schema) {
        Entity topicItem = schema.addEntity("TopicItem");
        topicItem.setTableName("topic");
        //topicItem.addIdProperty();
        topicItem.addBooleanProperty("saved");
        topicItem.addStringProperty("topicId");
        topicItem.addStringProperty("topicName");
        topicItem.addStringProperty("topicImage");
        topicItem.addStringProperty("latter");
    }
}
