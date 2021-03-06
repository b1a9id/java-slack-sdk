package com.slack.api.bolt.context.builtin;

import com.slack.api.app_backend.outgoing_webhooks.response.WebhookResponse;
import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.context.SayUtility;
import com.slack.api.bolt.response.Response;
import com.slack.api.bolt.util.BuilderConfigurator;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class OutgoingWebhooksContext extends Context implements SayUtility {

    private String triggerId;
    private String channelId;

    public Response ack(String text) {
        return Response.json(200, WebhookResponse.builder().text(text).build());
    }

    public Response ack(WebhookResponse response) {
        return Response.json(200, response);
    }

    public Response ack(
            BuilderConfigurator<WebhookResponse.WebhookResponseBuilder> builder) {
        return ack(builder.configure(WebhookResponse.builder()).build());
    }

}
