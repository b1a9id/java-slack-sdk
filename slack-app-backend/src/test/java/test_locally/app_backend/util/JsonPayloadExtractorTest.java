package test_locally.app_backend.util;

import com.google.gson.Gson;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.util.JsonPayloadExtractor;
import com.slack.api.util.json.GsonFactory;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JsonPayloadExtractorTest {

    // app_home_opened event payload
    String requestBody = "{\"token\":\"CtDotjxvwc3kQlHEc9jv2rfd\",\"team_id\":\"T12345678\",\"api_app_id\":\"A12345678\",\"event\":{\"type\":\"app_home_opened\",\"user\":\"U12345678\",\"channel\":\"C12345678\",\"tab\":\"home\",\"view\":{\"id\":\"VQQ8XSC1K\",\"team_id\":\"T12345678\",\"type\":\"home\",\"blocks\":[{\"type\":\"section\",\"block_id\":\"F17w9\",\"text\":{\"type\":\"mrkdwn\",\"text\":\"*Budget Performance*\",\"verbatim\":false},\"accessory\":{\"type\":\"button\",\"text\":{\"type\":\"plain_text\",\"text\":\"Manage App Settings\",\"emoji\":true},\"value\":\"app_settings\",\"action_id\":\"UECO4\"}},{\"type\":\"divider\",\"block_id\":\"e38\"},{\"type\":\"section\",\"block_id\":\"O\\/wND\",\"fields\":[{\"type\":\"mrkdwn\",\"text\":\"*Current Quarter*\\nBudget: $18,000 (ends in 53 days)\\nSpend: $4,289.70\\nRemain: $13,710.30\",\"verbatim\":false},{\"type\":\"mrkdwn\",\"text\":\"*Top Expense Categories*\\n:airplane: Flights \\u00b7 30%\\n:taxi: Taxi \\/ Uber \\/ Lyft \\u00b7 24% \\n:knife_fork_plate: Client lunch \\/ meetings \\u00b7 18%\",\"verbatim\":false}]},{\"type\":\"context\",\"block_id\":\"J45ny\",\"elements\":[{\"fallback\":\"20x20px image\",\"image_url\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/block-kit-modals.appspot.com\\/o\\/placeholder.png?alt=media&token=04c57315-b39c-4ac8-bca4-b0371c5c95f2\",\"image_width\":20,\"image_height\":20,\"image_bytes\":573,\"type\":\"image\",\"alt_text\":\"placeholder\"}]},{\"type\":\"section\",\"block_id\":\"aJEn\",\"text\":{\"type\":\"mrkdwn\",\"text\":\"*Expenses Awaiting Your Approval*\",\"verbatim\":false}},{\"type\":\"divider\",\"block_id\":\"YLQ\"},{\"type\":\"context\",\"block_id\":\"6o=R6\",\"elements\":[{\"type\":\"mrkdwn\",\"text\":\"Submitted by\",\"verbatim\":false},{\"fallback\":\"40x40px image\",\"image_url\":\"https:\\/\\/api.slack.com\\/img\\/blocks\\/bkb_template_images\\/profile_3.png\",\"image_width\":40,\"image_height\":40,\"image_bytes\":2470,\"type\":\"image\",\"alt_text\":\"Dwight Schrute\"},{\"type\":\"mrkdwn\",\"text\":\"*Dwight Schrute*\",\"verbatim\":false}]},{\"type\":\"section\",\"block_id\":\"vvC\",\"text\":{\"type\":\"mrkdwn\",\"text\":\"*Team Lunch (Internal)*\\nCost: *$85.50USD*\\nDate: *10\\/16\\/2019*\\nService Provider: *Honest Sandwiches*  \\nExpense no. *<fakelink.toUrl.com|#1797PD>*\",\"verbatim\":false},\"accessory\":{\"fallback\":\"88x88px image\",\"image_url\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/block-kit-modals.appspot.com\\/o\\/creditcard.png?alt=media&token=5370ece9-2a9a-4dd1-8235-68ec68c47c6c\",\"image_width\":88,\"image_height\":88,\"image_bytes\":1019,\"type\":\"image\",\"alt_text\":\"credit card\"}},{\"type\":\"actions\",\"block_id\":\"1RHCr\",\"elements\":[{\"type\":\"button\",\"action_id\":\"aHkyD\",\"text\":{\"type\":\"plain_text\",\"text\":\"Approve\",\"emoji\":true},\"style\":\"primary\",\"value\":\"approve\"},{\"type\":\"button\",\"action_id\":\"DUf\",\"text\":{\"type\":\"plain_text\",\"text\":\"Decline\",\"emoji\":true},\"style\":\"danger\",\"value\":\"decline\"},{\"type\":\"button\",\"action_id\":\"abXZc\",\"text\":{\"type\":\"plain_text\",\"text\":\"View Details\",\"emoji\":true},\"value\":\"details\"}]},{\"type\":\"divider\",\"block_id\":\"sew2\"},{\"type\":\"context\",\"block_id\":\"c=z\",\"elements\":[{\"type\":\"mrkdwn\",\"text\":\"Submitted by\",\"verbatim\":false},{\"fallback\":\"40x40px image\",\"image_url\":\"https:\\/\\/api.slack.com\\/img\\/blocks\\/bkb_template_images\\/profile_2.png\",\"image_width\":40,\"image_height\":40,\"image_bytes\":4013,\"type\":\"image\",\"alt_text\":\"Pam Beasely\"},{\"type\":\"mrkdwn\",\"text\":\"*Pam Beasely*\",\"verbatim\":false}]},{\"type\":\"section\",\"block_id\":\"\\/9Z\",\"text\":{\"type\":\"mrkdwn\",\"text\":\"*Flights to New York*\\nCost: *$520.78USD*\\nDate: *10\\/18\\/2019*\\nService Provider: *Delta Airways*\\nExpense no. *<fakelink.toUrl.com|#1803PD>*\",\"verbatim\":false},\"accessory\":{\"fallback\":\"88x88px image\",\"image_url\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/block-kit-modals.appspot.com\\/o\\/plane.png?alt=media&token=383e45cd-51dc-426a-948e-0a5ce816a9df\",\"image_width\":88,\"image_height\":88,\"image_bytes\":7355,\"type\":\"image\",\"alt_text\":\"plane\"}},{\"type\":\"actions\",\"block_id\":\"g35A\\/\",\"elements\":[{\"type\":\"button\",\"action_id\":\"YqOsI\",\"text\":{\"type\":\"plain_text\",\"text\":\"Approve\",\"emoji\":true},\"style\":\"primary\",\"value\":\"approve\"},{\"type\":\"button\",\"action_id\":\"Ws1WT\",\"text\":{\"type\":\"plain_text\",\"text\":\"Decline\",\"emoji\":true},\"style\":\"danger\",\"value\":\"decline\"},{\"type\":\"button\",\"action_id\":\"gfnG\",\"text\":{\"type\":\"plain_text\",\"text\":\"View Details\",\"emoji\":true},\"value\":\"details\"}]}],\"private_metadata\":\"\",\"callback_id\":\"\",\"state\":{\"values\":{}},\"hash\":\"1574168179.58d774d3\",\"title\":{\"type\":\"plain_text\",\"text\":\"View Title\",\"emoji\":true},\"clear_on_close\":false,\"notify_on_close\":false,\"close\":null,\"submit\":null,\"previous_view_id\":null,\"root_view_id\":\"VQQ8XSC1K\",\"app_id\":\"A12345678\",\"external_id\":\"\",\"app_installed_team_id\":\"T12345678\",\"bot_id\":\"B12345678\"}},\"type\":\"event_callback\",\"event_id\":\"EvQS5ZLDJT\",\"event_time\":1574169232}";

    Gson gson = GsonFactory.createSnakeCase();
    JsonPayloadExtractor extractor = new JsonPayloadExtractor();

    @Test
    public void testNull() {
        String nullValue = extractor.extractIfExists(null);
        assertNull(nullValue);
    }

    @Test
    public void testEmpty() {
        String nullValue = extractor.extractIfExists("");
        assertNull(nullValue);
    }

    @Test
    public void testWhitespace() {
        String nullValue = extractor.extractIfExists("  ");
        assertNull(nullValue);
    }

    @Test
    public void eventApi() {
        String json = extractor.extractIfExists(requestBody);
        assertThat(json, is(requestBody));
    }

    @Test
    public void eventApi_invalid() {
        String invalid = extractor.extractIfExists("{}foo");
        assertNull(invalid);
    }

    @Test
    public void payload() throws UnsupportedEncodingException {
        BlockActionPayload payload = new BlockActionPayload();
        payload.setTriggerId("xxxx");
        String stringPayload = gson.toJson(payload);
        String json = extractor.extractIfExists("payload=" + URLEncoder.encode(stringPayload, "UTF-8"));
        assertEquals(stringPayload, json);
    }

}
