# jms-message-selector test

regarding https://github.com/rabbitmq/rabbitmq-server/issues/4615

usage:
adjust RmqTestClient.RMQ_HOST_STRING to target your RabbitMQ instance.

gradle jar target will build a fat runnable jar if you'd like to run outside IDE.  A S(subscriber) or P(ublisher) param can be passed to the process to run as a publisher or subscriber.


Spin up two subscriber processes, ie 2x:
java -jar rmq-jms-msg-selector-test-1.0-SNAPSHOT.jar S

Spin up a publisher process
java -jar rmq-jms-msg-selector-test-1.0-SNAPSHOT.jar P

You should see the publishing process fail shortly after subscribers are started, with the below logged in the console:
exchange named 'jms-ndtop-slx-[uid]' has no saved state or incorrect saved state.

The above error is coming from the subscriber process disconnecting, and the specified exchange is not the exchange being published to (jms.durable.topic) by the publisher when the publisher is shut down over this error.

