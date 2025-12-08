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
