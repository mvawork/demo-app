package ru.menshevva.demoapp.dto.metadata;


public enum ReferenceFieldType {
    FIELD_TYPE_STRING("Строка"),        // Строковый тип (String)
    FIELD_TYPE_INTEGER("Целое число"),  // Целочисленный тип (int)
    FIELD_TYPE_DOUBLE("Дробное число"), // Число с плавающей точкой (double)
    FIELD_TYPE_LONG("Длинное целое"),   // Длинное целое число (long)
    FIELD_TYPE_BOOLEAN("Булево"),       // Булевый тип (boolean)
    FIELD_TYPE_DATE("Дата"),            // Дата (java.sql.Date)
    FIELD_TYPE_TIMESTAMP("Временная метка"), // Временная метка (java.sql.Timestamp)
    FIELD_TYPE_BIGDECIMAL("Точный десятичный"), // Точный десятичный тип (BigDecimal)
    FIELD_TYPE_BYTE("Байт"),            // Байтовый тип (byte[])
    FIELD_TYPE_FLOAT("Дробное число float"), // Число с плавающей точкой (float)
    FIELD_TYPE_SHORT("Короткое целое"), // Короткое целое число (short)
    FIELD_TYPE_CHAR("Символ"),          // Символьный тип (char)
    FIELD_TYPE_BYTE_ARRAY("Массив байтов"), // Массив байтов (byte[])
    FIELD_TYPE_OBJECT("Объект"),        // Объектный тип
    FIELD_TYPE_UNKNOWN("Неизвестный");  // Неизвестный тип

    private final String typeName;

    ReferenceFieldType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public static ReferenceFieldType getForName(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (var v : values()) {
            if (value.equals(v.name())) {
                return v;
            }
        }
        return null;
    }
}