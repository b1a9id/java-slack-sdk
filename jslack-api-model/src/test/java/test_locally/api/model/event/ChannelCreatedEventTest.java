package test_locally.api.model.event;

import com.github.seratch.jslack.api.model.event.ChannelCreatedEvent;
import com.google.gson.Gson;
import org.junit.Test;
import test_locally.unit.GsonFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChannelCreatedEventTest {

    @Test
    public void typeName() {
        assertThat(ChannelCreatedEvent.TYPE_NAME, is("channel_created"));
    }

    @Test
    public void deserialize() {
        String json = "{\n" +
                "    \"type\": \"channel_created\",\n" +
                "    \"channel\": {\n" +
                "        \"id\": \"C024BE91L\",\n" +
                "        \"name\": \"fun\",\n" +
                "        \"created\": 1360782804,\n" +
                "        \"creator\": \"U024BE7LH\"\n" +
                "    }\n" +
                "}";
        ChannelCreatedEvent event = GsonFactory.createSnakeCase().fromJson(json, ChannelCreatedEvent.class);
        assertThat(event.getType(), is("channel_created"));
        assertThat(event.getChannel(), is(notNullValue()));
        assertThat(event.getChannel().getId(), is("C024BE91L"));
        assertThat(event.getChannel().getName(), is("fun"));
        assertThat(event.getChannel().getCreated(), is(1360782804));
        assertThat(event.getChannel().getCreator(), is("U024BE7LH"));
    }

    @Test
    public void serialize() {
        Gson gson = GsonFactory.createSnakeCase();
        ChannelCreatedEvent event = new ChannelCreatedEvent();
        event.setChannel(new ChannelCreatedEvent.Channel());
        String generatedJson = gson.toJson(event);
        String expectedJson = "{\"type\":\"channel_created\",\"channel\":{}}";
        assertThat(generatedJson, is(expectedJson));
    }

}
