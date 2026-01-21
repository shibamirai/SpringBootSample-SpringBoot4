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
- application.properties の mybatis に関する設定 で「unknown property」と警告がでる
- modelmapper-spring 3.2.6 を使うと起動時に「A terminally deprecated method in sun.misc.Unsafe has been called」と Warning がでる

## 9 章 AOP

変更なし

## 10 章 エラー処理

変更なし

## 11 章 Spring セキュリティ

### 11.2.1 直リンクの禁止

Spring Boot 4 の Spring-Boot-Starter-Security では Spring セキュリティのバージョンは 7 となります。それに合わせて Thymeleaf 拡張ライブラリ(セキュリティ)は Thymeleaf-Extras-SpringSecurity6 とします。

[pom.xml]

```xml
<!-- SpringSecurity -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!-- Thymeleaf拡張ライブラリ-->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

SpringSecurity 5.7 以降から、セキュリティ設定クラスの書き方が大きく変更されています。

- WebSecurityConfigurerAdapter を継承せず、configure で行っている HttpSecurity http へのセキュリティ設定は SecurityFilterChain を Bean 定義して行う
- webjars や css などはセキュリティ対象外として設定するのではなく、ログイン不要ページとして設定する
- authorizeRequests()ではなく authorizeHttpRequests()を使う
- 設定はラムダ式で記述する
- antMatchers()ではなく requestMatchers()を使う
- 一般的な静的リソースの場所の指定(/webjars/\*\*, /css/\*\*, /js/\*\*)は、PathRequest.toStaticResources().atCommonLocations() としてまとめて指定する
- "/login" への直リンク許可設定は、次節のログイン処理設定で行うためここではまだ行わない
- csrf().disable() は非推奨となったため、ラムダ式で csrf(csrf -> csrf.disabe()) のように指定する
- H2 コンソールのパス("/h2-console/\*\*")は、PathRequest.toH2Console() として指定する
- H2 コンソールを表示させるためには、さらに以下の設定が必要

  ```java
  http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));
  http.csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()));
  ```

以下では H2 コンソールに対するセキュリティ設定を、別の Bean として独立させています。ですので H2 データベースを使わない場合は下記の h2ConsoleSecurityFilterChain は不要です。（securityFilterChain の @Order(2) も必要ありません）

[SecurityConfig.java]

```java
package com.example.config;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	/** H2 コンソール用のセキュリティ設定 */
	@Bean
	@Order(1)
    SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher(PathRequest.toH2Console())
			.authorizeHttpRequests(authorize -> authorize
				.anyRequest().permitAll()
			)
			.headers(headers -> headers
				.frameOptions(FrameOptionsConfig::disable)
			)
			.csrf(csrf -> csrf
				.ignoringRequestMatchers(PathRequest.toH2Console())
			);
		return http.build();
	}

	/** このアプリのセキュリティ設定 */
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers("/user/signup").permitAll()
				.anyRequest().authenticated()
			)
			// CSRF 対策を無効に設定 (一時的)
			.csrf(csrf -> csrf
		        .disable()
			);
		return http.build();
	}
}
```

#### 403 エラーの画面

authorizeHttpRequests()を使用するようになったことで、直リンクをしたときは 403 エラーの共通画面ではなく、ログインページにリダイレクトするようになっています。
ただし、ここではまだログイン処理を実装していない(11.2.2 で実装)ためリダイレクトされず、403 のエラーコードだけが返されるようになっているため、アプリで用意した共通エラー画面ではなくブラウザのエラー画面が表示されます。
（403 エラーは 11.3「認可」のところで出すことができます。）

### 11.2.2 ログイン処理

セキュリティ設定クラスの http.formLogin() もラムダ式で設定します。

[SecurityConfig.java]

```java
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	...(省略)

	/** このアプリのセキュリティ設定 */
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/user/signup").permitAll()
				.anyRequest().authenticated()
			)
	        // 変更点 ここから
			.formLogin(login -> login
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("userId")
				.passwordParameter("password")
				.defaultSuccessUrl("/user/list", true)
				.failureUrl("/login?error")
			)
	        // ここまで
			// CSRF 対策を無効に設定 (一時的)
			.csrf(csrf -> csrf
		        .disable()
			);
		return http.build();
	}
}
```

### 11.2.3 インメモリ認証

インメモリ認証の設定は auth.inMemoryAuthentication() メソッドではなく、 InMemoryUserDetailsManager を Bean 定義して行うように変更します。この中では SpringSecurity で用意されている User クラスを使って user と admin の２人のユーザーを作成しています。

[SecurityConfig.java]

```java
package com.example.config;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    // 変更点 ここから
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // ここまで

	/** H2 コンソール用のセキュリティ設定 */
	@Bean
	@Order(1)
    SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        ...(省略)
    }

	/** このアプリのセキュリティ設定 */
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        ...(省略)
    }

    // 変更点 ここから
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("user")
                .roles("GENERAL")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password("admin")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    // ここまで
}
```

#### ログイン失敗時のメッセージ変更

バージョンが上がってからデフォルトで Spring が用意しているメッセージソースが使用されようになったため、massages.properties の変更だけではメッセージは変更されません。メッセージを変更するには下記の Bean 定義を追加して、AuthenticationProvider のメッセージソースを変更してやる必要があります。

[SecurityConfig.java]

```java
package com.example.config;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 変更点ここから
    @Bean
    AuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService, MessageSource messageSource) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setMessageSource(messageSource);

        return provider;
    }
    // ここまで

	...(省略)
