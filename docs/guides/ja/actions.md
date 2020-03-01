---
layout: ja
title: "アクション"
lang: ja
---

# アクション

[アクション](https://api.slack.com/interactivity/actions)は、ユーザーがバグのレポート、休暇の申請、ミーティングの開始などのタスクをスピーディに終わらせるためのシンプルなショートカットです。

2020 年 3 月現在、この SDK がサポートしている機能は以下の通りです（[近い将来に追加予定](https://medium.com/slack-developer-blog/introducing-the-slack-app-toolkit-3d509a15f41b)）。

* [メッセージアクション](https://api.slack.com/interactive-messages)

アクションリクエストの全てのタイプで Slack アプリは 3 秒以内に `ack()` メソッドで返答する必要があります。そうでなければ、Slack 上でユーザーにタイムアウトした旨が通知されます。

## メッセージアクション

#### Slack アプリの設定

メッセージアクションを有効にするには [Slack アプリ管理画面](http://api.slack.com/apps)にアクセスし、開発中のアプリを選択、左ペインの **Features** > **Interactive Components** へ遷移します。このページで以下の設定を行います。

* **Interactivity** を Off から On にする
* `https://{あなたのドメイン}/slack/events` を **Request URL** に設定
* **Actions** セクションでアクションを設定
  * **Action Name**, **Short Description**, **Callback ID**
* 最下部にある **Save Changes** ボタンをクリック

<img src="{{ site.url | append: site.baseurl }}/assets/images/bolt-actions.png" width="400" />

指定された **Callback ID** は Slack API からのペイロードの中で `callback_id` として送信されます。

#### Bolt アプリがやること

Bolt アプリがメッセージアクションへの応答のためにやらなければならないことは以下の通りです。

1. Slack API からのリクエストを[検証](https://api.slack.com/docs/verifying-requests-from-slack)
1. リクエストボディをパースして `callback_id` が処理対象か確認
1. 返信メッセージを組み立てるなどメインの処理を実行
1. 受け取ったことを伝えるために Slack API へ 200 OK 応答

### コード例

**注**: もし Bolt を使った Slack アプリ開発にまだ慣れていない方は、まず「[Bolt ことはじめ]({{ site.url | append: site.baseurl }}/guides/ja/getting-started-with-bolt)」を読んでください。

Bolt は Slack アプリに共通で必要となる多くをやってくれます。それを除いて、あなたのアプリがやらなければならない手順は以下の通りです。

* 処理する `callback_id` を指定 (そのコマンドの名前、または正規表現)
* メッセージを組み立てるなどメインの処理の実装
* 受け取ったことを伝えるために `ack()`

このペイロードは `request_url` を持っており、例えば `ack()` した後、しばらく経ってからでも返信することができます。URL は発行されてから 30 分間を期限に最大 5 回まで使用することができます。処理が終わったタイミングで `respone_url` を使って返信する場合は `ctx.ack()` は引数なしで実行し `ctx.respond()` でメッセージを投稿する、というやり方になります。

以下のサンプルは、メッセージアクションのリクエストに応答する実装の例です。

```java
import com.slack.api.model.Message;
import com.slack.api.model.view.View;
import com.slack.api.methods.response.views.ViewsOpenResponse;

app.messageAction("create-task-action-callback-id", (req, ctx) -> {
  String userId = req.getPayload().getUser().getId();
  Message message = req.getPayload().getMessage();
  // そのメッセージを使ってここで何かする

  ViewsOpenResponse viewsOpenResp = ctx.client().viewsOpen(r -> r
    .triggerId(ctx.getTriggerId())
    .view(buildView(message)));
  if (!viewsOpenResp.isOk()) {
    String errorCode = viewsOpenResp.getError();
    ctx.logger.error("Failed to open a modal view for user: {} - error: {}", userId, errorCode);
    ctx.respond(":x: " + errorCode +  "というエラーでモーダルを開ませんでした");
  }

  return ctx.ack(); // 受け取ったことを伝えるために Slack API へ 200 OK 応答
});

View buildView(Message message) {
  return null; // TODO
}
```

同じコードを Kotlin で書くと以下のようになります（参考：「[Bolt ことはじめ > Koltin での設定]({{ site.url | append: site.baseurl }}/guides/ja/getting-started-with-bolt#getting-started-in-kotlin)」）。

```kotlin
app.messageAction("create-task-action-callback-id") { req, ctx ->
  val userId = req.payload.user.id
  val message = req.payload.message
  // そのメッセージを使ってここで何かする

  val viewsOpenResp = ctx.client().viewsOpen {
    it.triggerId(ctx.triggerId)
      .view(buildView(message))
  }
  if (!viewsOpenResp.isOk) {
    val errorCode = viewsOpenResp.error
    ctx.logger.error("Failed to open a modal view for user: ${userId} - error: ${errorCode}")
    ctx.respond(":x: ${errorCode} というエラーでモーダルを開ませんでした")
  }

  ctx.ack() // 受け取ったことを伝えるために Slack API へ 200 OK 応答
}
```

### Bolt がやっていること

上記のコードによって実際に何が起きているのかに興味があるなら、以下の擬似コードを読んでみるとわかりやすいかもしれません。

```java
import java.util.Map;
import com.google.gson.Gson;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.payload.MessageActionPayload;
import com.slack.api.util.json.GsonFactory;

PseudoHttpResponse handle(PseudoHttpRequest request) {

  // 1. Slack からのリクエストを検証
  // https://api.slack.com/docs/verifying-requests-from-slack
  // "X-Slack-Signature" header, "X-Slack-Request-Timestamp" ヘッダーとリクエストボディを検証
  if (!PseudoSlackRequestVerifier.isValid(request)) {
    return PseudoHttpResponse.builder().status(401).build();
  }

  // 2. リクエストボディをパースして `callback_id` が処理対象か確認

  // リクエストボディは payload={URL エンコードされた JSON 文字列} の形式
  String payloadString = PseudoPayloadExtractor.extract(request.getBodyAsString());
  Gson gson = GsonFactory.createSnakeCase();
  MessageActionPayload payload = gson.fromJson(payloadString, MessageActionPayload.class);
  if (payload.getCallbackId().equals("create-task-action-callback-id")) {
    // 3. 返信メッセージを組み立てるなどメインの処理を実行
  }

  // 4. 受け取ったことを伝えるために Slack API へ 200 OK 応答
  return PseudoHttpResponse.builder().status(200).build();
}
```
