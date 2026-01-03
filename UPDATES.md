# تحديثات مشروع SQL Parser

## 1. تحديث ملف SQLLexer.g4

أضفت هذين الـ tokens في ملف SQLLexer.g4 في قسم الكلمات المفتاحية:

```antlr
MATCHED: M A T C H E D;
TO: T O;
```

---

## 2. تحديث ملف testing.sql

غيرت السطر 12 من:

```sql
BEGIN_TRANSACTION;
```

إلى:

```sql
BEGIN TRANSACTION;
```

أو ببساطة:

```sql
BEGIN;
```

**السبب:** `BEGIN_TRANSACTION` ليس token واحد، بل كلمتان منفصلتان: `BEGIN` و `TRANSACTION`.

---

## 3. تحديث ملف SQLParser.g4

تم إعادة كتابة ملف SQLParser.g4 بالكامل مع الإصلاحات التالية:

### الإصلاحات الرئيسية:

#### أ. إزالة التكرار الأيسر المتبادل (Mutual Left Recursion)

- **المشكلة:** كانت قواعد `expression` و `predicate` تستدعي بعضها البعض مما يسبب خطأ في ANTLR
- **الحل:** دمج القاعدتين في قاعدة واحدة اسمها `expression` مع استخدام labeled alternatives

#### ب. إضافة جمل DML الأربعة:

1. **INSERT Statement:**

   ```sql
   INSERT INTO table (col1, col2) VALUES (val1, val2), (val3, val4);
   INSERT INTO table SELECT ...;
   ```

2. **UPDATE Statement:**

   ```sql
   UPDATE table SET col1 = val1, col2 = val2 WHERE condition;
   ```

3. **DELETE Statement:**

   ```sql
   DELETE FROM table WHERE condition;
   ```

4. **MERGE Statement:**

   ```sql
   MERGE INTO target USING source ON condition
   WHEN MATCHED THEN UPDATE SET ...
   WHEN NOT MATCHED THEN INSERT ...;
   ```

#### ج. دعم الأسماء المركبة (Qualified Names)

- **أضيفت قاعدة `qualifiedName`** لدعم:
  - `table.column`
  - `schema.table.column`
  - `database.schema.table.column`

#### د. تحسين التعبيرات (Expressions)

أضيفت أنواع تعبيرات جديدة:

- `EXISTS (SELECT ...)`
- `IN (value1, value2, ...)`
- `BETWEEN value1 AND value2`
- `LIKE 'pattern'`
- `IS NULL` / `IS NOT NULL`
- عمليات حسابية: `+`, `-`, `*`, `/`, `%`

#### هـ. معالجة الفاصلة المنقوطة (Semicolons)

- جعل الفاصلة المنقوطة اختيارية في كل مكان
- تم إصلاح التعارض بين `block` و `statement`

#### و. تحسين IF Statement

- **قبل:** `IF (condition) block`
- **بعد:** `IF (condition) block ELSE block`
- الآن يدعم `ELSE`

#### ز. إصلاح الأقواس

- تم تغيير `'('` و `')'` إلى `LPAREN` و `RPAREN` للتوحيد

#### ح. تحسين هيكل الـ Blocks

- **أضيفت قاعدة `singleStatement`** لفصل الجمل داخل الـ blocks
- **أضيفت قاعدة `statementList`** لإدارة قوائم الجمل
- الآن `block` يمكن أن يكون:
  - `{ statement1; statement2; }` (عدة جمل بين أقواس)
  - `statement` (جملة واحدة بدون أقواس)

#### ط. إصلاح Window Functions

- دعم `PARTITION BY` و `ORDER BY` في window specifications
- دعم `OVER()` clause مع aggregate functions

---

## 4. ملخص الأخطاء التي تم إصلاحها

### خطأ 1: Mutual Left Recursion

```Text
error(119): The following sets of rules are mutually left-recursive [expression, predicate]
```

**تم الإصلاح ✓**

### خطأ 2: Implicit Token Definitions

```Text
warning(125): implicit definition of token STRING in parser
warning(125): implicit definition of token NUMBER in parser
warning(125): implicit definition of token TO in parser
warning(125): implicit definition of token BEGIN_TRANSACTION in parser
```

**تم الإصلاح ✓** - تم استخدام tokens من الـ lexer وإضافة `TO` و `MATCHED`

### خطأ 3: Dot Notation Error

```Text
line 2:5 extraneous input '.' expecting ...
```

**تم الإصلاح ✓** - أضيفت قاعدة `qualifiedName`

### خطأ 4: EXISTS Error

```Text
line 13:4 extraneous input 'EXISTS' expecting ...
```

**تم الإصلاح ✓** - أضيف `EXISTS` expression

### خطأ 5: Semicolon Errors

```Text
extraneous input ';' expecting ...
```

**تم الإصلاح ✓** - تم جعل الفاصلة المنقوطة اختيارية

### خطأ 6: ELSE Error

```Text
line 16:2 extraneous input 'ELSE' expecting ...
```

**تم الإصلاح ✓** - تم دعم ELSE في IF statement

---

## 5. التغييرات في أولوية العمليات (Operator Precedence)

الأولوية من الأعلى إلى الأسفل:

1. `()` - الأقواس
2. `NOT` - النفي
3. `*`, `/`, `%` - الضرب والقسمة
4. `+`, `-` - الجمع والطرح
5. `=`, `!=`, `<`, `>`, `<=`, `>=` - المقارنات
6. `AND` - و المنطقية
7. `OR` - أو المنطقية

---

## 6. الملفات المُعدَّلة

- ✅ **SQLLexer.g4** - تم إضافة `MATCHED` و `TO`
- ✅ **SQLParser.g4** - استبدل الملف بالكامل
- ✅ **testing.sql** - تم تغيير `BEGIN_TRANSACTION` إلى `BEGIN TRANSACTION`
- ✅ **TestParser.java** - لم يعدل

---