```

※ この変更により、コンソールに WARN メッセージ（Global AuthenticationManager configured with an AuthenticationProvider bean.…）が出るようになります。UserDetailsService を @Bean 化せずこの場で作成するようにすれば出なくなりますが、変更が多岐に渡って本の記述と対応が取りづらくなるのでこのままにしておきます。

### 11.2.4 パスワードの暗号化

パスワードの暗号化は本の通りです。

[SecurityConfig.java]

```java
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	/** H2 コンソール用のセキュリティ設定 */
	@Bean
	@Order(1)
    SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        ...(省略)
    }

	/** このアプリのセキュリティ設定 */
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        ...(省略)
    }

    // 変更点 ここから
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();

        UserDetails user = User.withUsername("user")
			.password(encoder.encode("user"))
			.roles("GENERAL")
			.build();
        UserDetails admin = User.withUsername("admin")
			.password(encoder.encode("admin"))
			.roles("ADMIN")
			.build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    // ここまで
}
```

### 11.2.5 ユーザーデータ認証

本の通りに UserDetailService の実装クラス UserDetailServiceImpl を作成すれば、SecurityConfig から InMemoryUserDetailsManager の Bean 定義を削除するだけでユーザーデータ認証が行われるようになります。

[SecurityConfig.java]

```java
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    ...(省略)

    // 変更点 ここから
    /*
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();

        UserDetails user = User.withUsername("user")
			.password(encoder.encode("user"))
			.roles("GENERAL")
			.build();
        UserDetails admin = User.withUsername("admin")
			.password(encoder.encode("admin"))
			.roles("ADMIN")
			.build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    */
    // ここまで
}
```

### 11.2.6 ログアウト処理

http.logout()もラムダ式で記述します。http.\*() のメソッドはメソッドチェーンで繋げて書くことができます。

[SecurityConfig.java]

```java
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    ...(省略)

	/** このアプリのセキュリティ設定 */
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/user/signup").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(login -> login
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("userId")
				.passwordParameter("password")
				.defaultSuccessUrl("/user/list", true)
				.failureUrl("/login?error")
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
	        // 変更点 ここから
			// )
			// // CSRF 対策を無効に設定 (一時的)
			// .csrf(csrf -> csrf
		    //     .disable()
	        // ここまで
			);
		return http.build();
	}

    ...(省略)
}
```

### 11.2.7 CSRF 対策

[SecurityConfig.java]

```java
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    ...(省略)

	/** このアプリのセキュリティ設定 */
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/user/signup").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(login -> login
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("userId")
				.passwordParameter("password")
				.defaultSuccessUrl("/user/list", true)
				.failureUrl("/login?error")
			)
	        // 変更点 ここから
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
			)
	        // ここまで
			// CSRF 対策を無効に設定 (一時的)
			.csrf(csrf -> csrf
		        .disable()
			);
		return http.build();
	}

    ...(省略)
}
```

### 11.3.1 URL の認可

セキュリティ設定クラスへの URL 認可の設定も、antMatchers() ではなく requestMatchers() を使います。

[SecurityConfig.java]

```java
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    ...(省略)

	/** このアプリのセキュリティ設定 */
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/user/signup").permitAll()
                // 変更点 ここから
                .requestMatchers("/admin").hasAuthority("ROLE_ADMIN")
                // ここまで
				.anyRequest().authenticated()
			)
			.formLogin(login -> login
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("userId")
				.passwordParameter("password")
				.defaultSuccessUrl("/user/list", true)
				.failureUrl("/login?error")
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
			// )
			// // CSRF 対策を無効に設定 (一時的)
			// .csrf(csrf -> csrf
		    //     .disable()
			);
		return http.build();
	}

    ...(省略)
}
```

## 12 章 REST

### 12.2.3 検索

DataTables は新しいバージョンが使えるので 2.3.2 (2025/12/9 時点の最新) を使用します。それに伴い list.html で読み込むファイル名や、list.js での DataTables の言語設定ファイル名が変わっています。

[pom.xml]

```xml
<!-- datatables -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>datatables</artifactId>
    <version>2.3.2</version>
