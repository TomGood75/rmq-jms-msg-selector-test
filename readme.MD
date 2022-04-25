# jms-message-selector test

regarding https://github.com/rabbitmq/rabbitmq-server/issues/4615

- all code is in the relatively short com.RmqTestClient class.

UPDATED:
- runnable jar is available in build/libs, so no need to compile if preferred.  RMQ URI can be passed as
  - java -jar rmq-jms-msg-selector-test-1.0-SNAPSHOT.jar {S|P} amqp://lme:lme@atscmn-lme-rmq-prd-01:5672/test 

usage:
adjust RmqTestClient.RMQ_HOST_STRING to target your RabbitMQ instance.

The gradle jar target will build a fat runnable jar if you'd like to run outside IDE.  A 'S'(ubscriber) or 'P'(ublisher) param can be passed to the process to run the client as a publisher or subscriber.
Otherwise you can just flip in the main method which mode the process will run as, and run multiple instances of RmqTestClient in your IDE.

Spin up two subscriber processes, ie 2x:
java -jar rmq-jms-msg-selector-test-1.0-SNAPSHOT.jar S

Spin up a publisher process
java -jar rmq-jms-msg-selector-test-1.0-SNAPSHOT.jar P

You should see the publishing process fail shortly after subscribers are started, with the below logged in the console:
exchange named 'jms-ndtop-slx-[uid]' has no saved state or incorrect saved state.

The above error is coming from the subscriber process disconnecting, and the specified exchange is not the exchange being published to (jms.durable.topic) by the publisher when the publisher is shut down over this error.

The subscriber-mode clients will subscribe to the topic with a message selector, wait three seconds, close the connection, wait three seconds, and repeat.

The publisher-mode client will just keep publishing as fast as possible, to increase the likelyhood of running into the timing issue of a subscriber leaving causing an error.

