---
layout: ja
title: "Bolt の概要"
lang: ja
---

# Bolt の概要

**Bolt for Java** は、最新のプラットフォーム機能を使った Slack アプリの開発をスピーディに行うための抽象レイヤーを提供するフレームワークです。

このガイドは、Bolt を使ったアプリ開発の基礎的な内容を全てカバーします。なお Slack アプリ開発全般についてまだ不慣れな方は、まず「[An introduction to Slack apps（英語）](https://api.slack.com/start/overview)」に軽く目を通した方がよいかもしれません。

## App クラス

**App** クラスは、些末なことに煩わされることなく、その Slack アプリの本質的なロジックだけを書くことができる場所です。

**App** インスタンスを設定していくコードは、主に Slack から受信したイベント（アクション、コマンド実行、セレクトメニューの選択肢の読み込み、Events API で購読した Slack 内でのイベントなど）へどう応答するかの定義で構成されます。

```java
import com.slack.api.bolt.App;

App app = new App();
app.command("/echo", (req, ctx) -> {
  return ctx.ack(req.getText());
});
```

## イベントのディスパッチ

以下は、利用可能なイベントをディスパッチするためのメソッドの一覧です。

|メソッド|ディスパッチの条件 (値: 型)|説明|
|-|-|-|
|**app.event**|イベントデータ型: **Class\<Event\>**|[**イベント API**]({{ site.url | append: site.baseurl }}/guides/ja/events-api): 購読しているあらゆる bot/user events に応答します。|
|**app.command**|コマンド名: **String** \| **Pattern**|[**スラッシュコマンド**]({{ site.url | append: site.baseurl }}/guides/ja/slash-commands): スラッシュコマンドの実行に応答します。|
|**app.messageAction**|callback_id: **String** \| **Pattern**|[**アクション**]({{ site.url | append: site.baseurl }}/guides/ja/actions): メッセージメニューのアクション実行に応答します。|
|**app.blockAction**|action_id: **String** \| **Pattern**|[**インタラクティブコンポーネント**]({{ site.url | append: site.baseurl }}/guides/ja/interactive-components): **blocks** 内でのボタンクリック、セレクトメニューからの選択、ラジオボタン選択などユーザクアションに応答します。これらのイベントは全てのインターフェース（メッセージ、モーダル、Home タブ）で発火します。|
|**app.blockSuggestion**|action_id: **String** \| **Pattern**|[**インタラクティブコンポーネント**]({{ site.url | append: site.baseurl }}/guides/ja/interactive-components): **blocks** 内の external data source を使ったセレクトメニュー内でユーザーが `min_query_length` 以上の長さのキーワードを入力したときに表示する選択肢を応答します。|
|**app.viewSubmission**|callback_id: **String** \| **Pattern**|[**モーダル**]({{ site.url | append: site.baseurl }}/guides/ja/modals): Submit ボタンクリックによるデータ送信に応答します。|
|**app.viewClosed**|callback_id: **String** \| **Pattern**|[**モーダル**]({{ site.url | append: site.baseurl }}/guides/ja/modals): ユーザーがモーダルを閉じたときのイベントに応答します。そのモーダルを open/push したときに `notify_on_close` が `true` に設定されている必要があります。|
|**app.dialogSubmission**|callback_id: **String** \| **Pattern**|**ダイアログ**: ダイアログでのデータ送信に応答します。|
|**app.dialogSuggestion**|callback_id: **String** \| **Pattern**|**ダイアログ**: ダイアログ内での `external` type に設定されたセレクトメニューの選択肢読み込みのリクエストに応答します。|
|**app.dialogCancellation**|callback_id **String** \| **Pattern**|**ダイアログ**: ダイアログが閉じたときのイベントに応答します。|
|**app.attachmentAction**|callback_id: **String** \| **Pattern**|**旧式のメッセージ**: **attachements** 内で発生したユーザアクションに応答します。これらのイベントはメッセージのみで発火します。|

## 機能ごとの開発ガイド

以下のガイドページで、それぞれの機能について具体的なコード例を見つけることができます。

* [**スラッシュコマンド**]({{ site.url | append: site.baseurl }}/guides/ja/slash-commands)
* [**アクション**]({{ site.url | append: site.baseurl }}/guides/ja/actions)
* [**インタラクティブコンポーネント**]({{ site.url | append: site.baseurl }}/guides/ja/interactive-components)
* [**モーダル**]({{ site.url | append: site.baseurl }}/guides/ja/modals)
* [**Home タブ**]({{ site.url | append: site.baseurl }}/guides/ja/app-home)
* [**イベント API**]({{ site.url | append: site.baseurl }}/guides/ja/events-api)
* [**アプリの配布 (OAuth Flow)**]({{ site.url | append: site.baseurl }}/guides/ja/app-distribution)

## リクエストを ack する

アクション、コマンド、選択肢読み込みなどのイベントでのリクエストに対しては、必ず `ack()` メソッドで応答を返す必要があります。`ack()` に限らず、このようなユーティリティは全て **Context** オブジェクトのインスタンスメソッドとして定義されています。

```java
app.command("/hello", (req, ctx) -> { // 第二引数の ctx が Context 型です
  return ctx.ack() // 空ボディでの応答は、今回は何もリプライのメッセージを投稿しないという意思表示になります
});
```

アプリがユーザーアクションに対して、何かリプライになるメッセージを投稿したい場合は `ack()` メソッドに `text` として使用される文字列のメッセージを渡します。

```java
app.command("/ping", (req, ctx) -> {
  return ctx.ack(":wave: pong");
});
```

よりインタラクティブなメッセージを送るために [Block Kit](https://api.slack.com/block-kit) を使用することも可能です。

```java
import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;

app.command("/ping", (req, ctx) -> {
  return ctx.ack(asBlocks(
    section(section -> section.text(markdownText(":wave: pong"))),
    actions(actions -> actions
      .elements(asElements(
        button(b -> b.actionId("ping-again").text(plainText(pt -> pt.text("Ping"))).value("ping"))
      ))
    )
  ));
});
```

このような返信は、デフォルトではそのユーザにだけ見えるメッセージ（ephemeral message）として投稿されます。チャンネル内の他の人にも見えるメッセージとして投稿するには `in_channel` という種別を指定します。

```java
app.command("/ping", (req, ctx) -> {
  return ctx.ack(res -> res.responseType("in_channel").text(":wave: pong"));
});
```

## ユーザーアクションに respond する

`response_url` についてすでにご存知ですか？もしまだでしたら、まず「[Handling user interaction in your Slack apps > Message responses（英語）](https://api.slack.com/interactivity/handling#message_responses)」を読むことをおすすめします。

そのガイドページが説明しているように、一部のユーザーインタラクションによるペイロードは `repsonse_url` というプロパティを持っています。この `response_url` は、各ペイロードに一意な URL で、そのインタラクションが発生した場所（チャンネル）にメッセージを送信するために使うことができます。

上の `ack()` と似ていますが **Context** オブジェクトが受信した `response_url` を簡単に使うための `respond()` メソッドを提供しています。

```java
import com.slack.api.webhook.WebhookResponse;

app.command("/hello", (req, ctx) -> {
  // response_url を使ってメッセージを投稿
  WebhookResponse result = ctx.respond(res -> res
    .responseType("ephemeral") // または "in_channnel"
    .text("Hi there!") // 別の setter で blocks, attachments も使えます
  );
  return ctx.ack(); // この ack() はメッセージを投稿しない 
});
```

## Web API の利用

Web API を Bolt アプリ内で利用したいときは `ctx.client()` を使います。このメソッドが返す **MethodsClient** はあらかじめボットトークンを保持しています。そのため、トークンを渡す必要はありません。ただパラメーターを指定して呼び出すだけで OK です。

```java
app.command("/hello", (req, ctx) -> {
  // ctx.client() はすでにボットトークンを持っています
  ChatPostMessageResponse response = ctx.client().chatPostMessage(r -> r
    .channel("C1234567")
    .text(":wave: いつもお世話になっています！")
  );
  return ctx.ack();
});
```

ちなみに [**chat.postMessage**](https://api.slack.com/methods/chat.postMessage) API の呼び出しに限っては `say()` というユーティリティメソッドを使えば、より簡単になります。

```java
app.command("/hello", (req, ctx) -> {
  ChatPostMessageResponse response = ctx.say(":wave: いつもお世話になっています！");
  return ctx.ack();
});
```

ボットトークンではなく、リクエストしてきたユーザーのユーザートークンを使用したい場合はパラメーターとして指定することで上書きすることができます。

```java
import com.slack.api.methods.response.search.SearchMessagesResponse;

app.command("/my-search", (req, ctx) -> {
  String query = req.getPayload().getText();
  if (query == null || query.trim().size() == 0) {
    return ctx.ack("何か検索キーワードを指定してください :pray:");
  }

  String userToken = ctx.getRequestUserToken(); // これを使うには InstallationService を有効にする必要があります
  if (userToken != null) {
    SearchMessagesResponse response = ctx.client().searchMessages(r -> r
      .token(userToken) // ctx.client() にあらかじめ設定されていたボットトークンの代わりにこれを使います
      .query(query));
    if (response.isOk()) {
      String reply = "「" + query + "」で検索した結果 " + response.getMessages().getTotal() + " 件のメッセージがヒットしました";
      return ctx.ack(reply);
    } else {
      String reply = "「" + query + "」で検索したら「" + response.getError() + "」というエラーが発生しました";
      return ctx.ack(reply);
    }
  } else {
    return ctx.ack("この Slack アプリに検索を実行させるために権限を与えてください :pray:");
  }
});
```

## ロギング

**Context** オブジェクトから [SLF4J](http://www.slf4j.org/) のロガーにアクセスできます。

```java
app.command("/weather", (req, ctx) -> {
  String keyword = req.getPayload().getText();
  String userId = req.getPayload().getUserId();
  ctx.logger.info("Weather search by keyword: {} for user: {}", keyword, userId);
  return ctx.ack(weatherService.find(keyword).toMessage());
});
```

SLF4J の実装として [**ch.qos.logback:logback-classic**](https://search.maven.org/artifact/ch.qos.logback/logback-classic/1.2.3/jar) を使っている場合は [**logback.xml**](http://logback.qos.ch/manual/configuration.html) などの手段で設定をすることができます。

```xml
<configuration>
  <appender name="default" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%date %level [%thread] %logger{64} %msg%n</pattern>
    </encoder>
    </appender>
  <root level="debug">
    <appender-ref ref="default"/>
  </root>
</configuration>
```

## ミドルウェア

Bolt はチェインするミドルウェアの仕組みを提供しています。フィルターのような処理を全てのイベントに対して適用することで **App** の挙動をカスタマイズすることができます。

以下は、ミドルウェアがどのように動作するかを示すコード例です。このミドルウェアは `SLACK_APP_DEBUG_MODE` という環境変数が存在しているときだけ、アプリのエラーパターンの動作を統一的に変えています。

```java
import com.slack.api.bolt.App;
import com.slack.api.bolt.response.Response;
import com.slack.api.bolt.util.JsonOps;
import static java.util.stream.Collectors.joining;

class DebugResponseBody {
  String responseType; // ephemeral, in_channel
  String text;
}
String debugMode = System.getenv("SLACK_APP_DEBUG_MODE");

App app = new App();

if (debugMode != null && debugMode.equals("1")) { // SLACK_APP_DEBUG_MODE=1 という環境変数が設定されているときだけ動作する
  app.use((req, _resp, chain) -> {
    Response resp = chain.next(req);
    if (resp.getStatusCode() != 200) {
      resp.getHeaders().put("content-type", resp.getContentType());
      // 全てのヘッダーを一つの文字列としてダンプする
      String headers = resp.getHeaders().entrySet().stream()
        .map(e -> e.getKey() +  ": " + e.getValue() + "\n").collect(joining());

      // このユーザにだけ見えるメッセージにデバッグに役立つ情報を含める
      DebugResponseBody body = new DebugResponseBody();
      body.responseType = "ephemeral";
      body.text =
        ":warning: *[DEBUG MODE] Something is technically wrong* :warning:\n" +
        "Below is a response the Slack app was going to send...\n" +
        "*Status Code*: " + resp.getStatusCode() + "\n" +
        "*Headers*: ```" + headers + "```" + "\n" +
        "*Body*: ```" + resp.getBody() + "```";
      resp.setBody(JsonOps.toJsonString(body));

      resp.setStatusCode(200);
    }
    return resp;
  });
}
```

このミドルウェアは 404 Not Found として応答しようとしていたエラーのレスポンスを、デバッグに有用な情報を含めたそのユーザーにだけ見えるメッセージを投稿する 200 OK の応答に変えています。

<img src="{{ site.url | append: site.baseurl }}/assets/images/bolt-middleware.png" width="600" />


#### ミドルウェアの実行順序

Bolt に標準で組み込まれているミドルウェアはアプリ側で追加したカスタムのミドルウェアよりも先に実行されます。もし、標準のミドルウェアが何かを検知して `chain.next(req)` の呼び出しを停止した場合、後続のミドルウェアは呼ばれません。

最もよくあるパターンは **RequestVerification** ミドルウェアでリクエストが拒否される場合です。この拒否のあとは、他のどのミドルウェアも実行されないため、上記のサンプル例のミドルウェアも同様に動作しません。

## 対応している Web フレームワーク

[こちらのページ]({{ site.url | append: site.baseurl }}/guides/ja/supported-web-frameworks)を参考にしてください。

## デプロイ

デプロイについてのガイドを公開することを[予定しています](https://github.com/slackapi/java-slack-sdk/issues/348)。
