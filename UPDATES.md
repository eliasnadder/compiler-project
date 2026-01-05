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

### الإصلاحات الرئيسية

#### أ. إزالة التكرار الأيسر المتبادل (Mutual Left Recursion)

- **المشكلة:** كانت قواعد `expression` و `predicate` تستدعي بعضها البعض مما يسبب خطأ في ANTLR
- **الحل:** دمج القاعدتين في قاعدة واحدة اسمها `expression` مع استخدام labeled alternatives

#### ب. إضافة جمل DML الأربعة

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

## 6. دعم التعليقات المتداخلة (Nested Comments)

### ما هي التعليقات المتداخلة؟

التعليقات المتداخلة هي تعليقات داخل تعليقات، مثل:

```sql
/* تعليق خارجي (مستوى 1)
   /* تعليق داخلي (مستوى 2)
      /* تعليق أعمق (مستوى 3) */
   نهاية مستوى 2 */
نهاية مستوى 1 */

SELECT * FROM users;
```

### المشكلة في الكود القديم

**القاعدة القديمة:**

```antlr
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
```

**المشكلة:** تستخدم `.*?` (non-greedy match) التي تتوقف عند **أول** `*/` تجده، مما يسبب:

- ✅ التعليق الخارجي يُقرأ بشكل خاطئ
- ❌ التعليق الداخلي ينتهي مبكراً
- ❌ باقي النص يُعامل كـ كود عادي (خطأ!)

**مثال على الخطأ:**

```sql
/* تعليق خارجي
   /* تعليق داخلي */  <-- الـ lexer يتوقف هنا!
   هذا النص سيُقرأ كـ SQL! */ <-- خطأ!
```

### الحل: استخدمت Lexer Modes

تم تعديل SQLLexer.g4 لدعم التعليقات المتداخلة باستخدام **modes**:

```antlr
BLOCK_COMMENT_START: '/*' -> pushMode(BLOCK_COMMENT_MODE), skip;

mode BLOCK_COMMENT_MODE;

BLOCK_COMMENT_NESTED: '/*' -> pushMode(BLOCK_COMMENT_MODE), skip;
BLOCK_COMMENT_END: '*/' -> popMode, skip;
BLOCK_COMMENT_CONTENT: . -> skip;
```

### كيف يعمل الحل؟

#### 1. **pushMode** - الدخول في وضع التعليق

عندما يرى الـ lexer رمز `/*`:

```anltr
BLOCK_COMMENT_START: '/*' -> pushMode(BLOCK_COMMENT_MODE), skip;
```

- ينتقل إلى `BLOCK_COMMENT_MODE`
- يحفظ الوضع الحالي في stack

#### 2. **التعليقات المتداخلة**

إذا وجد `/*` آخر **داخل** التعليق:

```antlr
BLOCK_COMMENT_NESTED: '/*' -> pushMode(BLOCK_COMMENT_MODE), skip;
```

- يدخل مستوى جديد من التعليق
- يضيف وضع جديد إلى الـ stack

#### 3. **popMode** - الخروج من وضع التعليق

عندما يجد `*/`:

```antlr
BLOCK_COMMENT_END: '*/' -> popMode, skip;
```

- يخرج من مستوى واحد (يحذف من الـ stack)
- إذا كان هناك مستويات أخرى، يستمر في وضع التعليق
- إذا انتهت كل المستويات، يعود للوضع العادي

#### 4. **BLOCK_COMMENT_CONTENT** - محتوى التعليق

أي محرف آخر داخل التعليق:

```antlr
BLOCK_COMMENT_CONTENT: . -> skip;
```

- يتم تجاهله (skip)

### مثال توضيحي مع الخطوات

```sql
/* مستوى 1
   /* مستوى 2
      /* مستوى 3 */
   نهاية 2 */
نهاية 1 */
SELECT 1;
```

**خطوات المعالجة:**

| الخطوة | الرمز المقروء | الإجراء | Stack العمق |
|--------|--------------|---------|-------------|
| 1 | `/*` | `pushMode` → دخول مستوى 1 | `[COMMENT]` |
| 2 | `مستوى 1` | `skip` | `[COMMENT]` |
| 3 | `/*` | `pushMode` → دخول مستوى 2 | `[COMMENT, COMMENT]` |
| 4 | `مستوى 2` | `skip` | `[COMMENT, COMMENT]` |
| 5 | `/*` | `pushMode` → دخول مستوى 3 | `[COMMENT, COMMENT, COMMENT]` |
| 6 | `مستوى 3` | `skip` | `[COMMENT, COMMENT, COMMENT]` |
| 7 | `*/` | `popMode` → خروج من مستوى 3 | `[COMMENT, COMMENT]` |
| 8 | `نهاية 2` | `skip` | `[COMMENT, COMMENT]` |
| 9 | `*/` | `popMode` → خروج من مستوى 2 | `[COMMENT]` |
| 10 | `نهاية 1` | `skip` | `[COMMENT]` |
| 11 | `*/` | `popMode` → خروج من مستوى 1 | `[]` (عودة للوضع العادي) |
| 12 | `SELECT 1;` | **قراءة كـ SQL عادي** ✅ | `[]` |

