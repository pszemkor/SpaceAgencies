package model;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Administrator {
    private final String administrativeExchangeName;
    private final String commonExchangeName;

    public Administrator(String administrativeExchangeName, String commonExchangeName) {
        this.administrativeExchangeName = administrativeExchangeName;
        this.commonExchangeName = commonExchangeName;
    }

    public void init() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(commonExchangeName, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, commonExchangeName, "#");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received copy of message: " + message);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}