</dependency>
<!-- datatables-plugins -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>datatables-plugins</artifactId>
    <version>2.3.2</version>
    <scope>runtime</scope>
</dependency>
```

[list.html]

```HTML
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
	xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
	layout:decorate="~{layout/layout}">
<head>
	<title>ユーザー一覧</title>
	<!-- 個別CSS読込 -->
	<link rel="stylesheet" th:href="@{/css/user/list.css}">
	<!-- 個別JS読込 -->
	<!-- 変更点 ここから -->
	<link rel="stylesheet" th:href="@{/webjars/datatables/css/dataTables.jqueryui.min.css}">
	<script th:src="@{/webjars/datatables/js/dataTables.min.js}" defer></script>
	<!-- ここまで -->
	<script th:src="@{/js/user/list.js}" defer></script>
</head>
...(省略)
```

[list.js]

```javascript
...(省略)

/** DataTables作成 */
function createDataTables() {
	
	// 既にDataTablesが作成されている場合
	if (table != null) {
		// DataTables破棄
		table.destroy();
	}

	// DataTables作成
    table = $('#user-list-table').DataTable({
		// 日本語化
		language: {
			// 変更点 ここから
			url: '/webjars/datatables-plugins/i18n/ja.json'
			// ここまで
		},
        ...(省略)
    });
}
```

## 13 章 Spring Data JPA

javax パッケージは jakarta パッケージに修正してください。(MUser, Department, SalaryKey, Salary の 4 クラス)

### 13.2.1 CRUD

JPA の設定が変更になっています。

[application.properties]

```properties
#=====================
# JPA
#=====================
# デーブル自動作成
spring.jpa.hibernate.ddl-auto=none
# SQLログ出力
spring.jpa.show-sql=true
# ログのSQL文を見やすくフォーマットする
spring.jpa.properties.hibernate.format_sql=true
# バインドパラメーター出力
logging.level.org.hibernate.orm.jdbc.bind=trace
# 起動時の警告をなくすために明示的に有効にしておく
spring.jpa.open-in-view=true
```

## その他

### No static resource favicon.ico for request '/favicon.ico'.

ファビコン(favicon)とは、ウェブサイトのシンボルマークとしてブラウザのタブなどに表示されるアイコンのことです。Spring Boot で作成したアプリでは、もともとデフォルトのファビコンが用意されていましたが、このデフォルトアイコンも情報漏洩にあたる（Spring Boot で作成したことが分かる）ことから、現在は提供されていません。

ファビコンはサイトを参照するとブラウザから自動的に要求されるため、ファビコンが見つからないと、アプリは NoResourceFoundException という例外を発生させ、上記のログが出力されます。

これを回避するには２つの方法があります。

#### 方法 1. 自前のファビコンを用意する

自分でファビコンを作成し、`src/main/resource/static/favicon.ico` に配置します。

#### 方法 2. カスタムコントローラーで対応する

ファビコンへのリクエストを処理するカスタムコントローラーを作成し、何も返さないことでリクエストを無視します。

```java
public class FaviconController {

	@GetMapping("favicon.ico")
	@ResponseBody
	void returnNoFavicon() {
		// 何もしない		
	}
}
```