### الفرق بين الطريقة القديمة والجديدة

| الميزة | الطريقة القديمة | الطريقة الجديدة |
|--------|-----------------|-----------------|
| **تعليق بسيط** | ✅ يعمل | ✅ يعمل |
| **تعليق متداخل** | ❌ لا يعمل | ✅ يعمل |
| **عمق غير محدود** | ❌ | ✅ |
| **الأداء** | سريع جداً | سريع (overhead بسيط) |

### أمثلة اختبار

#### ✅ مثال 1: تعليق متداخل بسيط

```sql
/* خارجي /* داخلي */ خارجي مرة أخرى */
SELECT * FROM users;
```

**النتيجة:** يتجاهل كل التعليق، يقرأ `SELECT` فقط ✅

#### ✅ مثال 2: تعليقات متعددة المستويات

```sql
/* 
   مستوى 1
   /* مستوى 2
      /* مستوى 3
         /* مستوى 4 */
      */
   */
*/
INSERT INTO products VALUES (1, 'Phone');
```

**النتيجة:** يتجاهل كل التعليقات، يقرأ `INSERT` فقط ✅

#### ✅ مثال 3: تعليقات متداخلة مع كود SQL

```sql
SELECT name /* /* هذا تعليق */ */ FROM users;
```

**النتيجة:** `SELECT name FROM users;` ✅

#### ❌ مثال 4: ما كان يحدث قبل التعديل

```sql
/* خارجي /* داخلي */ يُقرأ هذا كـ SQL! */
```

**الطريقة القديمة:**

- يتوقف عند أول `*/`
- يقرأ `يُقرأ هذا كـ SQL! */` كـ كود ❌ **خطأ!**

**الطريقة الجديدة:**

- يستمر حتى آخر `*/`
- يتجاهل كل النص ✅ **صحيح!**

### ملاحظات تقنية

#### 1. **Stack-based Processing**

الـ lexer يستخدم **stack** لتتبع مستويات التعليقات:

- كل `/*` يضيف مستوى (push)
- كل `*/` يحذف مستوى (pop)
- عندما يفرغ الـ stack، نعود للوضع العادي

#### 2. **Mode Isolation**

القواعد داخل `BLOCK_COMMENT_MODE` **معزولة** عن الوضع العادي:

- لا تتداخل مع tokens الأخرى
- تعمل فقط عندما نكون داخل تعليق

#### 3. **Performance**

- الأداء ممتاز: O(n) حيث n = طول التعليق
- لا يؤثر على سرعة معالجة SQL العادي
- Overhead بسيط جداً

### دعم قواعد البيانات

معظم قواعد البيانات **لا تدعم** التعليقات المتداخلة افتراضياً:

| قاعدة البيانات | الدعم الافتراضي | ملاحظات |
|----------------|-----------------|---------|
| MySQL | ❌ لا | تستخدم `/* */` بسيط فقط |
| PostgreSQL | ❌ لا | يمكن تفعيله بإعدادات خاصة |
| SQL Server | ❌ لا | لا يدعم التعليقات المتداخلة |
| Oracle | ❌ لا | تعليقات بسيطة فقط |
| SQLite | ❌ لا | تعليقات بسيطة فقط |

**لكن** إضافة هذه الميزة في الـ parser:

- ✅ يجعل الـ lexer أكثر قوة ومرونة
- ✅ يساعد في معالجة SQL المُولَّد آلياً
- ✅ مفيد للأدوات التي تُنشئ SQL ديناميكياً
- ✅ يمنع أخطاء parsing في حالات نادرة

### الفوائد المكتسبة

1. ✅ **معالجة صحيحة** للتعليقات المعقدة
2. ✅ **لا أخطاء parsing** في الحالات النادرة
3. ✅ **توافق أفضل** مع SQL المُولَّد
4. ✅ **قوة أكبر** للـ lexer
5. ✅ **احترافية** في التعامل مع edge cases

## الخلاصة

التعديل يضيف دعماً كاملاً للتعليقات المتداخلة باستخدام **lexer modes**، مما يجعل الـ parser أكثر قوة ومرونة في معالجة جميع أشكال التعليقات، حتى في الحالات المعقدة والنادرة.

---

## 7. الملفات المُعدَّلة

- ✅ [**SQLLexer.g4**](src\main\antlr4\com\example\SQLLexer.g4)
  - تم إضافة `MATCHED` و `TO`
  - تم إضافة التعليقات المتداخلة في نهاية الملف
- ✅ [**SQLParser.g4**](src\main\antlr4\com\example\SQLParser.g4)
  - إصلاح مجمعة من الأخطاء
- ✅ [**testing.sql**](testing.sql)
  - تم تغيير `BEGIN_TRANSACTION` إلى `BEGIN TRANSACTION`
  - إضافة أمثلة عن التعليقات المتداخلة

---
