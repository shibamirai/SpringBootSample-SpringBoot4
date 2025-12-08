# Spring 解体新書第 2 版の変更点(2025/12/8 時点)

本の執筆時点から SprignBoot 自身を始め多くのライブラリのバージョンが変わっています。それらバージョン変更に伴う修正点をここにまとめ、ソースコードとともにここに公開します。

## 1 章 Spring の概要

変更なし

## 2 章 開発環境の構築

Java25 が推奨されているため、Pleiades から最新の Eclipse をインストールします。これには STS や Lombok 等も含まれているため、Eclipse 以外は別途インストールする必要はありません。

### 2.4.1 プロジェクト作成

新規 Spring スターター・プロジェクトの設定値は以下。

- タイプ（本では型）は Maven
- Java バージョンは 25
- パッケージは com.example

新規 Spring スターター・プロジェクト依存関係の設定値は以下。

- Spring Boot バージョンは 4.0.0
- 追加するライブラリ

  | 分類                 | ライブラリ                                  |
  | -------------------- | ------------------------------------------- |
  | 開発者ツール         | Spring Boot DevTools<br>Lombok              |
  | SQL                  | JDBC API<br>Spring Data JDBC<br>H2 Database |
  | テンプレートエンジン | Thymeleaf                                   |
  | Web                  | Spring Web                                  |

## 3 章 Hello World ・・・簡単なサンプル

### 3.3 データベースから値を取得する

Spring 起動時に実行する SQL の設定項目が変更されています。

[application.properties]

変更前

```properties
spring.datasouce.username=sa
spring.datasouce.password=
spring.datasource.sql-script-encoding=UTF-8
spring.datasource.initialize=true
spring.datasource.schema=classpath:schema.sql
spring.datasource.data=classpath:data.sql
```

変更後

```properties
spring.datasource.username=sa
spring.datasource.password=
spring.sql.init.encoding=UTF-8
spring.sql.init.mode=ALWAYS
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql
```

## 4 章 Web アプリケーションの概要

変更なし

## 5 章 Dependency Injection(依存性の注入)

変更なし

## 6 章 バインド&バリデーション(入力チェック)

### 6.1.1 ライブラリの仕様・・・webjars

webjars-locator ではなく、後継の webjars-locator-lite を利用します。

[pom.xml]

変更前

```xml
<!-- webjars-locator -->
<dependency>
  <groupId>org.webjars</groupId>
  <artifactId>webjars-locator</artifactId>
  <version>0.52</version>
</dependency>
```

変更後

```xml
<!-- webjars-locator-lite -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>webjars-locator-lite</artifactId>
</dependency>
```

### 6.3.1 バリデーションの実装

Spring Boot 3 から JavaEE が JakartaEE 9 になったため、パッケージ名が javax.\* となっているものをすべて jakarta.\* に変更する必要があります。

[SignupForm.java]

変更前

```java
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
```

変更後

```java
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
```

(上記だけでなく、以降に出てくるすべてのコードの javax パッケージを変更します)

## 7 章 画面レイアウト

変更なし

## 8 章 MyBatis

### 8.2 MyBatis 基本編

MyBatis は Spring Boot 4 に対応したバージョンを使用します。ModelMapper も最新のものを使用します。

[pom.xml]

```xml
<!-- MyBatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>4.0.0</version>
</dependency>
<!-- Model Mapper -->
<dependency>
    <groupId>org.modelmapper.extensions</groupId>
    <artifactId>modelmapper-spring</artifactId>
    <version>3.2.6</version>
</dependency>
```

※ 2025/12/8 現在、下記の症状がありますが動作には問題ありません
- application.properties の `mybatis.mapper-locations` で「unknown property」と警告がでる
- modelmapper-spring 3.2.6 を使うと起動時に「A terminally deprecated method in sun.misc.Unsafe has been called」と Warning がでる
